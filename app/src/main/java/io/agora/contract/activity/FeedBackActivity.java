package io.agora.contract.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import io.agora.contract.adapter.FeedBackAdapter;
import io.agora.model.FeedBackBean;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;
import io.agora.presenter.IFeedBackPresenterImpl;

/**
 * File Name:   意见反馈界面
 * Author:      ruan
 * Write Dates: 2017/8/30
 * Description:
 */

public class FeedBackActivity extends BaseActivity implements IFeedBack {

    RecyclerView rv_feedback;
    EditText et_comment_content;
    Button btn_send;
    MyUser myUser;
    String content;
    Context context;
    IFeedBackPresenterImpl iFeedBackPresenter;
    List<FeedBackBean> feedBackBeens;
    FeedBackAdapter feedBackAdapter;
    MKLoader loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        context = this;
    }

    @Override
    protected void initUIandEvent() {

        iFeedBackPresenter = new IFeedBackPresenterImpl(this);
        feedBackBeens = new ArrayList<>();

        rv_feedback = (RecyclerView) findViewById(R.id.rv_feedback);
        et_comment_content = (EditText) findViewById(R.id.et_feed_back);
        loading = (MKLoader) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        btn_send = (Button) findViewById(R.id.btn_send);
        myUser = BmobUser.getCurrentUser(MyUser.class);//获取本地用户
        GridLayoutManager manager = new GridLayoutManager(context, 1);
        rv_feedback.setLayoutManager(manager);
        iFeedBackPresenter.getData("");
        rv_feedback.setItemAnimator(new DefaultItemAnimator());
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                content = et_comment_content.getText().toString();


                if(!TextUtils.isEmpty(content)){

                    FeedBackBean feedBackBean = new FeedBackBean();

                    /**
                     * 通过判断传入头像地址
                     */
                    if(!TextUtils.isEmpty(myUser.getAuthIconUrl())){
                        //不为空才传入第三方头像链接
                        feedBackBean.setIcon_url(myUser.getAuthIconUrl());
                    }else if(myUser.getHead_icon()!=null){
                        //不为空时传入本地注册头像链接
                        feedBackBean.setIcon_url(myUser.getHead_icon().getUrl());
                    }else{
                        //当都为空时，传入空
                        feedBackBean.setIcon_url("");
                    }
                    String currentTime= DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date()).toString();//当前更新时间
                    feedBackBean.setTime(currentTime);
                    feedBackBean.setContent(content);
                    feedBackBean.setName(myUser.getUser_id_name());
                    iFeedBackPresenter.updataFeedbck(feedBackBean);
                    feedBackBeens.add(feedBackBean);
                    if(feedBackAdapter!=null){
                        //不为空时刷新界面
                        feedBackAdapter.notifyDataSetChanged();
                    }else{
                        iFeedBackPresenter.getData("");
                    }
                    et_comment_content.setText("");//发表成功后，设置输入框为空
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//收起软键盘
                    imm.hideSoftInputFromWindow(et_comment_content.getWindowToken(), 0) ;
                }else{
                    Toast.makeText(context,"输入内容为空！",Toast.LENGTH_SHORT).show();
                }




            }
        });

    }

    @Override
    protected void deInitUIandEvent() {

        feedBackBeens = null;
        feedBackAdapter = null;
        iFeedBackPresenter = null;


    }

    @Override
    public void updataResult(int code, String result) {

        if(code == 0){
            //更新成功
        }

    }

    @Override
    public void getDataResult(int code, List<FeedBackBean> feedBackBeen) {
        loading.setVisibility(View.GONE);
        if(code == 0){
            //获取数据成功
            feedBackBeens.addAll(feedBackBeen);
            if(feedBackAdapter == null){

                feedBackAdapter = new FeedBackAdapter(context,feedBackBeens);
                rv_feedback.setAdapter(feedBackAdapter);
            }else{
                feedBackAdapter.notifyDataSetChanged();
            }
        }

    }

    public void back(View view){
        finish();
    }
}
