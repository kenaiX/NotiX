package com.flyme.systemuitools.gamemode.utils;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.flyme.systemuitools.R;
import com.flyme.systemuitools.gamemode.events.DragEvents;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.view.StageView;
import com.hwangjr.rxbus.RxBus;

import java.util.ArrayList;
import java.util.List;

public class DragHelper {

    private static DragHelper sDragHelper = new DragHelper();
    private ImageView mDragView;
    private View mOriginView;
    private AppInfo mOriginInfo;
    private Dragable mOriginParent;
    private List<Dragable> mDragableViewList = new ArrayList<>();
    private boolean mDraging;
    private Dragable mLastDragView;

    private DragHelper() {

    }

    public static DragHelper getInstance() {
        return sDragHelper;
    }

    public void init(StageView v) {
        mDragView = (ImageView) v.findViewById(R.id.gamemode_dragView);
        mDragableViewList.clear();
        for (int i = 0, n = v.getChildCount(); i < n; i++) {
            View child = v.getChildAt(i);
            if (child instanceof Dragable) {
                mDragableViewList.add((Dragable) child);
            }
        }
    }

    public void startDrag(View v, AppInfo info, Dragable parent, float startX, float startY) {
        mOriginView = v;
        mOriginInfo = info;
        mOriginParent = parent;
        mDraging = true;
        /*Bitmap bitmap = Bitmap.createBitmap(mOriginView.getWidth(), mOriginView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        mOriginView.draw(c);*/

        mDragView.setScaleX(1f);
        mDragView.setScaleY(1f);
        mDragView.setAlpha(1f);
        mDragView.setVisibility(View.VISIBLE);
        mDragView.setImageDrawable(mOriginInfo.getIcon());

        int[] position = new int[2];
        mOriginView.getLocationInWindow(position);

        updateDragView(startX, startY);

        if (mOriginParent != null) {
            mOriginParent.onDragEnter();
            mLastDragView = mOriginParent;
        }
    }

    public void stopDtag() {
        mDraging = false;
    }

    public boolean idDraging() {
        return mDraging;
    }

    public void reset() {
        mOriginView = null;
        mOriginInfo = null;
        mOriginParent = null;
        mDraging = false;

        mDragView.setVisibility(View.INVISIBLE);
        mDragView.setImageDrawable(null);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mLastDragView = null;
            final Dragable firedView = getFireDragableView(event.getX(), event.getY());
            Rect rect = null;
            if (firedView != null) {
                rect = firedView.canAccept();
            }
            if (rect != null) {
                animateDragView(rect, new Runnable() {
                    @Override
                    public void run() {
                        //当mDragView不显示的时候意味着用户已经关闭了此窗口
                        if (mDragView.isShown()) {
                            mDragView.setVisibility(View.INVISIBLE);
                            firedView.onAcceptedCompleted(event, mOriginInfo);
                            if (firedView != mOriginParent) {
                                mOriginParent.onRemovedCompleted(mOriginView);
                            }
                            RxBus.get().post(new DragEvents.StopDragCompleted());
                        }
                    }
                });
            } else {
                rect = mOriginParent.canAccept();
                animateDragView(rect, new Runnable() {
                    @Override
                    public void run() {
                        mDragView.setVisibility(View.INVISIBLE);
                        mOriginParent.onAcceptedCompleted(event, mOriginInfo);
                        RxBus.get().post(new DragEvents.StopDragCompleted());
                    }
                });
            }
            stopDtag();
        } else if (action == MotionEvent.ACTION_MOVE) {
            updateDragView(event.getX(), event.getY());

            final Dragable firedView = getFireDragableView(event.getX(), event.getY());
            if (firedView != null) {
                firedView.onDragOver(event);
            }
            if (mLastDragView != firedView) {
                if (mLastDragView != null) {
                    mLastDragView.onDragExit();
                }
                mLastDragView = firedView;
            }
        }
        return true;
    }

    void animateDragView(Rect toRect, Runnable finishedCallback) {
        int[] position = new int[2];
        mDragView.getLocationInWindow(position);
        Rect temp = new Rect(position[0], position[1], position[0] + mDragView.getWidth(), position[1] + mDragView.getHeight());

        if (toRect.width() == 0) {
            mDragView.animate()
                    .alpha(0f)
                    .scaleY(0.3f)
                    .scaleX(0.3f)
                    .withEndAction(finishedCallback);
        } else {
            mDragView.animate()
                    .translationXBy(toRect.centerX() - temp.centerX())
                    .translationYBy(toRect.centerY() - temp.centerY())
                    .withEndAction(finishedCallback);
        }
    }

    void updateDragView(float x, float y) {
        x -= mDragView.getWidth() / 2;
        y -= mDragView.getHeight() * 1.5f;

        mDragView.setTranslationX(x);
        mDragView.setTranslationY(y);
    }

    Dragable getFireDragableView(float startX, float startY) {
        for (Dragable d : mDragableViewList) {
            View v = (View) d;
            if (startX > v.getLeft() && startX <= v.getRight() && startY > v.getTop() && startY <= v.getBottom()) {
                return d;
            }
        }
        return null;
    }
}
