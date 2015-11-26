package com.beautifullife.core.rx.schedulers;

import android.os.Handler;
import android.os.Looper;

import com.beautifullife.core.rx.android.plugins.RxAndroidPlugins;

import rx.Scheduler;


/** Android-specific Schedulers. */
public final class AndroidSchedulers {
    private AndroidSchedulers() {
        throw new AssertionError("No instances");
    }

    private static final Scheduler MAIN_THREAD_SCHEDULER =
            new HandlerScheduler(new Handler(Looper.getMainLooper()));

    /** A {@link Scheduler} which executes actions on the Android UI thread. */
    public static Scheduler mainThread() {
        Scheduler scheduler =
                RxAndroidPlugins.getInstance().getSchedulersHook().getMainThreadScheduler();
        return scheduler != null ? scheduler : MAIN_THREAD_SCHEDULER;
    }
}
