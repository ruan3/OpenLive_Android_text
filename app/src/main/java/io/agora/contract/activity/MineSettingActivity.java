package io.agora.contract.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.smile.filechoose.api.ChooserType;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import cn.bmob.v3.BmobUser;
import io.agora.contract.LoginActivity;
import io.agora.contract.view.CircleImageView;
import io.agora.model.EventMsg;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;
import io.agora.presenter.IMineSettingPresenter;
import io.agora.presenter.IMineSettingPresenterImpl;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/19
 * Description:
 */

public class MineSettingActivity extends BaseActivity implements FileChooserListener,IMineSetting {

    CircleImageView iv_head_icon;
    RelativeLayout rl_set_headIcon;
    TextView tv_set_userName;
    RelativeLayout rl_login_out;
    RelativeLayout rl_set_name;
    EditText et_set_userName;

    FileChooserManager fm;
    IMineSettingPresenter iMineSettingPresenter;
    ChosenFile choosedFile;
    Context context;

    private boolean isShowEdt = false;

    String updateName;//更新后的名字


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_setting);
        context = this;
    }

    @Override
    protected void initUIandEvent() {

        iv_head_icon = (CircleImageView) findViewById(R.id.avatar);
        rl_set_headIcon = (RelativeLayout) findViewById(R.id.rl_set_headIcon);
        tv_set_userName = (TextView) findViewById(R.id.tv_set_userName);
        rl_login_out = (RelativeLayout) findViewById(R.id.rl_login_out);
        rl_set_name = (RelativeLayout) findViewById(R.id.rl_set_name);
        et_set_userName = (EditText) findViewById(R.id.et_set_userName);
        fm = new FileChooserManager(this);
        iMineSettingPresenter = new IMineSettingPresenterImpl(fm,this);

        rl_set_headIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosedFile == null){
                    pickFile();
                }
            }
        });

        rl_set_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isShowEdt){
                    et_set_userName.setVisibility(View.GONE);
                    tv_set_userName.setVisibility(View.VISIBLE);
                    isShowEdt = false;
                }else{
                    et_set_userName.setVisibility(View.VISIBLE);
                    tv_set_userName.setVisibility(View.GONE);
                    isShowEdt = true;
                }


            }
        });

        et_set_userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(v.getText().toString())) {
                        return false;
                    }
                    updateName = v.getText().toString();
                    iMineSettingPresenter.upDateUserName(updateName);

                    v.setText("");

                    return true;
                }
                return false;
            }
        });

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user.getHead_icon()!=null){

            Glide.with(context).load(user.getHead_icon().getUrl()).into(iv_head_icon);
        }
        if(!TextUtils.isEmpty(user.getUser_id_name())){

            tv_set_userName.setText(user.getUser_id_name());
        }

        rl_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMineSettingPresenter.loginOut(context);
            }
        });

    }

    public void pickFile(){
        iMineSettingPresenter.pickFile(this);
    }

    @Override
    protected void deInitUIandEvent() {

    }

    /**
     * 重写文件选择方法
     * @param chosenFile
     */
    @Override
    public void onFileChosen(ChosenFile chosenFile) {


        choosedFile = chosenFile;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("life", choosedFile.getFilePath());
                File Image = new File(choosedFile.getFilePath());
                iMineSettingPresenter.uploadHeadIcon(Image,context);
//                iv_head_icon.setImageURI(Uri.parse(choosedFile.getFilePath()));
//                Glide.with(context).load(Uri.parse(choosedFile.getFilePath())).into(iv_head_icon);
            }
        });

    }

    /**
     *
     * @param s
     */
    @Override
    public void onError(String s) {

        Log.e("Com","选择头像文件报错了----->"+s);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
                fm.setFileChooserListener(this);
            }
            Log.i("Com", "Probable file size: " + fm.queryProbableFileSize(data.getData(), this));
            fm.submit(requestCode, data);
//            iv_head_icon.setImageURI(data.getData());
            Glide.with(context).load(data.getData()).into(iv_head_icon);
        }

        if (requestCode == 0x1 && resultCode == RESULT_OK)
            super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void updateIconSuccess(String s) {
        choosedFile = null;
        Log.e("Com","个人头像上传成功");
    }

    @Override
    public void updateIconFalid(String e) {
        Log.e("Com","个人头像上传失败----->"+e);
        Toast.makeText(context,"更新头像失败"+e,Toast.LENGTH_LONG).show();
        choosedFile = null;
    }

    @Override
    public void UpdateUserName(int code, String e) {

        if(code == 0){
            Log.e("Com","更新名字成功！");
            tv_set_userName.setText(updateName);
        }else{
            Log.e("Com","更新名字失败---->"+e);
            Toast.makeText(context,"更新昵称失败"+e,Toast.LENGTH_LONG).show();
        }
        et_set_userName.setVisibility(View.GONE);
        tv_set_userName.setVisibility(View.VISIBLE);

    }

    @Override
    public void LogOut(int code) {

        if(code == 0){
            EventMsg eventMsg = new EventMsg();
            eventMsg.setMsg("getOut");
            EventBus.getDefault().post(eventMsg);
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Subscribe
    public void Event(String msg){

        Log.e("Com","EventBus接受到消息----->"+msg);

    }
}
