package io.agora.contract.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.agora.contract.adapter.ServiceAdapter;
import io.agora.contract.utils.LogUtils;
import io.agora.model.ChatBean;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;

/**
 * File Name:  客服中心（用图灵机器人搞事情）
 * Author:      ruan
 * Write Dates: 2017/8/15
 * Description:
 */

public class ServiceCenterActivity extends BaseActivity {

    private TuringManager mTuringManager;
//    private TTSManager mTtsManager;
    /**
     * 申请的turing的apikey（测试使用）
     * **/
    private final String TURING_APIKEY = "f8d6951792244a02bbdcc9197949e26d";
    /**
     * 申请的secret（测试使用）
     * **/
    private final String TURING_SECRET = "cf7d500b7bf7f976";

    EditText et_input;
    Button btn_send;
    RecyclerView serivce_recyclerView;

    ArrayList<ChatBean> chatBeans;
    ChatBean chatBean;
    String msg;
    ServiceAdapter serviceAdapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);
        context = this;
    }

    @Override
    protected void initUIandEvent() {

        et_input = (EditText) findViewById(R.id.et_input);
        btn_send = (Button) findViewById(R.id.btn_send);
        serivce_recyclerView = (RecyclerView) findViewById(R.id.serivce_recyclerView);

//        mTtsManager = new TTSManager(this, BD_APIKEY, BD_SECRET);
        mTuringManager = new TuringManager(this, TURING_APIKEY,
                TURING_SECRET);
        mTuringManager.setHttpRequestListener(myHttpConnectionListener);
//        mTtsManager.startTTS("你好啊");
        chatBeans = new ArrayList<>();
        chatBean = new ChatBean();
        chatBean.setType(1);
        chatBean.setMsg("客服：小软为您服务！工号007");
        chatBeans.add(chatBean);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = et_input.getText().toString();
                if(!TextUtils.isEmpty(msg)){

                    chatBean = new ChatBean();
                    chatBean.setType(0);
                    chatBean.setMsg(msg);
                    chatBeans.add(chatBean);
                    mTuringManager.requestTuring(msg);
                    serviceAdapter.notifyDataSetChanged();
                    if(chatBeans.size()>1){
                        serivce_recyclerView.smoothScrollToPosition(chatBeans.size()-1);
                    }
                    et_input.setText("");
                }else{
                    Toast.makeText(ServiceCenterActivity.this,"不能发空白消息！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        serviceAdapter = new ServiceAdapter(context,chatBeans);
        GridLayoutManager manager = new GridLayoutManager(context, 1);
        serivce_recyclerView.setLayoutManager(manager);
        serivce_recyclerView.setAdapter(serviceAdapter);
//        serivce_recyclerView.smoothScrollToPosition(chatBeans.size()-1);


    }

    @Override
    protected void deInitUIandEvent() {

    }

    /**
     * 退出按钮
     * @param view
     */
    public void back(View view){
        finish();
    }

    /**
     * 测试发送消息给图灵
     * @param view
     */
    public void sendMessageTOtulin(View view){

        mTuringManager.requestTuring("你好！图灵");

    }
    /*
     * 网络请求回调
     */
    HttpRequestListener myHttpConnectionListener = new HttpRequestListener() {

        @Override
        public void onSuccess(String result) {
            if (result != null) {
                try {
                    LogUtils.e("图灵机器人result" + result);
                    JSONObject result_obj = new JSONObject(result);
                    if (result_obj.has("text")) {
                        LogUtils.e("图灵机器人"+result_obj.get("text").toString());
                        msg = result_obj.get("text").toString();
                        chatBean = new ChatBean();
                        chatBean.setType(1);
                        chatBean.setMsg(msg);
                        chatBeans.add(chatBean);
                        serviceAdapter.notifyDataSetChanged();
                        if(chatBeans.size()>1){
                            serivce_recyclerView.smoothScrollToPosition(chatBeans.size()-1);
                        }
                    }
                } catch (JSONException e) {
                    LogUtils.e("图灵机器人---->JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onFail(int code, String error) {
            LogUtils.e("图灵机器人----->onFail code:" + code + "|error:" + error);
        }
    };

}
