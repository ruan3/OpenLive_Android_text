package io.agora.contract.activity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/19
 * Description:
 */

public interface IMineSetting {

    public void updateIconSuccess(String s);
    public void updateIconFalid(String e);
    public void UpdateUserName(int code,String e);
    public void LogOut(int code);

}
