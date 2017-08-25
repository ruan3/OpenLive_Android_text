package io.agora.openlive.ui;

import java.util.List;

import io.agora.model.LiveVideos;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/14
 * Description:
 */

public interface ILiveRoomActivty {

    public void getOnlineNumber(LiveVideos videos);
    public void RealTimeCallBack(int code,List<LiveVideos> videos);
    public void UpdataLikeAndGif(int code,String result);
    public void getDataResult(LiveVideos videos);
}
