package cc.kenai.test.animaiton;

import android.graphics.Matrix;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

public class AnimationDebug {
    private static final float LAUNCHER_SCALE = 0.95f;

    //------------SCALE UP  eg.Open app from Launcher------------
    //Icon
    private static final long SCALE_UP_ICON_DURATION = 400;
    private static final PathInterpolator sScaleUpIconInterpolator = new PathInterpolator(0.30f, 0.65f, 0.10f, 1.00f);

    //Enter eg.Other app animaiton.
    private static final long SCALE_UP_ENTER_DURATION = 400;
    private static final long SCALE_UP_ENTER_ALPHA_DURATION = 64;
    private static final PathInterpolator sScaleUpEnterInterpolator = new PathInterpolator(0.30f, 0.65f, 0.10f, 1.00f);

    //Exit eg.Launcher animation.
    private static final long SCALE_UP_EXIT_DURATION = 400;
    private static final PathInterpolator sScaleUpExitInterpolator = new PathInterpolator(0.33f, 0.00f, 0.67f, 1.00f);


    //------------SCALE DOWN  eg.Exit app to Launcher------------
    //Icon
    private static final long SCALE_DOWN_ICON_DURATION = 400;
    private static final PathInterpolator sScaleDownIconInterpolator = new PathInterpolator(0.30f, 0.65f, 0.10f, 1.00f);

    //Enter eg.Launcher animation.
    private static final long SCALE_DOWN_ENTER_DURATION = 400;
    private static final PathInterpolator sScaleDownEnterInterpolator = sScaleUpExitInterpolator;

    //Exit eg.Other app animaiton.
    private static final long SCALE_DOWN_EXIT_DURATION = 400;
    private static final long SCALE_DOWN_EXIT_ALPHA_DURATION = 164;
    private static final PathInterpolator sScaleDownExitInterpolator = sScaleUpEnterInterpolator;


    //图标放大
    public static AnimationSet debugCreateThumbnailAnimationScaleUp(int appWidth, int appHeight, float scaleW, float scaleH, float launcherPivotX, float launcherPivotY, float translateX, float translateY) {
        Animation scale = new ScaleAnimation(1, 1 / scaleW, 1, 1 / scaleW,
                launcherPivotX,
                launcherPivotY);
        scale.setDuration(SCALE_UP_ICON_DURATION);
        scale.setInterpolator(sScaleUpIconInterpolator);

        Animation alpha = new AlphaAnimation(0, 0);
        alpha.setDuration(SCALE_UP_ICON_DURATION);
        alpha.setInterpolator(sScaleUpIconInterpolator);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(scale);
        set.addAnimation(alpha);
        set.setFillAfter(false);
        set.initialize(appWidth, appHeight, appWidth, appHeight);
        set.setZAdjustment(Animation.ZORDER_BOTTOM);

        return set;
    }
    //图标缩小
    public static  AnimationSet debugCreateThumbnailAnimationScaleDown(int appWidth, int appHeight, float scaleW, float scaleH, float launcherPivotX, float launcherPivotY,float translateX,float translateY){
        Animation scaleX = new ScaleXAnimation(1 / scaleW, 1, 1, 1,
                launcherPivotX,
                0);
        scaleX.setDuration(SCALE_DOWN_ICON_DURATION);
        scaleX.setInterpolator(sScaleDownIconInterpolator);

        Animation scaleY = new ScaleYAnimation(1, 1, 1 / scaleW, 1,
                0,
                launcherPivotY);
        scaleY.setDuration(SCALE_DOWN_ICON_DURATION);
        scaleY.setInterpolator(sScaleDownIconInterpolator);


        Animation alpha2 = new AlphaAnimation(0, 1);
        alpha2.setStartOffset(SCALE_DOWN_ICON_DURATION/3 - 100 );
        alpha2.setDuration(SCALE_DOWN_ICON_DURATION - SCALE_DOWN_ICON_DURATION/3);
        alpha2.setInterpolator(sScaleDownIconInterpolator);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(scaleX);
        set.addAnimation(scaleY);
        //set.addAnimation(alpha1);
        set.addAnimation(alpha2);


        set.setFillAfter(false);
        set.initialize(appWidth, appHeight, appWidth, appHeight);
        set.setZAdjustment(Animation.ZORDER_BOTTOM);

        return set;
    }


    //THUMBNAIL_TRANSITION_ENTER_SCALE_UP
    public static AnimationSet debugCreateAnimation1(int appWidth, int appHeight, float scaleW, float scaleH, float launcherPivotX, float launcherPivotY,float translateX,float translateY){
        AnimationSet set = new AnimationSet(false);
        Animation scale = new ScaleAnimation(scaleW, 1, scaleH, 1,
                launcherPivotX,
                launcherPivotY);
        scale.setDuration(SCALE_UP_ENTER_DURATION);
        scale.setInterpolator(sScaleUpEnterInterpolator);

        Animation alpha = new AlphaAnimation(0f, 1f);
        alpha.setDuration(SCALE_UP_ENTER_ALPHA_DURATION);
        alpha.setInterpolator(sScaleUpEnterInterpolator);

        set.addAnimation(scale);
        set.addAnimation(alpha);
        set.setFillAfter(true);
        set.initialize(appWidth, appHeight, appWidth, appHeight);
        set.setZAdjustment(Animation.ZORDER_TOP);
        return set;
    }

