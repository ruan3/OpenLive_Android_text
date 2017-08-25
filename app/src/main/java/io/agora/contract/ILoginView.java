package io.agora.contract;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/4
 * Description:
 */

public interface ILoginView {

    public void doLoginResult(int code,String result);
    public void doRegister(int code,String result);
    public void doResetPwd(int code,String reslut);
    public void onComplete(Platform platform , int i, HashMap<String, Object> hashMap);
    public void onError(Platform platform, int i, Throwable throwable);
    public void onCancel(Platform platform, int i);
}
