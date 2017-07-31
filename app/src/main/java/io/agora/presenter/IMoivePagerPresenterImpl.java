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

import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.viewpager.IMoivePager;
import io.agora.model.MediaItem;
import io.agora.model.NewVideoPagerBean;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/28
 * Description:
 */

public class IMoivePagerPresenterImpl implements IMoivePagerPresenter {

    Context context;
    RequestQueue mQueue;
    IMoivePager iMoivePager;
    NewVideoPagerBean newVideoPagerBean;
    ArrayList<MediaItem> mediaItems;

    public IMoivePagerPresenterImpl(Context context,IMoivePager iMoivePager){

        this.context = context;
        this.iMoivePager = iMoivePager;
        mQueue = new Volley().newRequestQueue(context);

    }

    @Override
    public void getData() {

        StringRequest request = new StringRequest(Request.Method.GET, Constants.NET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                LogUtils.e("獲取到电影網絡數據："+s);
                iMoivePager.getDataResult(0,s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("获取电影网络数据失败----->"+volleyError.toString());
                iMoivePager.getDataResult(-1,volleyError.toString());
            }
        });

        mQueue.add(request);

    }

    @Override
    public void processData(String result) {

        Gson gson = new Gson();

        newVideoPagerBean = gson.fromJson(result,NewVideoPagerBean.class);
        mediaItems = newVideoPagerBean.getTrailers();
        if(mediaItems!=null&&mediaItems.size()>0){
            LogUtils.e("电影页面数据解析成功---->"+mediaItems.get(2).toString());
            iMoivePager.processDataResult(0,mediaItems);

        }else{

            LogUtils.e("电影页面没有数据返回");
            iMoivePager.processDataResult(-1,null);

        }


    }


}
