package io.agora.openlive.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luolc.emojirain.EmojiRainLayout;
import com.ufreedom.floatingview.Floating;
import com.ufreedom.floatingview.FloatingBuilder;
import com.ufreedom.floatingview.FloatingElement;
import com.ufreedom.floatingview.effect.TranslateFloatingTransition;
import com.ufreedom.floatingview.spring.ReboundListener;
import com.ufreedom.floatingview.spring.SpringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.common.Constant;
import io.agora.contract.animation.PlaneFloating;
import io.agora.contract.animation.StarFloating;
import io.agora.model.LiveVideos;
import io.agora.openlive.R;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.model.Message;
import io.agora.openlive.model.User;
import io.agora.openlive.model.VideoStatusData;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.utils.UIUtils;

public class LiveRoomActivity extends BaseActivity implements AGEventHandler {

    private final static Logger log = LoggerFactory.getLogger(LiveRoomActivity.class);

    private GridVideoViewContainer mGridVideoViewContainer;

    private RelativeLayout mSmallVideoViewDock;

    private LinearLayout ly_leaving;//主播离开提示

    Context context;

    int isAudience = 0;//判别主播与观众

    String anchorID;//主播的id

    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    RecyclerView msgListView;//显示消息的recycleVIew

    /**
     * 评论UI
     */
    LinearLayout ly_comment;
    ImageView iv_star;
    ImageView iv_paper_airplane;
    ImageView iv_like;
    private Floating mFloating;
    private int mScreenWidth;
    private int mScreenHeight;

    /**
     * 更新点赞数到后台
     */
    private int likes = 0;

    /**
     * 更新礼物数到后台
     */
    private int gifts = 0;

