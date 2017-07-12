package io.agora.contract.fragment;

import java.util.List;

import io.agora.model.LiveVideos;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/7
 * Description:
 */

public interface IContentFragment {

    public void getDatas(int code,List<LiveVideos> videos);
    public void setDataResult(int code,String result);
    public void getDataFailed(int code, String errorMsg);
    public void RealTimeCallBack(int code,List<LiveVideos> videos);

}
