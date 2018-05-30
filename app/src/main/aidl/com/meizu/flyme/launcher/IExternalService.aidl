package com.meizu.flyme.launcher;

interface IExternalService {
    Bundle takeScreenshot();
    void scaleHome(float scale);
    void snapHome();
    String getAppsUsedRecord();
}