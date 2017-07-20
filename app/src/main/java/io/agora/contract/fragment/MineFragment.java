package io.agora.contract.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.bmob.v3.BmobUser;
import io.agora.contract.activity.MineSettingActivity;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.view.CircleImageView;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;
import io.agora.openlive.R;
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

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_mine,null);
        setting = (RadioButton) view.findViewById(R.id.setting);
        avatar = (CircleImageView) view.findViewById(R.id.avatar);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_like = (TextView) view.findViewById(R.id.tv_like);
        tv_gift = (TextView) view.findViewById(R.id.tv_gift);
        tv_live = (TextView) view.findViewById(R.id.tv_live);
        return view;
    }

    @Override
    public void initData() {

        iMineFragmentPresenter = new IMineFragmentPresentImpl(this);
        iMineFragmentPresenter.getData();

        //点击设置跳转到个人信息设置界面
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MineSettingActivity.class);
                context.startActivity(intent);
            }
        });

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user.getHead_icon()!=null){

            Glide.with(context).load(user.getHead_icon().getUrl()).into(avatar);
        }
        if(!TextUtils.isEmpty(user.getUser_id_name())){
            tv_user_name.setText(user.getUser_id_name().toString());
        }


    }

    @Override
    public String setTitile() {
        return "我的";
    }

    @Override
    public void getData(int code, String e, LiveVideos liveVideos) {

        if(code == 0){

            LogUtils.e("获取到对应信息---->"+liveVideos.getLikes());
            tv_like.setText("喜欢："+liveVideos.getLikes());
            tv_live.setText("直播："+liveVideos.getLiveTimes());
            tv_gift.setText("礼物："+liveVideos.getGift_times());

        }else{
            LogUtils.e("获取失败------>"+e);
        }

    }
}
