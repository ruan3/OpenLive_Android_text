package io.agora.contract.viewpager;

import java.util.ArrayList;

import io.agora.model.MediaItem;
import io.agora.model.NewsEntity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/31
 * Description:
 */

public interface INewsPager {

    public void getDataResult(int code,String result);
    public void processDataResult(int code, ArrayList<NewsEntity> newsEntities);

}
