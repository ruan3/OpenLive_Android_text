package io.agora.contract;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.ChangeBounds;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.util.DisplayMetrics;
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

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.ui.HomeActivity;
import io.agora.openlive.ui.MainActivity;
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

    MKLoader loading;

    ILoginRegisterPresenter loginPresenter;


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
                loginPresenter.forgetPwd("1633486734@qq.com");
            }
        });
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
        }else{
            Toast.makeText(context,"登录失败"+result,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void doRegister(int code, String result) {
        loading.setVisibility(View.GONE);
        if(code == 0){
            Toast.makeText(context,"注册成功"+result,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"注册失败"+result,Toast.LENGTH_SHORT).show();
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


}
