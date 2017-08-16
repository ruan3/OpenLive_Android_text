package io.agora.presenter;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import io.agora.contract.activity.INewsDetail;
import io.agora.contract.utils.Apis;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.utils.NewsJsonUtils;
import io.agora.model.NewsDetialEntity;
import io.vov.vitamio.utils.Log;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/1
 * Description:
 */

public class INewsDetialPresenterImpl implements INewsDetailPresenter{

    Context context;
    RequestQueue mQueue;
    INewsDetail newsDetail;

    public INewsDetialPresenterImpl(Context context, INewsDetail newsDetail){

        this.context = context;
        this.newsDetail = newsDetail;
        mQueue = new Volley().newRequestQueue(context);

    }

    @Override
    public void getData(final String docid) {

        StringRequest request = new StringRequest(Request.Method.GET, getDetailUrl(docid), new Response.Listener<String>() {
            @Override
            public void onResponse(Request<String> request, String s) {
                LogUtils.i("獲取到新闻详情網絡數據："+s);
                newsDetail.getDataResult(0,s);
                processData(s,docid);
            }

//            @Override
//            public void onResponse(String s) {
//
//                LogUtils.i("獲取到新闻详情網絡數據："+s);
//                newsDetail.getDataResult(0,s);
//                processData(s,docid);
//            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Request request, VolleyError volleyError) {
                LogUtils.i("获取新闻详情网络数据失败----->"+volleyError.toString());
                newsDetail.getDataResult(-1,volleyError.toString());
            }

//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                LogUtils.i("获取新闻详情网络数据失败----->"+volleyError.toString());
//                newsDetail.getDataResult(-1,volleyError.toString());
//            }
        });

        mQueue.add(request);

    }

    @Override
    public void processData(String result,String docid) {


        NewsDetialEntity newsDetailBean = NewsJsonUtils.readJsonNewsDetailBeans(result, docid);
        if(newsDetailBean!=null){
            LogUtils.e("新闻内容---->"+newsDetailBean.toString());
            newsDetail.processDataResult(0,newsDetailBean);

        }else{
            LogUtils.e("新闻内容为空---->"+newsDetailBean);
            newsDetail.processDataResult(-1,null);
        }

    }

    private String getDetailUrl(String docId) {
        StringBuffer sb = new StringBuffer(Apis.NEW_DETAIL);
        sb.append(docId).append(Apis.END_DETAIL_URL);
        return sb.toString();
    }
}
