package io.agora.contract;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.ChangeBounds;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import io.agora.contract.utils.LogUtils;
import io.agora.model.EventMsg;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.ui.HomeActivity;
import io.agora.presenter.ILoginRegisterPresenter;
import io.agora.presenter.IloginRegisterPresenterImpl;

/**
 * File Name:   登录注册
 * Author:      ruan
 * Write Dates: 2017/7/4
 * Description: 登录注册view
 */

public class LoginActivity extends Activity implements ILoginView{

    EditText login_email;
    EditText login_pass;
    TextView forget;
    TextView login;
    ImageView backImg;
    EditText register_email;
    EditText register_pass;
    EditText confirm_pass;
    TextView signUp;
    LinearLayout.LayoutParams paramsLogin;
    LinearLayout.LayoutParams paramsRegister;
    FrameLayout.LayoutParams frameParams;
    ObjectAnimator animator1,animator2;
    RelativeLayout relativeLayout;
    RelativeLayout relativeLayout2;
    LinearLayout mainLinear,img;
    private ImageView logo;
    FrameLayout mainFrame;
    private Context context;
    ImageView iv_qq;
    ImageView iv_sina;
    ImageView iv_wechat;

    MKLoader loading;

    ILoginRegisterPresenter loginPresenter;

    private static final int COMPLETE = 0;//第三方登录完成
    private static final int ERROR = 1;//第三方登录失败
    private static final int CANCEL = 2;//取消第三方登录
    Handler uiHandler;//更新ui
    Platform platform;
    String platformName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        context = this;
        //初始化控件
        initView();

