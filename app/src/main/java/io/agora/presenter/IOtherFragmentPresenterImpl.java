package io.agora.presenter;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

import io.agora.contract.fragment.IOtherFragment;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.model.NetAudioPagerData;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/24
 * Description:
 */

public class IOtherFragmentPresenterImpl implements IOtherFragmentPresenter {

    RequestQueue mQueue;

    Context context;

    IOtherFragment iOtherFragment;

    public IOtherFragmentPresenterImpl(Context context,IOtherFragment iOtherFragment){
        this.context = context;
        mQueue = new Volley().newRequestQueue(context);
        this.iOtherFragment = iOtherFragment;
    }

    @Override
    public void getData() {

        StringRequest request = new StringRequest(Request.Method.GET, Constants.ALL_RES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                LogUtils.e("獲取到看看網絡數據："+s);
                iOtherFragment.getDataResult(0,s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("获取看看网络数据失败----->"+volleyError.toString());
                iOtherFragment.getDataResult(-1,volleyError.toString());
            }
        });

        mQueue.add(request);

    }

    @Override
    public void processData(String result) {

        //解析数据
        NetAudioPagerData data = new Gson().fromJson(result,NetAudioPagerData.class);
        /**
         * 页面的数据
         */
        List<NetAudioPagerData.ListEntity> datas;
        datas = data.getList();
        if(datas!=null&&datas.size()>0){

            LogUtils.e("看看页面数据解析成功");
            iOtherFragment.processDataResult(0,datas);

        }else{

            LogUtils.e("看看页面没有数据返回");
            iOtherFragment.processDataResult(-1,null);

        }

    }


}
