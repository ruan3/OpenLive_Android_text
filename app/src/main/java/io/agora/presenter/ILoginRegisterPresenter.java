package io.agora.presenter;

import android.content.Context;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/4
 * Description:
 */

public interface ILoginRegisterPresenter {

    public void doLogin(String uid,String pwd);
    public void doRegister(String uid,String pwd);
    public void forgetPwd(String number,Context context);
    public void sinaLogin(String PlatformName);
}
