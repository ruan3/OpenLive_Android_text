package io.agora.presenter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.contract.ILoginView;
import io.agora.model.MyUser;
import rx.Subscriber;
import rx.subjects.Subject;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/4
 * Description:
 */

public class IloginRegisterPresenterImpl implements ILoginRegisterPresenter {

    ILoginView iLoginView;

    public IloginRegisterPresenterImpl(ILoginView iLoginView){
        this.iLoginView = iLoginView;
    }

    @SuppressLint("UseValueOf")
    @Override
    public void doLogin(String uid, String pwd) {

        final MyUser myUser = new MyUser();
        myUser.setUsername(uid);
        myUser.setPassword(pwd);
        myUser.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
            @Override
            public void onCompleted() {
                Log.i("Com","=====completed");
            }

            @Override
            public void onError(Throwable throwable) {
                iLoginView.doLoginResult(-1,new BmobException(throwable).toString());
            }

            @Override
            public void onNext(BmobUser bmobUser) {
                iLoginView.doLoginResult(0,bmobUser.getUsername());
                testGetCurrentUser();
            }
        });

    }

    /**
     * 获取本地用户
     */
    private void testGetCurrentUser() {
//		MyUser myUser = BmobUser.getCurrentUser(this, MyUser.class);
//		if (myUser != null) {
//			log("本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
//					+ ",age = "+ myUser.getAge());
//		} else {
//			toast("本地用户为null,请登录。");
//		}
        //V3.4.5版本新增加getObjectByKey方法获取本地用户对象中某一列的值
        String username = (String) BmobUser.getObjectByKey("username");
        Integer age = (Integer) BmobUser.getObjectByKey("age");
        Boolean sex = (Boolean) BmobUser.getObjectByKey("sex");
        JSONArray hobby= (JSONArray) BmobUser.getObjectByKey("hobby");
        JSONArray cards= (JSONArray) BmobUser.getObjectByKey("cards");
        JSONObject banker= (JSONObject) BmobUser.getObjectByKey("banker");
        JSONObject mainCard= (JSONObject) BmobUser.getObjectByKey("mainCard");
        Log.e("Com","username："+username+",\nage："+age+",\nsex："+ sex);
        Log.e("Com","hobby:"+(hobby!=null?hobby.toString():"为null")+"\ncards:"+(cards!=null ?cards.toString():"为null"));
        Log.e("Com","banker:"+(banker!=null?banker.toString():"为null")+"\nmainCard:"+(mainCard!=null ?mainCard.toString():"为null"));
    }

    @SuppressLint("UseValueOf")
    @Override
    public void doRegister(String uid, String pwd) {
        final MyUser myUser = new MyUser();
        myUser.setUsername(uid);
        myUser.setPassword(pwd);
        myUser.setAge(18);
        myUser.setEmail("1633486734@qq.com");
        myUser.setSex(true);
        myUser.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e==null){
                    iLoginView.doRegister(0,myUser.toString());
                }else{
                    Log.e("Com","注册失败："+e);
                    iLoginView.doRegister(-1,e.toString());
                }
            }
        });


    }

    @Override
    public void forgetPwd(final String number) {

        BmobUser.resetPasswordByEmail(number, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    iLoginView.doResetPwd(0,"重置密码请求成功，请到" + number + "邮箱进行密码重置操作");
                }else{
                    iLoginView.doResetPwd(-1,"重置密码失败："+e.toString());
                }
            }
        });

    }
}
