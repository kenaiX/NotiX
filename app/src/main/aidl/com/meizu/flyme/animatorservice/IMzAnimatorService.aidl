package com.meizu.flyme.animatorservice;

interface IMzAnimatorService { 
    void setKeyguardToLauncherAnimator(float flingerY);
    void setFingerprintUnlockToLauncherAnimator();
    void setSmartWallpaperAnimator(float flingerY);
    void hideLauncher();
    void showLauncherAnimation();
}