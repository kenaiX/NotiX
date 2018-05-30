package com.flyme.systemui.recents;

interface IRecentsRemoteService {
    void lockPkgTemporarily(String pkgName, int state/*1:lock 0:unlock*/);
}
