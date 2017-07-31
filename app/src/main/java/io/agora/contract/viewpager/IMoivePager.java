package io.agora.contract.viewpager;

import java.util.ArrayList;

import io.agora.model.MediaItem;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/28
 * Description:
 */

public interface IMoivePager {

    public void getDataResult(int code,String result);
    public void processDataResult(int code, ArrayList<MediaItem> mediaItems);
}
