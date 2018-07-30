package com.meizu.flyme.launcher;

interface IExternalService {
    Bundle takeScreenshot();
    void scaleHome(float scale);
    void snapHome(boolean isFirstScreen);
    String getAppsUsedRecord();
}