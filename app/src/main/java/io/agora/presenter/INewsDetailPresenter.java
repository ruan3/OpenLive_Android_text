package io.agora.presenter;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/1
 * Description:
 */

public interface INewsDetailPresenter {

    public void getData(String docid);

    public void processData(String result,String docid);
}
