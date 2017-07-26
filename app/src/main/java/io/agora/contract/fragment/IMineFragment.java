package io.agora.contract.fragment;

import java.util.List;

import io.agora.model.LiveVideos;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/20
 * Description:
 */

public interface IMineFragment {

    public void getData(int code, String e, LiveVideos liveVideos);
    public void RealTimeCallBack(int code,List<LiveVideos> videos);

}
