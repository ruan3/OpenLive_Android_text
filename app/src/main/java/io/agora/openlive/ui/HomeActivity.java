package io.agora.openlive.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import eu.long1.spacetablayout.SpaceTabLayout;
import io.agora.contract.fragment.ContentFragment;
import io.agora.contract.fragment.MineFragment;
import io.agora.contract.fragment.NewsFragment;
import io.agora.contract.fragment.OtherFragment;
import io.agora.contract.utils.CacheUtils;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.utils.Utils;
import io.agora.model.EventMsg;
import io.agora.openlive.R;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description: 登录完成后进入的主界面
 */

public class HomeActivity extends BaseActivity {

    List<Fragment> fragments;
    SpaceTabLayout tabLayout;
    TextView tv_title;
    MKLoader loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_home);
//        BmobUpdateAgent.update(this);//正常的更新
        BmobUpdateAgent.silentUpdate(this);
        final Utils utils = new Utils();

        final String picPath = CacheUtils.getString(this,"ExceptionUrl");
        if(!TextUtils.isEmpty(picPath)){
            /**
             * 如果有异常发生，上传异常文件
             */
            BmobFile bmobFile = new BmobFile(new File(picPath));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        LogUtils.e("上传异常文件成功！");
                        if(utils.deleteFile(picPath)){
                            LogUtils.e("删除异常文件成功！");
                            CacheUtils.putString(HomeActivity.this,"ExceptionUrl","");
                        }
                    }else{
                        LogUtils.e("上传异常文件失败--->"+e.toString());
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                    LogUtils.e("上传文件进度--->"+value);
                }
            });
        }else{
            LogUtils.e("没有获取到异常文件路径");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Com","主页面start()"+EventBus.TAG);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Com","主页面stop()");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg eventMsg){

        Log.e("Com","EventBus主页面接受到消息----->"+eventMsg.getMsg());
        if(eventMsg!=null){

            if("getOut".equals(eventMsg.getMsg())){
                finish();
            }
        }

    }


    @Override
    protected void initUIandEvent() {

        fragments = new ArrayList<>();
        fragments.add(new ContentFragment());
//        fragments.add(new OtherFragment());
        fragments.add(new NewsFragment());
        fragments.add(new MineFragment());

        loading = (MKLoader) findViewById(R.id.loading);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);
        tabLayout.initialize(viewPager,getSupportFragmentManager(),fragments);
        tabLayout.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });
        tabLayout.setTabOneText("直播");
        tabLayout.setTabOneIcon(R.drawable.icon_video);
        tabLayout.setTabTwoText("看看");
        tabLayout.setTabTwoIcon(R.drawable.other);
        tabLayout.setTabThreeText("我");
        tabLayout.setTabThreeIcon(R.drawable.mine);
        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "" + tabLayout.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void deInitUIandEvent() {

    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 主界面不需要支持滑动返回，重写该方法永久禁用当前界面的滑动返回功能
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