    //THUMBNAIL_TRANSITION_EXIT_SCALE_UP
    public static AnimationSet debugCreateAnimation2(int appWidth, int appHeight, float scaleW, float scaleH, float launcherPivotX, float launcherPivotY,float translateX,float translateY){
        AnimationSet set = new AnimationSet(false);
        Animation scale = new ScaleAnimation(1, LAUNCHER_SCALE, 1, LAUNCHER_SCALE, launcherPivotX,
                launcherPivotY);
        scale.setDuration(SCALE_UP_EXIT_DURATION);
        scale.setInterpolator(sScaleUpExitInterpolator);
        //scale.setInterpolator(sScaleInterpolator);
        set.addAnimation(scale);
        //set.addAnimation(alpha);
        //set.addAnimation(translate);
        set.setFillAfter(true);
        set.initialize(appWidth, appHeight, appWidth, appHeight);

        return set;
    }

    //THUMBNAIL_TRANSITION_ENTER_SCALE_DOWN
    public static AnimationSet debugCreateAnimation3(int appWidth, int appHeight, float scaleW, float scaleH, float launcherPivotX, float launcherPivotY,float translateX,float translateY){
        Animation scale = new ScaleAnimation(LAUNCHER_SCALE, 1, LAUNCHER_SCALE, 1, launcherPivotX,
                launcherPivotY);
        scale.setDuration(SCALE_DOWN_ENTER_DURATION);
        scale.setInterpolator(sScaleDownEnterInterpolator);
        Animation alpha = new AlphaAnimation(1f, 1);
        Animation translate = new TranslateAnimation(translateX, 0, translateY, 0);
        AnimationSet set = new AnimationSet(true);
        //set.addAnimation(alpha);//退出时桌面透明度直接为1
        set.addAnimation(scale);
        //set.addAnimation(translate);

        return set;
    }

    //THUMBNAIL_TRANSITION_EXIT_SCALE_DOWN
    public static AnimationSet debugCreateAnimation4(int appWidth, int appHeight, float scaleW, float scaleH, float launcherPivotX, float launcherPivotY,float translateX,float translateY){
        Animation scaleX = new ScaleXAnimation(1, scaleW, 1, 1,
                launcherPivotX,
                0);
        scaleX.setDuration(SCALE_DOWN_EXIT_DURATION/2);
        scaleX.setInterpolator(sScaleDownExitInterpolator);

        Animation scaleY = new ScaleYAnimation(1, 1, 1, scaleH/(appHeight/(float)appWidth),
                0,
                launcherPivotY);
        scaleY.setDuration(SCALE_DOWN_EXIT_DURATION/2);
        scaleY.setInterpolator(sScaleDownExitInterpolator);

        Animation alpha = new AlphaAnimation(1, 0);
        alpha.setStartOffset(SCALE_DOWN_EXIT_DURATION-SCALE_DOWN_EXIT_ALPHA_DURATION-100);
        alpha.setDuration(SCALE_DOWN_EXIT_ALPHA_DURATION);
        alpha.setInterpolator(sScaleDownExitInterpolator);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(scaleX);
        set.addAnimation(scaleY);
        set.addAnimation(alpha);
        set.setZAdjustment(Animation.ZORDER_TOP);
        set.setFillAfter(false);
        set.initialize(appWidth, appHeight, appWidth, appHeight);

        return set;
    }



    private static class ScaleXAnimation extends ScaleAnimation {
        float[] mTmpValues = new float[9];
        float mFromXScale = 1;
        float mToXScale = 1;
        float mPivotX;
        float mPivotY;

        public ScaleXAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
            super(fromX, toX, fromY, fromY, pivotX, pivotY);
            mFromXScale = fromX;
            mToXScale = toX;
            mPivotX = pivotX;
            mPivotY = pivotY;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Matrix m = t.getMatrix();
            m.getValues(mTmpValues);
            float dx = mFromXScale + ((mToXScale - mFromXScale) * interpolatedTime);
            t.getMatrix().setScale(dx, mTmpValues[Matrix.MSCALE_Y], mPivotX, mPivotY);
            //Log.d(TAG, "scale dx =" + dx);
        }
    }

    private static class ScaleYAnimation extends ScaleAnimation {
        float[] mTmpValues = new float[9];
        float mFromYScale = 1;
        float mToYScale = 1;
        float mPivotX;
        float mPivotY;

        public ScaleYAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
            super(fromX, toX, fromY, fromY, pivotX, pivotY);
            mFromYScale = fromY;
            mToYScale = toY;
            mPivotX = pivotX;
            mPivotY = pivotY;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Matrix m = t.getMatrix();
            m.getValues(mTmpValues);
            float dy = mFromYScale + ((mToYScale - mFromYScale) * interpolatedTime);
            t.getMatrix().setScale(mTmpValues[Matrix.MSCALE_X], dy, mPivotX, mPivotY);
            //Log.d(TAG, "scale dy =" + dy);
        }
    }
}