        /**
         * 设置点击动画
         */
        AnimationTranslation();

    }

    private void initView() {
        paramsLogin = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        paramsRegister = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        frameParams = new FrameLayout.LayoutParams(inDp(50),inDp(50));

        register_email = (EditText) findViewById(R.id.register_email);
        register_pass = (EditText) findViewById(R.id.register_pass);
        signUp = (TextView) findViewById(R.id.signUp);
        login_email = (EditText) findViewById(R.id.login_email);
        login_pass = (EditText) findViewById(R.id.login_pass);
        login = (TextView) findViewById(R.id.login);
        backImg = (ImageView) findViewById(R.id.backImg);
        confirm_pass = (EditText) findViewById(R.id.confirm_pass);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relative2);
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        img = (LinearLayout) findViewById(R.id.img);
        forget = (TextView) findViewById(R.id.forget);
        loading = (MKLoader) findViewById(R.id.loading);

        iv_sina = (ImageView) findViewById(R.id.iv_sina);
        iv_qq = (ImageView) findViewById(R.id.iv_qq);
        iv_wechat = (ImageView) findViewById(R.id.iv_wechat);



        logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setLayoutParams(frameParams);

        loginPresenter = new IloginRegisterPresenterImpl(this);

        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            // 允许用户使用应用
            //BmobUser中的特定属性
            String username = (String) BmobUser.getObjectByKey("username");
            //MyUser中的扩展属性
            String password = (String) BmobUser.getObjectByKey("password");
            login_email.setText(username);
            login_pass.setText(password);
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…

        }

        /**
         * 新浪的第三方登录
         */
        iv_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loginPresenter.sinaLogin(SinaWeibo.NAME);
            }
        });

        /**
         * 微信的第三方登录
         */
        iv_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loginPresenter.sinaLogin(Wechat.NAME);
            }
        });

        /**
         * QQ的第三方登录
         */
        iv_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loginPresenter.sinaLogin(QQ.NAME);
            }
        });

    }


    private void AnimationTranslation() {
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                logo.setX(relativeLayout2.getRight() / 2);
                logo.setY(inDp(50));
                mainFrame.addView(logo);
            }
        });

        paramsRegister.weight = (float)0.75;
        paramsLogin.weight = (float) 4.25;

        mainLinear.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainLinear.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainFrame.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;
                if(keypadHeight > screenHeight * 0.15){

                    if(paramsRegister.weight == 4.25){
                        animator1 = ObjectAnimator.ofFloat(backImg,"scaleX",(float)1.95);
                        animator2 = ObjectAnimator.ofFloat(backImg,"scaleY",(float)1.95);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1,animator2);
                        set.setDuration(1000);
                        set.start();
                    }else{
                        animator1 = ObjectAnimator.ofFloat(backImg,"scaleX",(float) 1.75);
                        animator2 = ObjectAnimator.ofFloat(backImg,"scaleY",(float)1.75);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1,animator2);
                        set.setDuration(500);
                        set.start();
                    }

                }else{

                    animator1 = ObjectAnimator.ofFloat(backImg,"scaleX",(float)3);
                    animator2 = ObjectAnimator.ofFloat(backImg,"scaleY",(float)3);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(animator1,animator2);
                    set.setDuration(500);
                    set.start();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(paramsRegister.weight == 4.25){

                    loading.setVisibility(View.VISIBLE);

                    Snackbar.make(relativeLayout,"注册搞事情！",Snackbar.LENGTH_SHORT).show();
                    loginPresenter.doRegister(register_email.getText().toString(),register_pass.getText().toString());
                    return;

                }

                register_email.setVisibility(View.VISIBLE);
                register_pass.setVisibility(View.VISIBLE);
                confirm_pass.setVisibility(View.VISIBLE);

                final ChangeBounds bounds = new ChangeBounds();
                bounds.setDuration(1500);
                bounds.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(@NonNull Transition transition) {

                        ObjectAnimator animator1 = ObjectAnimator.ofFloat(signUp,"translationX",mainLinear.getWidth() / 2 - relativeLayout2.getWidth() / 2 - signUp.getWidth() / 2);
                        ObjectAnimator animator2 = ObjectAnimator.ofFloat(img,"translationX",-relativeLayout2.getX());
                        ObjectAnimator animator3 = ObjectAnimator.ofFloat(signUp,"rotation",0);

                        ObjectAnimator animator4 = ObjectAnimator.ofFloat(login_email,"alpha",1,0);
                        ObjectAnimator animator5 = ObjectAnimator.ofFloat(login_pass,"alpha",1,0);
                        ObjectAnimator animator6 = ObjectAnimator.ofFloat(forget,"alpha",1,0);

                        ObjectAnimator animator7 = ObjectAnimator.ofFloat(login,"rotation",90);
                        ObjectAnimator animator8 = ObjectAnimator.ofFloat(login,"y",relativeLayout2.getHeight() / 2);
                        ObjectAnimator animator9 = ObjectAnimator.ofFloat(register_email,"alpha",0,1);

                        ObjectAnimator animator10 = ObjectAnimator.ofFloat(confirm_pass,"alpha",0,1);
                        ObjectAnimator animator11 = ObjectAnimator.ofFloat(register_pass,"alpha",0,1);
                        ObjectAnimator animator12 = ObjectAnimator.ofFloat(signUp,"y",login.getY());

                        ObjectAnimator animator13 = ObjectAnimator.ofFloat(backImg,"translationX",img.getX());
                        ObjectAnimator animator14 = ObjectAnimator.ofFloat(signUp,"scaleX",2);
                        ObjectAnimator animator15 = ObjectAnimator.ofFloat(signUp,"scaleY",2);

                        ObjectAnimator animator16 = ObjectAnimator.ofFloat(login,"scaleX",1);
                        ObjectAnimator animator17 = ObjectAnimator.ofFloat(login,"scaleY",1);
                        ObjectAnimator animator18 = ObjectAnimator.ofFloat(logo,"x",relativeLayout2.getRight() / 2 - relativeLayout.getRight());

                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1,animator2,animator3,animator4,animator5,animator6,
                                animator7,animator8,animator9,animator10,animator11,animator12,
                                animator13,animator14,animator15,animator16,animator17,animator18);
                        set.setDuration(1500);
                        set.start();

                    }

                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {

                        login_email.setVisibility(View.INVISIBLE);
                        login_pass.setVisibility(View.INVISIBLE);
                        forget.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onTransitionCancel(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(@NonNull Transition transition) {

                    }
                });

                TransitionManager.beginDelayedTransition(mainLinear,bounds);

                paramsRegister.weight = (float) 4.25;
                paramsLogin.weight = (float) 0.75;

                relativeLayout.setLayoutParams(paramsRegister);
                relativeLayout2.setLayoutParams(paramsLogin);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(paramsLogin.weight == 4.25){
                    loading.setVisibility(View.VISIBLE);
                    Snackbar.make(relativeLayout2,"登录进去了，搞事情！",Snackbar.LENGTH_SHORT).show();
                    loginPresenter.doLogin(login_email.getText().toString(),login_pass.getText().toString());
                    return;
                }
                login_email.setVisibility(View.VISIBLE);
                login_pass.setVisibility(View.VISIBLE);
                forget.setVisibility(View.VISIBLE);

                final ChangeBounds bounds = new ChangeBounds();
                bounds.setDuration(1500);
                bounds.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(@NonNull Transition transition) {


                        ObjectAnimator animator1 = ObjectAnimator.ofFloat(login, "translationX", mainLinear.getWidth() / 2 - relativeLayout.getWidth() / 2 - login.getWidth() / 2);
                        ObjectAnimator animator2 = ObjectAnimator.ofFloat(img, "translationX", (relativeLayout.getX()));
                        ObjectAnimator animator3 = ObjectAnimator.ofFloat(login, "rotation", 0);

                        ObjectAnimator animator4 = ObjectAnimator.ofFloat(login_email, "alpha", 0, 1);
                        ObjectAnimator animator5 = ObjectAnimator.ofFloat(login_pass, "alpha", 0, 1);
                        ObjectAnimator animator6 = ObjectAnimator.ofFloat(forget, "alpha", 0, 1);

                        ObjectAnimator animator7 = ObjectAnimator.ofFloat(signUp, "rotation", 90);
                        ObjectAnimator animator8 = ObjectAnimator.ofFloat(signUp, "y", relativeLayout.getHeight() / 2);
                        ObjectAnimator animator9 = ObjectAnimator.ofFloat(register_email, "alpha", 1, 0);

                        ObjectAnimator animator10 = ObjectAnimator.ofFloat(confirm_pass, "alpha", 1, 0);
                        ObjectAnimator animator11 = ObjectAnimator.ofFloat(register_pass, "alpha", 1, 0);
                        ObjectAnimator animator12 = ObjectAnimator.ofFloat(login, "y", signUp.getY());

                        ObjectAnimator animator13 = ObjectAnimator.ofFloat(backImg, "translationX", -img.getX());
                        ObjectAnimator animator14 = ObjectAnimator.ofFloat(login, "scaleX", 2);
                        ObjectAnimator animator15 = ObjectAnimator.ofFloat(login, "scaleY", 2);

                        ObjectAnimator animator16 = ObjectAnimator.ofFloat(signUp, "scaleX", 1);
                        ObjectAnimator animator17 = ObjectAnimator.ofFloat(signUp, "scaleY", 1);
                        ObjectAnimator animator18 = ObjectAnimator.ofFloat(logo, "x", logo.getX()+relativeLayout2.getWidth());


                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7,
                                animator8, animator9, animator10, animator11, animator12, animator13, animator14, animator15, animator16, animator17,animator18);
                        set.setDuration(1500).start();

                    }

                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {


                        register_email.setVisibility(View.INVISIBLE);
                        register_pass.setVisibility(View.INVISIBLE);
                        confirm_pass.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onTransitionCancel(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(@NonNull Transition transition) {

                    }
                });

                TransitionManager.beginDelayedTransition(mainLinear,bounds);
                paramsRegister.weight = (float) 0.75;
                paramsLogin.weight = (float) 4.25;

                relativeLayout.setLayoutParams(paramsRegister);
                relativeLayout2.setLayoutParams(paramsLogin);
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPresenter.forgetPwd(null,context);
            }
        });

        //uiHandler在主线程中创建，所以自动绑定主线程
        uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case COMPLETE:
                        Toast.makeText(context,"第三方登录成功",Toast.LENGTH_SHORT).show();
                        final PlatformDb platDB = platform.getDb();//获取数平台数据DB

                        //通过DB获取各种数据
