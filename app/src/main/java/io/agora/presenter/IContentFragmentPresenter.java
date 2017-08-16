package io.agora.presenter;

import io.agora.openlive.ui.ILiveRoomActivty;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/7
 * Description:
 */

public interface IContentFragmentPresenter {

    public void getData();
    public void setData();
    public void RealTimeCallBack();
    //直播页的方法
    public void getOnlinePerson(String uid, ILiveRoomActivty iLiveRoomActivty);
    public void RealTimeCallBack(ILiveRoomActivty iLiveRoomActivty);
}
