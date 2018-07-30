package com.meizu.flyme.launcher;

import com.meizu.flyme.launcher.IExternalCallback;

interface IExternalService {
    Bundle takeScreenshot();
    void scaleHome(float scale);
    void snapHome(boolean isFirstScreen);
    String getAppsUsedRecord();
    void setExternalCallback(in IExternalCallback callback);
}