//                        platDB.getToken();
//                        platDB.getUserGender();
//                        platDB.getUserIcon();
//                        platDB.getUserId();
//                        platDB.getUserName();
                        LogUtils.e("第三方用户信息:"+platDB.getPlatformNname()+"头像--->"+platDB.getUserIcon());
                        if(platDB.getPlatformNname().equals("SinaWeibo")){
                            //bmob 在接收平台字段有规定，所以得判断一下
                                platformName = "weibo";
                        }else if(platDB.getPlatformNname().equals("QQ")){
                                platformName = "qq";
                        }
                        //获取到第三方授权登录后，实现bmob的一键登录
                        BmobUser.BmobThirdUserAuth thirdUserAuth = new BmobUser.BmobThirdUserAuth(platformName, platDB.getToken(),platDB.getExpiresIn()+"",platDB.getUserId());
                        LogUtils.e("Bmob第三方登录对象getSnsType----->"+thirdUserAuth.getSnsType()+"token--->"+thirdUserAuth.getAccessToken()+"expirestime--->"
                                +thirdUserAuth.getExpiresIn()+"userID--->"+thirdUserAuth.getUserId());
                        //快速登录
                        BmobUser.loginWithAuthData(thirdUserAuth, new LogInListener<JSONObject>() {
                            @Override
                            public void done(JSONObject jsonObject, BmobException e) {

                                if(e==null){
                                    LogUtils.e("Bmob第三方登录成功--->"+jsonObject.toString());
                                    MyUser bmobUser =  BmobUser.getCurrentUser(MyUser.class);
                                    if(bmobUser!=null){
                                        LogUtils.e("是否有本地用户---->"+bmobUser.getUsername());
                                        bmobUser.setUser_id_name(platDB.getUserName());
                                        bmobUser.setAuthIconUrl(platDB.getUserIcon());
                                        bmobUser.update(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                loading.setVisibility(View.GONE);
                                                if(e!=null){
                                                    LogUtils.e("更换昵称失败----->"+e.toString());
                                                }else{
                                                    LogUtils.e("更换昵称成功");
                                                    Intent intent = new Intent(context,HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                                    }else{
                                        LogUtils.e("第三方登录没有保存到本地用户！");
                                    }
                                }else{
                                    LogUtils.e("Bmob第三方登录失败---->"+e.toString());
                                }

                            }
                        });
//                        EventBus.getDefault().post(platDB);
//                        Intent intent = new Intent(context,HomeActivity.class);
////                        intent.putExtra("userName",platDB.getUserName());
////                        intent.putExtra("userIconUrl",platDB.getUserIcon());
//                        startActivity(intent);
//                        finish();
                        break;
                    case ERROR:
                        loading.setVisibility(View.GONE);
                        Toast.makeText(context,"第三方登录失败",Toast.LENGTH_SHORT).show();
                        break;
                    case CANCEL:
                        loading.setVisibility(View.GONE);
                        Toast.makeText(context,"第三方登录取消",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }

    private int inDp(int dp) {

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    @Override
    public void doLoginResult(int code, String result) {
        loading.setVisibility(View.GONE);
        if(code == 0){
            Toast.makeText(context,"登录成功"+result,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context,HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(context,"登录失败"+result,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void doRegister(int code, String result) {
        loading.setVisibility(View.GONE);
        if(code == 0){
            Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
            login.performClick();
            login_email.setText(register_email.getText().toString());
            login_pass.setText(register_pass.getText().toString());
        }else{
            Toast.makeText(context,"注册失败",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void doResetPwd(int code, String reslut) {
        loading.setVisibility(View.GONE);
        if(code == 0){
            Toast.makeText(context,reslut,Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context,reslut,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        this.platform = platform;
        uiHandler.sendEmptyMessage(COMPLETE);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

        uiHandler.sendEmptyMessage(ERROR);

    }

    @Override
    public void onCancel(Platform platform, int i) {

        uiHandler.sendEmptyMessage(CANCEL);

    }


}
