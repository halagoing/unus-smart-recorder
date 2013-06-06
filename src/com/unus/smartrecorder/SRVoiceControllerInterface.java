package com.unus.smartrecorder;

import android.app.Dialog;
import android.os.Bundle;

public interface SRVoiceControllerInterface {
    public Dialog createDialog(int id);
    public void prepareDialog(int id, Dialog dialog, Bundle args);
    
    public void record();
}
