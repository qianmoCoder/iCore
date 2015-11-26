package com.beautifullife.core.common;

import android.os.Handler;
import android.os.Looper;

import com.beautifullife.core.util.MultiHashMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2015/11/17.
 */
public class ObserverManager {


    private ObserverManager() {
    }

    public static ObserverManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static ObserverManager instance = new ObserverManager();
    }

    private MultiHashMap<Integer, WeakReference<Observer>> observers = new MultiHashMap<Integer, WeakReference<Observer>>();

    private Handler handler = new Handler(Looper.getMainLooper());

    public void registerObserver(int action, Observer observer) {
        synchronized (observers) {
            observers.put(action, new WeakReference<Observer>(observer));
        }
    }

    public void unRegisterObserver(Observer observer) {
        synchronized (observers) {
            Set<Map.Entry<Integer, ArrayList<WeakReference<Observer>>>> entrySet = observers.entrySet();
            for (Map.Entry<Integer, ArrayList<WeakReference<Observer>>> entry : entrySet) {
                ArrayList<WeakReference<Observer>> list = entry.getValue();
                if (list == null || list.size() == 0) {
                    continue;
                }
                for (int i = list.size() - 1; i > -1; i--) {
                    WeakReference<Observer> wact = list.get(i);
                    if (observer == wact.get()) {
                        list.remove(i);
                    }
                }
            }
        }
    }

    public void unRegisterObserver(int action, Observer observer) {
        synchronized (observers) {
            ArrayList<WeakReference<Observer>> list = observers.get(action);
            if (list == null || list.size() == 0) {
                return;
            }
            for (int i = list.size() - 1; i > -1; i--) {
                WeakReference<Observer> wact = list.get(i);
                if (observer == wact.get()) {
                    list.remove(i);
                }
            }

        }
    }

    public void notifyUi(final int action, final Object obj, final int stateCode) {
        ArrayList<WeakReference<Observer>> listeners = observers.get(action);
        if (listeners != null) {
            listeners = copyListener(listeners);
            for (WeakReference<Observer> weakListener : listeners) {
                final Observer listener = weakListener.get();
                if (listener != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                listener.onReceiverNotify(action, obj, stateCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

    }

    private ArrayList<WeakReference<Observer>> copyListener(ArrayList<WeakReference<Observer>> listeners) {
        ArrayList<WeakReference<Observer>> copy = new ArrayList<WeakReference<Observer>>();
        synchronized (observers) {
            for (int i = listeners.size() - 1; i > -1; i--) {
                WeakReference<Observer> weakListener = listeners.get(i);
                if (weakListener.get() == null) {
                    listeners.remove(i);
                } else {
                    copy.add(weakListener);
                }
            }
        }
        return copy;
    }
}
