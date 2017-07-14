package io.agora.contract.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.bmob.v3.BmobUser;
import io.agora.contract.LoginActivity;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;
import io.agora.openlive.ui.HomeActivity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/7
 * Description:
 */

public class SplaseActivity extends BaseActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splase);
    }

    @Override
    protected void initUIandEvent() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后才执行到这里
                //执行在主线程中
                Intent intent;
                BmobUser bmobUser = BmobUser.getCurrentUser();
                if(bmobUser != null){
                    // 允许用户使用应用
                    intent = new Intent(SplaseActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else{
                    //缓存用户对象为空时， 可打开用户注册界面…
                    intent = new Intent(SplaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                }


                finish();
                Log.e("Spalse", "当前线程名称==" + Thread.currentThread().getName());
            }
        }, 2000);

    }

    @Override
    protected void deInitUIandEvent() {

    }
}
