package com.artifex.mupdfdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class MuPDFReaderView extends ReaderView {
	public enum Mode {Viewing, Selecting, Drawing}
	private final Context mContext;
	private boolean mLinksEnabled = false;
	private Mode mMode = Mode.Viewing;
	private boolean tapDisabled = false;
	private int tapPageMargin;
	
	//{{ 2013.6.8 Suhwan Hwang : 페이지 이동 추가 
	private onPageChagedListener mPageChangedListener;
	
	public interface onPageChagedListener {
	    void onPageChanged(int page);
	}
	public void setOnPageChangedListener(onPageChagedListener listener) {
	    mPageChangedListener = listener;
	}
	//}}

	protected void onTapMainDocArea() {}
	protected void onDocMotion() {}
	protected void onHit(Hit item) {};

	public void setLinksEnabled(boolean b) {
		mLinksEnabled = b;
		resetupChildren();
	}

	public void setMode(Mode m) {
		mMode = m;
	}

	public MuPDFReaderView(Context act) {
		super(act);
		mContext = act;
		// Get the screen size etc to customise tap margins.
		// We calculate the size of 1 inch of the screen for tapping.
		// On some devices the dpi values returned are wrong, so we
		// sanity check it: we first restrict it so that we are never
		// less than 100 pixels (the smallest Android device screen
		// dimension I've seen is 480 pixels or so). Then we check
		// to ensure we are never more than 1/5 of the screen width.
//		DisplayMetrics dm = new DisplayMetrics();
//		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		tapPageMargin = (int)dm.xdpi;
//		if (tapPageMargin < 100)
//			tapPageMargin = 100;
//		if (tapPageMargin > dm.widthPixels/5)
//			tapPageMargin = dm.widthPixels/5;
		tapPageMargin = 0;
	}

	public MuPDFReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		tapPageMargin = 0;
	}
	
	public MuPDFReaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		tapPageMargin = 0;
	}
	
	public boolean onSingleTapUp(MotionEvent e) {
		LinkInfo link = null;

		if (mMode == Mode.Viewing && !tapDisabled) {
			MuPDFView pageView = (MuPDFView) getDisplayedView();
			Hit item = pageView.passClickEvent(e.getX(), e.getY());
			onHit(item);
			if (item == Hit.Nothing) {
				if (mLinksEnabled && pageView != null
				&& (link = pageView.hitLink(e.getX(), e.getY())) != null) {
					link.acceptVisitor(new LinkInfoVisitor() {
						@Override
						public void visitInternal(LinkInfoInternal li) {
							// Clicked on an internal (GoTo) link
							setDisplayedViewIndex(li.pageNumber);
						}

						@Override
						public void visitExternal(LinkInfoExternal li) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(li.url));
							mContext.startActivity(intent);
						}

						@Override
						public void visitRemote(LinkInfoRemote li) {
							// Clicked on a remote (GoToR) link
						}
					});
				} else if (e.getX() < tapPageMargin) {
					super.smartMoveBackwards();
				} else if (e.getX() > super.getWidth() - tapPageMargin) {
					super.smartMoveForwards();
				} else if (e.getY() < tapPageMargin) {
					super.smartMoveBackwards();
				} else if (e.getY() > super.getHeight() - tapPageMargin) {
					super.smartMoveForwards();
				} else {
					onTapMainDocArea();
				}
			}
		}
		return super.onSingleTapUp(e);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		switch (mMode) {
		case Drawing:
			MuPDFView pageView = (MuPDFView)getDisplayedView();
			if (pageView != null)
				pageView.startDraw(e.getX(), e.getY());
			break;
		}
		return super.onDown(e);
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		MuPDFView pageView = (MuPDFView)getDisplayedView();
		switch (mMode) {
		case Viewing:
			if (!tapDisabled)
				onDocMotion();

			return super.onScroll(e1, e2, distanceX, distanceY);
		case Selecting:
			if (pageView != null)
				pageView.selectText(e1.getX(), e1.getY(), e2.getX(), e2.getY());
			return true;
		case Drawing:
			if (pageView != null)
				pageView.continueDraw(e2.getX(), e2.getY());
			return true;
		default:
			return true;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		switch (mMode) {
		case Viewing:
			return super.onFling(e1, e2, velocityX, velocityY);
		default:
			return true;
		}
	}

	public boolean onScaleBegin(ScaleGestureDetector d) {
		// Disabled showing the buttons until next touch.
		// Not sure why this is needed, but without it
		// pinch zoom can make the buttons appear
		tapDisabled = true;
		return super.onScaleBegin(d);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if ((event.getAction() & event.ACTION_MASK) == MotionEvent.ACTION_DOWN)
			tapDisabled = false;

		return super.onTouchEvent(event);
	}

	protected void onChildSetup(int i, View v) {
//13.06.05 Test 결과 탐색 창과 관련 있어 없어도 괜찮은 것 같음.(있으면 Search 관련 java 추가해야 함
		//		if (SearchTaskResult.get() != null
//				&& SearchTaskResult.get().pageNumber == i)
//			((MuPDFView) v).setSearchBoxes(SearchTaskResult.get().searchBoxes);
//		else
//			((MuPDFView) v).setSearchBoxes(null);

		((MuPDFView) v).setLinkHighlighting(mLinksEnabled);

		((MuPDFView) v).setChangeReporter(new Runnable() {
			public void run() {
				applyToChildren(new ReaderView.ViewMapper() {
					@Override
					void applyToView(View view) {
						((MuPDFView) view).update();
					}
				});
			}
		});
	}

	protected void onMoveToChild(int i) {
//13.06.05 Test 결과 탐색 창과 관련 있어 없어도 괜찮은 것 같음.(있으면 Search 관련 java 추가해야 함.
// 주석 시 상위 탐색 바 보이지 않음.
		//		if (SearchTaskResult.get() != null
//				&& SearchTaskResult.get().pageNumber != i) {
//			SearchTaskResult.set(null);
//			resetupChildren();
//		}
	    
	    // 2013.6.8 Suhwan Hwang : 페이지 전환 콜백 추가 
	    if (mPageChangedListener != null) {
	        mPageChangedListener.onPageChanged(i);
	    }
	}

	@Override
	protected void onMoveOffChild(int i) {
		View v = getView(i);
		if (v != null)
			((MuPDFView)v).deselectAnnotation();
	}

	protected void onSettle(View v) {
		// When the layout has settled ask the page to render
		// in HQ
		((MuPDFView) v).addHq(false);
	}

	protected void onUnsettle(View v) {
		// When something changes making the previous settled view
		// no longer appropriate, tell the page to remove HQ
		((MuPDFView) v).removeHq();
	}

	@Override
	protected void onNotInUse(View v) {
		((MuPDFView) v).releaseResources();
	}

	@Override
	protected void onScaleChild(View v, Float scale) {
		((MuPDFView) v).setScale(scale);
	}
}