    /**
     * 仿微信下动物雨
     * @param savedInstanceState
     */
    EmojiRainLayout emojiContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        context = this;
        mFloating = new Floating(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        mScreenWidth = UIUtils.getScreenWidth(this);
        mScreenHeight = UIUtils.getScreenWidth(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private boolean isBroadcaster(int cRole) {
        return cRole == Constants.CLIENT_ROLE_BROADCASTER;
    }

    private boolean isBroadcaster() {
        return isBroadcaster(config().mClientRole);
    }

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent i = getIntent();
        int cRole = i.getIntExtra(ConstantApp.ACTION_KEY_CROLE, 0);
        isAudience = cRole;
        if (cRole == 0) {
            throw new RuntimeException("Should not reach here");
        }

        String roomName = i.getStringExtra(ConstantApp.ACTION_KEY_ROOM_NAME);

        anchorID = roomName;

        doConfigEngine(cRole);//创建直播需要的引擎，根据房间号

        //这个空间继承与recleyView,内部实现了adapter,这个是显示界面的view
        mGridVideoViewContainer = (GridVideoViewContainer) findViewById(R.id.grid_video_view_container);
        mGridVideoViewContainer.setItemEventHandler(new VideoViewEventListener() {
            @Override
            public void onItemDoubleClick(View v, Object item) {
                log.debug("onItemDoubleClick " + v + " " + item);

                if (mUidsList.size() < 2) {
                    return;
                }

                if (mViewType == VIEW_TYPE_DEFAULT)
                    switchToSmallVideoView(((VideoStatusData) item).mUid);
                else
                    switchToDefaultVideoView();
            }
        });

        ImageView button1 = (ImageView) findViewById(R.id.btn_1);
        ImageView button2 = (ImageView) findViewById(R.id.btn_2);
        ImageView button3 = (ImageView) findViewById(R.id.btn_3);

        ly_leaving = (LinearLayout) findViewById(R.id.ly_leaving);
        ly_leaving.setVisibility(View.GONE);

        ImageView btn_message = (ImageView) findViewById(R.id.btn_message);

        /**
         * 启动输入界面
         */
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageEditContainer();
            }
        });


        if (isBroadcaster(cRole)) {//是否是主播
            //创建渲染视图(createRendererView)
            SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
            /**
             * 设置本地视频显示属性 (setupLocalVideo)
             * 设置视频属性。Class VideoCanvas:
             * view: 视频显示视窗。
             * renderMode: 视频显示模式。
             * RENDER_MODE_HIDDEN (1): 如果视频尺寸与显示视窗尺寸不一致，则视频流会按照显示视窗的比例进行周边裁剪或图像拉伸后填满视窗。
             * RENDER_MODE_FIT(2): 如果视频尺寸与显示视窗尺寸不一致，在保持长宽比的前提下，将视频进行缩放后填满视窗。
             * RENDER_MODE_ADAPTIVE(3)：如果自己和对方都是竖屏，或者如果自己和对方都是横屏，使用RENDER_MODE_HIDDEN；
             * 如果对方和自己一个竖屏一个横屏，则使用RENDER_MODE_FIT。
             *
             * uid: 本地用户ID, 与joinchannel方法中的uid保持一致。
             */
            rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, 0));
            /**
             * 如果想让SurfaveView显示图片或者视频必须要调用SurfaceView.setZOrderOnTop(true),
             * 也就是说必须把SurfaceView置于Activity显示窗口的最顶层才能正常显示
             */
            surfaceV.setZOrderOnTop(true);
            /**
             *  大概的意思就是说控制窗口中表面的视图层是否放置在常规视图层的顶部。
             最终，我在调用setZOrderOnTop(true)之后调用了setZOrderMediaOverlay(true)，OK，遮挡问题完美解决！
             */
            surfaceV.setZOrderMediaOverlay(true);

            mUidsList.put(0, surfaceV); // get first surface view

            mGridVideoViewContainer.initViewContainer(getApplicationContext(), 0, mUidsList); // first is now full view
            //开启视频预览(startPreview)
            worker().preview(true, surfaceV, 0);
            broadcasterUI(button1, button2, button3);
        } else {
            audienceUI(button1, button2, button3);
        }

        worker().joinChannel(roomName, config().mUid);

        TextView textRoomName = (TextView) findViewById(R.id.room_name);
        textRoomName.setText(roomName);

        initMessageUI();
        InitCommentUI();

        //下动物雨的容器
        emojiContainer = (EmojiRainLayout) findViewById(R.id.fl_emoji_container);
        initEmojiContainer();
    }

    /**
     * 初始化下动物雨的控件
     */
    private void initEmojiContainer(){

        emojiContainer.addEmoji(R.drawable.emoji_1);
        emojiContainer.addEmoji(R.drawable.emoji_2);
        emojiContainer.addEmoji(R.drawable.emoji_3);
        emojiContainer.addEmoji(R.drawable.emoji_4);
        emojiContainer.addEmoji(R.drawable.emoji_5);

    }



    /**
     * 启动下动物雨动画
     */
    private void startEmoji(){
        emojiContainer.stopDropping();
        emojiContainer.startDropping();
    }


    private InChannelMessageListAdapter mMsgAdapter;
    private ArrayList<Message> mMsgList;

    /**
     * 初始化评论UI
     */
    private void InitCommentUI(){

        ly_comment = (LinearLayout) findViewById(R.id.ly_comment);
        iv_star = (ImageView) findViewById(R.id.iv_star);
        iv_like = (ImageView) findViewById(R.id.iv_like);
        iv_paper_airplane = (ImageView) findViewById(R.id.iv_paper_airplane);

        //给星监听
        iv_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendChannelMsg("sugar");

                StarFloating(v);
                if(isBroadcaster(isAudience)){
                    gifts++;
                }

            }
        });

        //喜欢监听
        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendChannelMsg("cool~");
                likeFloating(v);
                if(isBroadcaster(isAudience)){
                    likes++;
                }

            }
        });

        //纸飞机监听
        iv_paper_airplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendChannelMsg("paper_air_plane");
                PlaneFloating(v);
                if(isBroadcaster(isAudience)){
                    gifts++;
                }
            }
        });

    }

    /**
     * 喜欢点赞
     * @param v
     */
    private void likeFloating(View v){
        FloatingElement floatingElement = new FloatingBuilder()
                .anchorView(iv_like)
                .targetView(R.layout.ic_like)
                .floatingTransition(new TranslateFloatingTransition())
                .build();
        mFloating.startFloating(floatingElement);
    }

    /**
     * 初始化消息输入框
     */
    private void initMessageUI(){

        Log.e("Com","------initMessageUi----");

        mMsgList = new ArrayList<>();
        msgListView = (RecyclerView) findViewById(R.id.msg_list);
        mMsgAdapter = new InChannelMessageListAdapter(context,mMsgList);
        mMsgAdapter.setHasStableIds(true);
        msgListView.setAdapter(mMsgAdapter);
        msgListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));
        msgListView.addItemDecoration(new MessageListDecoration());
    }

    /**
     * 更新消息列表
     * @param msg
     */
    private void notifyMessageChanged(Message msg){
        mMsgList.add(msg);


//        if(msgStr.contains("下小精灵")){
//
//            startEmoji();
//
//        }

        int MAX_MESSAGE_COUNT = 16;

        if(mMsgList.size() > MAX_MESSAGE_COUNT){
            int toRemove = mMsgList.size() - MAX_MESSAGE_COUNT;
            for(int i=0;i<toRemove;i++){
                mMsgList.remove(i);
            }
        }
            mMsgAdapter.notifyDataSetChanged();
            msgListView.scrollToPosition(mMsgList.size()-1);
    }

    /**
     * 隐藏消息输入框
     * @param view
     */
    public void onClickHideIME(View view) {
        log.debug("onClickHideIME " + view);

        closeIME(findViewById(R.id.msg_content));

        findViewById(R.id.msg_input_container).setVisibility(View.GONE);
//        findViewById(R.id.bottom_action_end_call).setVisibility(View.VISIBLE);
        findViewById(R.id.bottom_action_container).setVisibility(View.VISIBLE);
    }

    private int mDataStreamId;

    /**
     * 显示输入消息框
     */
    private void showMessageEditContainer() {
        findViewById(R.id.bottom_action_container).setVisibility(View.GONE);
//        findViewById(R.id.bottom_action_end_call).setVisibility(View.GONE);
        findViewById(R.id.msg_input_container).setVisibility(View.VISIBLE);

        EditText edit = (EditText) findViewById(R.id.msg_content);

        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String msgStr = v.getText().toString();
                    if (TextUtils.isEmpty(msgStr)) {
                        return false;
                    }
                    sendChannelMsg(msgStr);
                    if(msgStr.equals("下小精灵")){
                        startEmoji();
                    }
                    v.setText("");

                    Message msg = new Message(Message.MSG_TYPE_TEXT,
                            new User(config().mUid, String.valueOf(config().mUid)), msgStr);
                    notifyMessageChanged(msg);

                    return true;
                }
                return false;
            }
        });

        openIME(edit);
    }

    /**
     * 发消息
     * @param msgStr
     */
    private void sendChannelMsg(String msgStr){
        RtcEngine rtcEngine = rtcEngine();
        if(mDataStreamId <= 0){
            mDataStreamId = rtcEngine.createDataStream(true,true);
        }
        if(mDataStreamId < 0){
            String errorMsg = "Create data stream error happened " + mDataStreamId;
            log.warn(errorMsg);
            showLongToast(errorMsg);
            return;
        }

        byte[] encodeMsg;
        try{
            encodeMsg = msgStr.getBytes("UTF-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            encodeMsg = msgStr.getBytes();
        }
        rtcEngine.sendStreamMessage(mDataStreamId,encodeMsg);
        Log.e("Com","------sendChannelMsg----");
    }

    /**
     * 接受消息回调
     * @param type
     * @param data
     */
    @Override
    public void onExtraCallback(final int type, final Object... data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    Log.e("Com","------runOnUiThread----");
                    return;
                }
                Log.e("Com","------onExtraCallback----");
                doHandleExtraCallback(type, data);
            }
        });
    }

    /**
     * 接受消息Handler
     * @param type
     * @param data
     */
    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
