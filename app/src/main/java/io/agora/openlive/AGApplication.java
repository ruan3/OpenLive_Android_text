package io.agora.openlive;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.mob.MobApplication;
import com.mob.MobSDK;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackManager;
import cn.bmob.v3.Bmob;
import io.agora.openlive.model.WorkerThread;

public class AGApplication extends Application {

    private WorkerThread mWorkerThread;

    /**
     * SDK初始化也可以放到Application中
     */
    public static String APPID = "e0e5821ab961e7607a39f5926cae7a51";
    Context context;



    @Override
    public void onCreate() {
        super.onCreate();
        //第二：默认初始化
        Bmob.initialize(getApplicationContext(), APPID, "demo");
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);
        // 必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getInstance().init(this) 来初始化滑动返回
        BGASwipeBackManager.getInstance().init(this);
        context = this;
        //初始化SDK
        if(!(context instanceof MobApplication)){
            MobSDK.init(context.getApplicationContext());
        }
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
