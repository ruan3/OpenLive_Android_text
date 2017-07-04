package io.agora.presenter;

import android.widget.Toast;

import io.agora.contract.ILoginView;

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

    @Override
    public void doLogin(String uid, String pwd) {
        iLoginView.doLoginResult(0,"dddd");
    }

    @Override
    public void doRegister(String uid, String pwd) {
        iLoginView.doRegister(0,"dddd");
    }

    @Override
    public void forgetPwd(String number) {

    }
}
