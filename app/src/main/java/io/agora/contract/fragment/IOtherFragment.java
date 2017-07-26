package io.agora.contract.fragment;

import java.util.List;

import io.agora.model.NetAudioPagerData;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/24
 * Description:
 */

public interface IOtherFragment {

    public void getDataResult(int code,String result);
    public void processDataResult(int code,List<NetAudioPagerData.ListEntity> datas);

}
