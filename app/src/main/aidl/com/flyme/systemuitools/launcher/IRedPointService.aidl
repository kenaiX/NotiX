package com.flyme.systemuitools.launcher;

import com.flyme.systemuitools.launcher.IRedPointCallback;

interface IRedPointService {
    int getVersion();
    String getRedPointList();
    void markRedPointList(String delete);
    void setListener(IRedPointCallback callback);
}
