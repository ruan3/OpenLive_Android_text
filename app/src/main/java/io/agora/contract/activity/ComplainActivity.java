package io.agora.contract.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;
import io.agora.model.ComplainBean;
import io.agora.model.EventMsg;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.BaseActivity;
import io.agora.presenter.IComplainPresenter;
import io.agora.presenter.IComplainPresenterImpl;

/**
 * File Name:   举报界面
 * Author:      ruan
 * Write Dates: 2017/9/4
 * Description:
 */

public class ComplainActivity extends BaseActivity implements IComplain{

    TextView tv_complain_user;
    EditText et_complain_content;
    Button btn_submit;
    String liveId;
    IComplainPresenter icpt;
    String BroadcastID;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        context = this;
    }

    @Override
    protected void initUIandEvent() {

        icpt = new IComplainPresenterImpl(this);
        Intent intent = getIntent();
        final String BroasdName = intent.getStringExtra(ConstantApp.ACTION_KEY_BROCAST_NAME);
        liveId = intent.getStringExtra(ConstantApp.ACTION_KEY_LIVE_ID);
        BroadcastID =  intent.getStringExtra(ConstantApp.ACTION_KEY_ROOM_NAME);
        tv_complain_user = (TextView) findViewById(R.id.tv_complain_user);
        et_complain_content = (EditText) findViewById(R.id.et_complain_content);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_complain_content.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    ComplainBean complainBean = new ComplainBean();
                    complainBean.setContent(content);
                    complainBean.setBroadcastName(BroasdName);
                    complainBean.setComplainName(BmobUser.getCurrentUser(MyUser.class).getUser_id_name());
                    complainBean.setBroadcastId(BmobUser.getCurrentUser(MyUser.class).getObjectId());
                    complainBean.setBroadcastId(BroadcastID);
                    icpt.UpdataComplainContent(complainBean);
                    Toast.makeText(context,"举报提交成功，我们将会审核，通过会给您回复！",Toast.LENGTH_LONG).show();
                    //通知直播列表页可以更新了
                    EventMsg eventMsg = new EventMsg();
                    eventMsg.setMsg("liveRoomClose");
                    EventBus.getDefault().post(eventMsg);

                }else{
                    Toast.makeText(context,"举报内容不可为空！",Toast.LENGTH_LONG).show();
                }
            }
        });

        tv_complain_user.setText("主播:"+BroasdName);

    }

    @Override
    protected void deInitUIandEvent() {

    }

    @Override
    public void saveResult(int code, String result) {

        if(code == 0){
            //提交成功
            icpt.UpdataLiveRoom(liveId);
        }

    }
}
