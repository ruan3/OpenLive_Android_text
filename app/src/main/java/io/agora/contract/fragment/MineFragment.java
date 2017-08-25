package io.agora.contract.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;
import io.agora.contract.activity.MineSettingActivity;
import io.agora.contract.activity.ServiceCenterActivity;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.utils.Utils;
import io.agora.contract.view.CircleImageView;
import io.agora.model.EventMsg;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.presenter.IContentFragmentPresenter;
import io.agora.presenter.IContentFragmentPresenterImpl;
import io.agora.presenter.IMineFragmentPresentImpl;
import io.agora.presenter.IMineFragmentPresenter;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class MineFragment extends BaseFragment implements  IMineFragment{

    private RadioButton setting;
    private CircleImageView avatar;
    private TextView tv_user_name;
    IMineFragmentPresenter iMineFragmentPresenter;
    TextView tv_like;
    TextView tv_live;
    TextView tv_gift;
    LinearLayout ll_service;
    LinearLayout ll_update;
    TextView tv_app_version;

    private boolean isFirst = true;

    int like;
    int live;
    int gift;

    @Override
    public View initView() {
//        EventBus.getDefault().register(this);
        View view = View.inflate(context, R.layout.fragment_mine,null);
        setting = (RadioButton) view.findViewById(R.id.setting);
        avatar = (CircleImageView) view.findViewById(R.id.avatar);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_like = (TextView) view.findViewById(R.id.tv_like);
        tv_gift = (TextView) view.findViewById(R.id.tv_gift);
        tv_live = (TextView) view.findViewById(R.id.tv_live);
        ll_service = (LinearLayout) view.findViewById(R.id.ll_service);
        tv_app_version = (TextView) view.findViewById(R.id.tv_app_version);
        ll_update = (LinearLayout) view.findViewById(R.id.ll_update);
        return view;
    }

    @Override
    public void initData() {


        tv_like.setText("喜欢："+like);
        tv_live.setText("直播："+live);
        tv_gift.setText("礼物："+gift);
        tv_app_version.setText(Utils.getVersionName(context)+"");
        if(isFirst&&BmobUser.getCurrentUser()!=null){
            iMineFragmentPresenter = new IMineFragmentPresentImpl(this);
            iMineFragmentPresenter.getData();
            iMineFragmentPresenter.RealTimeCallBack();
            isFirst = false;
        }

        //点击设置跳转到个人信息设置界面
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MineSettingActivity.class);
                context.startActivity(intent);
            }
        });

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user!=null){

            if(user.getHead_icon()!=null){

                Glide.with(context).load(user.getHead_icon().getUrl()).into(avatar);
            }else{
                Glide.with(context).load(user.getAuthIconUrl()).into(avatar);
            }
            if(!TextUtils.isEmpty(user.getUser_id_name())){
                tv_user_name.setText(user.getUser_id_name().toString());
            }
        }
        //客服中心按钮
        ll_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ServiceCenterActivity.class);
                startActivity(intent);

            }
        });

        //应用更新
        ll_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUpdateAgent.forceUpdate(context);
            }
        });

    }

    @Override
    public String setTitile() {
        return "我的";
    }

    @Override
    public void getData(int code, String e, LiveVideos liveVideos) {

        if(code == 0){

            LogUtils.e("获取到对应信息---->"+liveVideos.getLikes());
            like = liveVideos.getLikes();
            live = liveVideos.getLiveTimes();
            gift = liveVideos.getGift_times();
            tv_like.setText("喜欢："+liveVideos.getLikes());
            tv_live.setText("直播："+liveVideos.getLiveTimes());
            tv_gift.setText("礼物："+liveVideos.getGift_times());

        }else{
            LogUtils.e("获取失败------>"+e);
        }

    }

    @Override
    public void RealTimeCallBack(int code, List<LiveVideos> videos) {

        if(iMineFragmentPresenter!=null){

            iMineFragmentPresenter.getData();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
    }

}