//        boolean muted;

        switch (type) {

            case AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG:

                peerUid = (Integer) data[0];
                Log.e("Com","peerUid----->"+peerUid);
                final byte[] content = (byte[]) data[1];
                Log.e("Com","content----->"+new String(content));
                String contentStr = new String(content);
                if(contentStr.equals("cool~")){
                    likeFloating(iv_like);
                    likes++;
                }else if(contentStr.equals("sugar")) {
                    StarFloating(iv_star);
                    gifts++;
                }else if(contentStr.equals("paper_air_plane")) {
                    PlaneFloating(iv_paper_airplane);
                    gifts++;
                }else if(contentStr.equals("下小精灵")){
                    gifts++;
                    startEmoji();
                    notifyMessageChanged(new Message(new User(peerUid, String.valueOf(peerUid)), new String(content)));
                } else {
                    notifyMessageChanged(new Message(new User(peerUid, String.valueOf(peerUid)), new String(content)));
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];
                Log.e("Com","error----->"+error);
                Log.e("Com","description----->"+description);
//                notifyMessageChanged(new Message(new User(0, null), error + " " + description));

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED:
//                notifyHeadsetPlugged((int) data[0]);

                break;

        }
    }


    /**
     * 主播角色UI
     * @param button1
     * @param button2
     * @param button3
     */
    private void broadcasterUI(ImageView button1, ImageView button2, ImageView button3) {
        button1.setTag(true);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && (boolean) tag) {
                    doSwitchToBroadcaster(false);
                } else {
                    doSwitchToBroadcaster(true);
                }
            }
        });
        button1.setColorFilter(getResources().getColor(R.color.agora_blue), PorterDuff.Mode.MULTIPLY);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worker().getRtcEngine().switchCamera();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                boolean flag = true;
                if (tag != null && (boolean) tag) {
                    flag = false;
                }
                worker().getRtcEngine().muteLocalAudioStream(flag);
                ImageView button = (ImageView) v;
                button.setTag(flag);
                if (flag) {
                    button.setColorFilter(getResources().getColor(R.color.agora_blue), PorterDuff.Mode.MULTIPLY);
                } else {
                    button.clearColorFilter();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBroadcaster(isAudience)) {
            //主播退出的方式
            //先查询到数据的id
            BmobQuery<LiveVideos> query = new BmobQuery<>();
            query.addWhereEqualTo("anchorName", BmobUser.getCurrentUser().getObjectId());
            query.findObjects(new FindListener<LiveVideos>() {
                @Override
                public void done(List<LiveVideos> list, BmobException e) {
                    if(list!=null&&list.size()>0){
                        //查询成功，得到当前liveVideoId
                        String liveId = list.get(0).getObjectId();
                        LiveVideos liveVideos = list.get(0);
                        liveVideos.setLiving(false);
                        Log.e("Com","当前主播获得点赞数---->"+likes);
                        liveVideos.increment("likes",likes);
                        liveVideos.increment("gift_times",gifts);
                        //更新主播状态
                        liveVideos.update(liveId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.e("Com","LivingRoom-主播离开房间成功---->");
                                    likes = 0;
                                }else{
                                    Log.e("Com","livingRoom-主播离开房间失败---->"+e.toString());
                                }
                            }
                        });

                    }
                }
            });

        }else{
            //观众退出的方式
            //先查询到数据的id
            BmobQuery<LiveVideos> query = new BmobQuery<>();
            query.addWhereEqualTo("anchorName", anchorID);
            query.findObjects(new FindListener<LiveVideos>() {
                @Override
                public void done(List<LiveVideos> list, BmobException e) {
                    if(list!=null&&list.size()>0){
                        //查询成功，得到当前liveVideoId
                        String liveId = list.get(0).getObjectId();
                        LiveVideos liveVideos = list.get(0);
                        liveVideos.increment("audience",-1);
                        //更新主播状态
                        liveVideos.update(liveId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.e("Com","livingRoom-观众离开房间成功---->");
                                }else{
                                    Log.e("Com","livingRoom-观众离开房间失败---->"+e.toString());
                                }
                            }
                        });

                    }
                }
            });

        }


    }

    /**
     * 观众的ui
     * @param button1
     * @param button2
     * @param button3
     */
    private void audienceUI(ImageView button1, ImageView button2, ImageView button3) {
        button1.setTag(null);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && (boolean) tag) {
                    doSwitchToBroadcaster(false);
                } else {
                    doSwitchToBroadcaster(true);
                }
            }
        });
        button1.clearColorFilter();
        button2.setVisibility(View.GONE);
        button3.setTag(null);
        button3.setVisibility(View.GONE);
        button3.clearColorFilter();
    }

    /**
     * 创建直播引擎
     * @param cRole
     */
    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        if (prefIndex > ConstantApp.VIDEO_PROFILES.length - 1) {
            prefIndex = ConstantApp.DEFAULT_PROFILE_IDX;
        }
        int vProfile = ConstantApp.VIDEO_PROFILES[prefIndex];

        worker().configEngine(cRole, vProfile);
    }

    @Override
    protected void deInitUIandEvent() {
        doLeaveChannel();
        event().removeEventHandler(this);

        mUidsList.clear();
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        if (isBroadcaster()) {

            worker().preview(false, null, 0);
        }
    }

    public void onClickClose(View view) {

        finish();


    }

    public void onShowHideClicked(View view) {
        boolean toHide = true;
        if (view.getTag() != null && (boolean) view.getTag()) {
            toHide = false;
        }
        view.setTag(toHide);

        doShowButtons(toHide);
    }

    private void doShowButtons(boolean hide) {
        View topArea = findViewById(R.id.top_area);
        topArea.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        View button1 = findViewById(R.id.btn_1);
        button1.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        View button2 = findViewById(R.id.btn_2);
        View button3 = findViewById(R.id.btn_3);
        if (isBroadcaster()) {
            button2.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            button3.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        } else {
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        //这个应该是在子线程一直运行的，监听所在的房间是否有人直播
        doRenderRemoteUi(uid);
    }

    private void doSwitchToBroadcaster(boolean broadcaster) {
        final int currentHostCount = mUidsList.size();
        final int uid = config().mUid;
        log.debug("doSwitchToBroadcaster " + currentHostCount + " " + (uid & 0XFFFFFFFFL) + " " + broadcaster);

        if (broadcaster) {
            doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doRenderRemoteUi(uid);

                    ImageView button1 = (ImageView) findViewById(R.id.btn_1);
                    ImageView button2 = (ImageView) findViewById(R.id.btn_2);
                    ImageView button3 = (ImageView) findViewById(R.id.btn_3);
                    broadcasterUI(button1, button2, button3);

                    doShowButtons(false);
                }
            }, 1000); // wait for reconfig engine
        } else {
            stopInteraction(currentHostCount, uid);
        }
    }

    private void stopInteraction(final int currentHostCount, final int uid) {
        doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doRemoveRemoteUi(uid);

                ImageView button1 = (ImageView) findViewById(R.id.btn_1);
                ImageView button2 = (ImageView) findViewById(R.id.btn_2);
                ImageView button3 = (ImageView) findViewById(R.id.btn_3);
                audienceUI(button1, button2, button3);

                doShowButtons(false);
            }
        }, 1000); // wait for reconfig engine
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                mUidsList.put(uid, surfaceV);
                if (config().mUid == uid) {
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                }

                if (mViewType == VIEW_TYPE_DEFAULT) {
                    log.debug("doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                    log.debug("doRenderRemoteUi VIEW_TYPE_SMALL" + " " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(bigBgUid);
                }
                ly_leaving.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    log.debug("already added to UI, ignore it " + (uid & 0xFFFFFFFFL) + " " + mUidsList.get(uid));
                    return;
                }

                final boolean isBroadcaster = isBroadcaster();
                log.debug("onJoinChannelSuccess " + channel + " " + uid + " " + elapsed + " " + isBroadcaster);

                worker().getEngineConfig().mUid = uid;

                SurfaceView surfaceV = mUidsList.remove(0);
                if (surfaceV != null) {
                    mUidsList.put(uid, surfaceV);
                }
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        Log.e("Com","测试主播是否下线的东西---"+reason);
        //这个是监听，主播离线的操作
        doRemoveRemoteUi(uid);
    }

    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap.Entry<Integer, SurfaceView> highest = null;
                for (HashMap.Entry<Integer, SurfaceView> pair : mUidsList.entrySet()) {
                    log.debug("requestRemoteStreamType " + currentHostCount + " local " + (config().mUid & 0xFFFFFFFFL) + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getHeight() + " " + pair.getValue().getWidth());
                    if (pair.getKey() != config().mUid && (highest == null || highest.getValue().getHeight() < pair.getValue().getHeight())) {
                        if (highest != null) {
                            rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_LOW);
                            log.debug("setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                        }
                        highest = pair;
                    } else if (pair.getKey() != config().mUid && (highest != null && highest.getValue().getHeight() >= pair.getValue().getHeight())) {
                        rtcEngine().setRemoteVideoStreamType(pair.getKey(), Constants.VIDEO_STREAM_LOW);
                        log.debug("setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getWidth() + " " + pair.getValue().getHeight());
                    }
                }
                if (highest != null && highest.getKey() != 0) {
                    rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_HIGH);
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                }
            }
        }, 500);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                mUidsList.remove(uid);

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));

                if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();

                } else {
                    switchToSmallVideoView(bigBgUid);
                }
                Log.e("Com","排查正常直播提示错误----->1");
                if(!isBroadcaster(isAudience)){
                    ly_leaving.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null)
            mSmallVideoViewDock.setVisibility(View.GONE);
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), config().mUid, mUidsList);

        mViewType = VIEW_TYPE_DEFAULT;

        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                rtcEngine().setRemoteVideoStreamType(uid, Constants.VIDEO_STREAM_HIGH);
                log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
            }
        }
    }

    private void switchToSmallVideoView(int uid) {
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(uid, mUidsList.get(uid));
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), uid, slice);

        bindToSmallVideoView(uid);

        mViewType = VIEW_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    public int mViewType = VIEW_TYPE_DEFAULT;

    public static final int VIEW_TYPE_DEFAULT = 0;

    public static final int VIEW_TYPE_SMALL = 1;

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, exceptUid, mUidsList, new VideoViewEventListener() {
                @Override
                public void onItemDoubleClick(View v, Object item) {
                    switchToDefaultVideoView();
                }
            });
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        recycler.setAdapter(mSmallVideoViewAdapter);

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }


    /**
     * 给星星动画
     * @param v
     */
    private void StarFloating(final View v){

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(iv_star.getMeasuredWidth(), iv_star.getMeasuredHeight()));
        imageView.setImageResource(R.drawable.sugar);

        final FloatingElement floatingElement = new FloatingBuilder()
                .anchorView(v)
                .targetView(imageView)
                .floatingTransition(new StarFloating())
                .build();

        SpringHelper.createWithBouncinessAndSpeed(0f,1f,11,15).reboundListener(new ReboundListener() {
            @Override
            public void onReboundUpdate(double currentValue) {
                v.setScaleX((float) currentValue);
                v.setScaleY((float) currentValue);
            }

            @Override
            public void onReboundEnd() {
                mFloating.startFloating(floatingElement);
            }
        }).start();

    }

    /**
     * 飞机的动画
     * @param v
     */
    private void PlaneFloating(View v){

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(iv_paper_airplane.getMeasuredWidth(), iv_paper_airplane.getMeasuredHeight()));
        imageView.setImageResource(R.drawable.paper_airplane);

        FloatingElement floatingElement = new FloatingBuilder()
                .anchorView(v)
                .targetView(imageView)
                .floatingTransition(new PlaneFloating(mScreenHeight))
                .build();
        mFloating.startFloating(floatingElement);

    }
}
