package io.agora.contract.activity;

import java.util.ArrayList;

import io.agora.model.NewsDetialEntity;
import io.agora.model.NewsEntity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/1
 * Description:
 */

public interface INewsDetail {

    public void getDataResult(int code ,String result);

    public void processDataResult(int code, NewsDetialEntity newsDetialEntity);
}
