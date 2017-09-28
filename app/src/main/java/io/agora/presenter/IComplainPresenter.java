package io.agora.presenter;

import io.agora.model.ComplainBean;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/9/4
 * Description:
 */

public interface IComplainPresenter {

    public void UpdataComplainContent(ComplainBean complainBean);

    public void UpdataLiveRoom(String liveId);

}
