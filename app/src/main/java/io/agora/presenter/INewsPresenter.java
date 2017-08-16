package io.agora.presenter;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/31
 * Description:
 */

public interface INewsPresenter {

    public void getData(int type,int pageIndex);

    public void processData(String result);

}
