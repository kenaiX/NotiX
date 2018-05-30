package com.flyme.systemuitools.launcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;

public class RedPointService extends Service {

    private final ArrayList<IRedPointCallback> mListenerList = new ArrayList<>();

    private final IBinder mImpl = new IRedPointService.Stub() {
        @Override
        public int getVersion() throws RemoteException {
            return 1;
        }

        @Override
        public String getRedPointList() throws RemoteException {
            return RedPointManager.getInstance().getListString();
        }

        @Override
        public void markRedPointList(String delete) throws RemoteException {
            RedPointManager.getInstance().markRedPointList(delete);
        }

        @Override
        public void setListener(IRedPointCallback callback) throws RemoteException {
            synchronized (mListenerList) {
                if (!mListenerList.contains(callback)) {
                    mListenerList.add(callback);
                }
            }
        }
    };

    private final RedPointManager.RedPointChangeCallback mCallback = new RedPointManager.RedPointChangeCallback() {
        @Override
        public void onChange() {
            onRedPointListChanged();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        RedPointManager.getInstance().setChangeListener(mCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RedPointManager.getInstance().setChangeListener(null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mImpl;
    }

    private void onRedPointListChanged() {
        String listString = RedPointManager.getInstance().getListString();
        synchronized (mListenerList) {
            ArrayList<IRedPointCallback> toRemove = null;
            for (int i = mListenerList.size() - 1; i > -1; i--) {
                try {
                    mListenerList.get(i).onRedPointListChanged(listString);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    if (toRemove == null) {
                        toRemove = new ArrayList<>();
                    }
                    toRemove.add(mListenerList.get(i));
                }
            }
            if (toRemove != null) {
                for (IRedPointCallback callback : toRemove) {
                    mListenerList.remove(callback);
                }
            }
        }
    }
}
