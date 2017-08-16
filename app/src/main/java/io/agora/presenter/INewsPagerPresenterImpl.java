package io.agora.presenter;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.agora.contract.utils.Apis;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.viewpager.INewsPager;
import io.agora.model.NBABean;
import io.agora.model.NewsBean;
import io.agora.model.NewsEntity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/31
 * Description:
 */

public class INewsPagerPresenterImpl implements INewsPresenter {

    INewsPager iNewsPager;
    Context context;
    RequestQueue mQueue;
    NewsBean newsBean;
    NBABean nbaBean;
    ArrayList<NewsEntity> newsEntities;
    int type;
    public INewsPagerPresenterImpl(Context context,INewsPager iNewsPager){

        this.context = context;
        this.iNewsPager = iNewsPager;
        mQueue = new Volley().newRequestQueue(context);

    }

    @Override
    public void getData(int type,int pageIndex) {

        this.type = type;

        StringRequest request = new StringRequest(Request.Method.GET, getUrl(pageIndex,type), new Response.Listener<String>() {
            @Override
            public void onResponse(Request<String> request, String s) {
                LogUtils.e("獲取到热点網絡數據："+s);
                iNewsPager.getDataResult(0,s);
                processData(s);
            }

//            @Override
//            public void onResponse(String s) {
//
//                LogUtils.e("獲取到热点網絡數據："+s);
//                iNewsPager.getDataResult(0,s);
//                processData(s);
//            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Request request, VolleyError volleyError) {
                LogUtils.e("获取热点网络数据失败----->"+volleyError.toString());
                iNewsPager.getDataResult(-1,volleyError.toString());
            }

//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                LogUtils.e("获取热点网络数据失败----->"+volleyError.toString());
//                iNewsPager.getDataResult(-1,volleyError.toString());
//            }
        });

        mQueue.add(request);


    }

    @Override
    public void processData(String result) {
        Gson gson = new Gson();
        if(type == 0){
            newsBean = gson.fromJson(result,NewsBean.class);
            newsEntities = newsBean.getT1348647909107();
            if(newsEntities!=null&&newsEntities.size()>0){

                LogUtils.e("解析热点页成功---->"+newsEntities.get(1).getTitle()+"大小为----->"+newsEntities.size());

                iNewsPager.processDataResult(0,newsEntities);

            }else{

                LogUtils.e("解析热点页失败---");
                iNewsPager.processDataResult(-1,null);

            }
        }else if(type == 1){

            nbaBean = gson.fromJson(result,NBABean.class);
            newsEntities = nbaBean.getT1348649145984();
            if(newsEntities!=null&&newsEntities.size()>0){

                LogUtils.e("解析NBA页成功---->"+newsEntities.get(1).getTitle()+"大小为----->"+newsEntities.size());

                iNewsPager.processDataResult(0,newsEntities);

            }else{

                LogUtils.e("解析NBA页失败---");
                iNewsPager.processDataResult(-1,null);

            }

        }


    }

    private String getUrl(int page,int type) {

        StringBuilder sb=new StringBuilder();
        if(type == 0){

            sb.append(Apis.TOP_URL).append(Apis.TOP_ID);

        }else if(type == 1){
            sb.append(Apis.COMMON_URL).append(Apis.NBA_ID);
        }
        sb.append("/").append(page).append(Apis.END_URL);
        return sb.toString();
    }

}
