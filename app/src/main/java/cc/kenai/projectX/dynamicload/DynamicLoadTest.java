package cc.kenai.projectX.dynamicload;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;


public class DynamicLoadTest extends Activity{
    @Override
    protected void onResume() {
        super.onResume();

        demoDynamicLoad();

    }

    //dx.bat --dex --output test.jar C:\Users\kenai\Documents\workspace\my\ProjectX\animationdebug\libs\test.jar  && adb push test.jar sdcard/test.jar
    public void demoDynamicLoad() {
        final File optimizedDexOutputPath = new File("sdcard/test.jar");
        File dexOutputDir = getDir("dex", 0);
        DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                dexOutputDir.getAbsolutePath(), null, getClassLoader());
        Class libProviderClazz = null;

        try {
            libProviderClazz = cl.loadClass("cc.kenai.dynamic.MusicControlTest");
            Method inject = libProviderClazz.getMethod("inject", Context.class);
            inject.invoke(null,this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
