package io.agora.openlive.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.SurfaceView;
import android.text.TextUtils;
import io.agora.common.Constant;
import io.agora.openlive.R;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.videoprp.AgoraYuvEnhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WorkerThread extends Thread {
    private final static Logger log = LoggerFactory.getLogger(WorkerThread.class);

    private final Context mContext;

    private static final int ACTION_WORKER_THREAD_QUIT = 0X1010; // quit this thread

    private static final int ACTION_WORKER_JOIN_CHANNEL = 0X2010;

    private static final int ACTION_WORKER_LEAVE_CHANNEL = 0X2011;

    private static final int ACTION_WORKER_CONFIG_ENGINE = 0X2012;

    private static final int ACTION_WORKER_PREVIEW = 0X2014;

    private static final class WorkerThreadHandler extends Handler {

        private WorkerThread mWorkerThread;

        WorkerThreadHandler(WorkerThread thread) {
            this.mWorkerThread = thread;
        }

        public void release() {
            mWorkerThread = null;
        }

        @Override
        public void handleMessage(Message msg) {
            if (this.mWorkerThread == null) {
                log.warn("handler is already released! " + msg.what);
                return;
            }

            switch (msg.what) {
                case ACTION_WORKER_THREAD_QUIT:
                    mWorkerThread.exit();
                    break;
                case ACTION_WORKER_JOIN_CHANNEL:
                    String[] data = (String[]) msg.obj;
                    mWorkerThread.joinChannel(data[0], msg.arg1);
                    break;
                case ACTION_WORKER_LEAVE_CHANNEL:
                    String channel = (String) msg.obj;
                    mWorkerThread.leaveChannel(channel);
                    break;
                case ACTION_WORKER_CONFIG_ENGINE:
                    Object[] configData = (Object[]) msg.obj;
                    mWorkerThread.configEngine((int) configData[0], (int) configData[1]);
                    break;
                case ACTION_WORKER_PREVIEW:
                    Object[] previewData = (Object[]) msg.obj;
                    mWorkerThread.preview((boolean) previewData[0], (SurfaceView) previewData[1], (int) previewData[2]);
                    break;
            }
        }
    }

    private WorkerThreadHandler mWorkerHandler;

    private boolean mReady;

    public final void waitForReady() {
        while (!mReady) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("wait for " + WorkerThread.class.getSimpleName());
        }
    }

    @Override
    public void run() {
        log.trace("start to run");
        Looper.prepare();

        mWorkerHandler = new WorkerThreadHandler(this);

        ensureRtcEngineReadyLock();

        mReady = true;

        // enter thread looper
        Looper.loop();
    }

    private RtcEngine mRtcEngine;

    private AgoraYuvEnhancer mVideoEnhancer = null;

    public final void enablePreProcessor() {
        if (mEngineConfig.mClientRole == Constants.CLIENT_ROLE_BROADCASTER) {
            if (Constant.PRP_ENABLED) {
                if (mVideoEnhancer == null) {
                    mVideoEnhancer = new AgoraYuvEnhancer(mContext);
                    mVideoEnhancer.SetLighteningFactor(Constant.PRP_DEFAULT_LIGHTNESS);
                    mVideoEnhancer.SetSmoothnessFactor(Constant.PRP_DEFAULT_SMOOTHNESS);
                    mVideoEnhancer.StartPreProcess();
                }
            }
        }
    }

    public final void setPreParameters(float lightness, float smoothness) {
        if (mEngineConfig.mClientRole == Constants.CLIENT_ROLE_BROADCASTER) {
            if (Constant.PRP_ENABLED) {
                if (mVideoEnhancer == null) {
                    mVideoEnhancer = new AgoraYuvEnhancer(mContext);
                }
                mVideoEnhancer.StartPreProcess();
            }
        }

        Constant.PRP_DEFAULT_LIGHTNESS = lightness;
        Constant.PRP_DEFAULT_SMOOTHNESS = smoothness;

        if (mVideoEnhancer != null) {
            mVideoEnhancer.SetLighteningFactor(Constant.PRP_DEFAULT_LIGHTNESS);
            mVideoEnhancer.SetSmoothnessFactor(Constant.PRP_DEFAULT_SMOOTHNESS);
        }
    }

    public final void disablePreProcessor() {
        if (mVideoEnhancer != null) {
            mVideoEnhancer.StopPreProcess();
            mVideoEnhancer = null;
        }
    }

    /**
     * 使用该方法加入频道，在同一个频道内的用户可以互相通话，多个用户加入同一个频道，可以群聊。使用不同App ID的应用程序是不能互通的。
     * 如果已在通话中，
     * 用户必须调用leaveChannel退出当前通话，才能进入下一个频道。
     *
     * 同一个频道里不能出现两个相同的 UID。如果你的 App 支持多设备同时登录，即同一个用户账号可以在不同的设备上同时登录(例如微信支持在 PC 端和移动端同时登录)，
     * 请保证传入的 UID 不相同。例如你之前都是用同一个用户标识作为 UID, 建议从现在开始加上设备 ID,
     * 以保证传入的 UID 不相同 。如果你的 App 不支持多设备同时登录，例如在电脑上登录时，手机上会自动退出，这种情况下就不需要在 UID 上添加设备 ID。
     * @param channel
     * @param uid
     */
    public final void joinChannel(final String channel, int uid) {
        if (Thread.currentThread() != this) {
            log.warn("joinChannel() - worker thread asynchronously " + channel + " " + uid);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_JOIN_CHANNEL;
            envelop.obj = new String[]{channel};
            envelop.arg1 = uid;
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();
        /**
         *channel key : 此为程序生成的Channel Key
            当用户使用静态Key也即只使用App ID时, 该参数是可选的，传NULL即可；
         *当用户使用Channel Key时，Agora为应用程序开发者额外签发一个App Certificate，开发者结合Agora提供的算法生成此Channel Key，用于服务器端用户验证。
         一般来说使用App ID即可，对于安全有极高要求的使用者需要使用Channel Key。
         *
         * channelName : 标识通话的频道名称，长度在64字节以内的字符串。
         * optionalInfo : 非必选项) 开发者需加入的任何附加信息 [1]。一般可设置为空字符串，或频道相关信息。该信息不会传递给频道内的其他用户。
         *
         * 例如: 当主播想要自定义直播频道的视频分辨率和码率，且旁路直播已开通时，可以通过设置该参数实现功能，Json 格式示例如下:
         * {“owner”:true, ..., “width”:300, “height”:400, “bitrate”:100}; 。只有当 width, height, 和 bitrate 三者都不为 0 时，分辨率和码率设置才能生效。
         *
         *
         * optionalUid :(非必选项) 用户ID，32位无符号整数。建议设置范围：1到(2^32-1)，并保证唯一性。如果不指定（即设为0），
         * SDK 会自动分配一个，并在 onJoinChannelSuccess回调方法中返回，App层必须记住该返回值并维护，SDK不对该返回值进行维护。
         *
         */
        mRtcEngine.joinChannel(null, channel, "OpenLive", uid);

        mEngineConfig.mChannel = channel;

        enablePreProcessor();
        log.debug("joinChannel " + channel + " " + uid);
    }

    public final void leaveChannel(String channel) {
        if (Thread.currentThread() != this) {
            log.warn("leaveChannel() - worker thread asynchronously " + channel);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_LEAVE_CHANNEL;
            envelop.obj = channel;
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }

        disablePreProcessor();

        int clientRole = mEngineConfig.mClientRole;
        mEngineConfig.reset();
        log.debug("leaveChannel " + channel + " " + clientRole);
    }

    private EngineConfig mEngineConfig;

    public final EngineConfig getEngineConfig() {
        return mEngineConfig;
    }

    private final MyEngineEventHandler mEngineEventHandler;

    public final void configEngine(int cRole, int vProfile) {
        if (Thread.currentThread() != this) {
            log.warn("configEngine() - worker thread asynchronously " + cRole + " " + vProfile);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_CONFIG_ENGINE;
            envelop.obj = new Object[]{cRole, vProfile};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();
        mEngineConfig.mClientRole = cRole;
        mEngineConfig.mVideoProfile = vProfile;

        /*设置本地视频属性
        * 使用该方法设置视频编码属性（Profile）。每个属性对应一套视频参数，如分辨率、帧率、码率等。
        * 当设备的摄像头不支持指定的分辨率时，SDK会自动选择一个合适的摄像头分辨率，但是编码分辨率仍然用setVideoProfile 指定的。
        * 应在调用 enableVideo 后设置视频属性。
        *  应在调用 joinChannel/startPreview 前设置视频属性。
        *  是否交换宽和高。
            true：交换宽和高
            false：不交换宽和高(默认)
        * **/
        mRtcEngine.setVideoProfile(mEngineConfig.mVideoProfile, true);

        /**
         * 在加入频道前，用户需要通过本方法设置观众(默认)或主播模式。在加入频道后，用户可以通过本方法切换用户模式。
         * cRole==
         * CLIENT_ROLE_BROADCASTER = 1; 主播
            CLIENT_ROLE_AUDIENCE = 2; 观众(默认)
         *PermissKey =
         * 如果没有开通连麦鉴权功能，将其设置为空。

         如果已开通连麦鉴权功能，根据不同的用户角色:

         观众: 将其设置为空。
         主播: 将其设置为 In Channel Permission Ke
         *
         */
        mRtcEngine.setClientRole(cRole, "");

        /**
         * 创建数据流
         * 该方法用于创建数据流。频道内每人最多只能创建5个数据流。
         * 频道内数据通道最多允许数据延迟5秒，若超过5秒接收方尚未收到数据流，则数据通道会向应用程序报错。
         * 目前Agora Native SDK支持99%可靠和100%有序的数据传输。
         */
        mRtcEngine.createDataStream(true,true);

        log.debug("configEngine " + cRole + " " + mEngineConfig.mVideoProfile);
    }

    public final void preview(boolean start, SurfaceView view, int uid) {
        if (Thread.currentThread() != this) {
            log.warn("preview() - worker thread asynchronously " + start + " " + view + " " + (uid & 0XFFFFFFFFL));
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_PREVIEW;
            envelop.obj = new Object[]{start, view, uid};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();
        if (start) {
            mRtcEngine.setupLocalVideo(new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid));
            //开启视频预览(startPreview)
            /**
             * 使用该方法启动本地视频预览。在开启预览前，必须先调用setupLocalVideo设置预览窗口及属性，
             * 且必须调用enableVideo开启视频功能。如果在调用joinChannel进入频道之前调用了startPreview启动本地视频预览，
             * 在调用leaveChannel退出频道之后本地预览仍然处于启动状态，如需要关闭本地预览，需要调用stopPreview。
             */
            mRtcEngine.startPreview();
        } else {
            mRtcEngine.stopPreview();
        }
    }

    public static String getDeviceID(Context context) {
        // XXX according to the API docs, this value may change after factory reset
        // use Android id as device id
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private RtcEngine ensureRtcEngineReadyLock() {
        if (mRtcEngine == null) {
            String appId = mContext.getString(R.string.private_app_id);
            if (TextUtils.isEmpty(appId)) {
                throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
            }
            mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler.mRtcEventHandler);//创建etcEngine对象
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);//设置频道模式
            mRtcEngine.enableVideo();//开启录像
            mRtcEngine.setLogFile(Environment.getExternalStorageDirectory()
                    + File.separator + mContext.getPackageName() + "/log/agora-rtc.log");
            mRtcEngine.enableDualStreamMode(true);//使用单流/双流直播模式，true = 双流 ，false = 单流
        }
        return mRtcEngine;
    }

    public MyEngineEventHandler eventHandler() {
        return mEngineEventHandler;
    }

    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }

    /**
     * call this method to exit
     * should ONLY call this method when this thread is running
     */
    public final void exit() {
        if (Thread.currentThread() != this) {
            log.warn("exit() - exit app thread asynchronously");
            mWorkerHandler.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT);
            return;
        }

        mReady = false;

        // TODO should remove all pending(read) messages

        mVideoEnhancer = null;

        log.debug("exit() > start");

        // exit thread looper
        Looper.myLooper().quit();

        mWorkerHandler.release();

        log.debug("exit() > end");
    }

    public WorkerThread(Context context) {
        this.mContext = context;

        this.mEngineConfig = new EngineConfig();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mEngineConfig.mUid = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_UID, 0);

        this.mEngineEventHandler = new MyEngineEventHandler(mContext, this.mEngineConfig);
    }
}
