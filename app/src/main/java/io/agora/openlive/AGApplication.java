package io.agora.openlive;

import android.app.Application;

import cn.bmob.v3.Bmob;
import io.agora.openlive.model.WorkerThread;

public class AGApplication extends Application {

    private WorkerThread mWorkerThread;

    /**
     * SDK初始化也可以放到Application中
     */
    public static String APPID = "e0e5821ab961e7607a39f5926cae7a51";

    @Override
    public void onCreate() {
        super.onCreate();
        //第二：默认初始化
        Bmob.initialize(getApplicationContext(), APPID, "demo");
    }

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }
}
