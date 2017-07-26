package io.agora.contract.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import java.util.List;

import io.agora.contract.adapter.OtherFragmentAdapter;
import io.agora.contract.utils.CacheUtils;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.model.NetAudioPagerData;
import io.agora.openlive.R;
import io.agora.presenter.IOtherFragmentPresenter;
import io.agora.presenter.IOtherFragmentPresenterImpl;

/**
 * File Name: 看看页面
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class OtherFragment extends BaseFragment implements IOtherFragment,SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout other_refreshLayout;
    RecyclerView other_recyclerView;
    FloatingActionButton other_floatBtn;
    IOtherFragmentPresenter iOtherFragmentPresenter;
    OtherFragmentAdapter otherFragmentAdapter;

    boolean isFirst = true;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_other,null);
        other_floatBtn = (FloatingActionButton) view.findViewById(R.id.other_floatBtn);
        other_recyclerView = (RecyclerView) view.findViewById(R.id.other_recyclerView);
        other_refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.other_refreshLayout);
        return view;
    }

    @Override
    public void initData() {


        iOtherFragmentPresenter = new IOtherFragmentPresenterImpl(context,this);
        if(isFirst){

            iOtherFragmentPresenter.getData();
            isFirst = false;
        }else{


            String savaJson = CacheUtils.getString(context, Constants.ALL_RES_URL);
            if(!TextUtils.isEmpty(savaJson)){
                //解析数据
//                iOtherFragmentPresenter.processData(savaJson);
            }

        }

        other_refreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        other_refreshLayout.setOnRefreshListener(this);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        other_recyclerView.setLayoutManager(manager);
//        other_recyclerView.setHasFixedSize(true);
        other_recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public String setTitile() {
        return "看看";
    }

    @Override
    public void getDataResult(int code, String result) {
        other_refreshLayout.setRefreshing(false);
        if(code == 0){
            LogUtils.e("获取看看网路数据--->"+result);

            //保存数据
            CacheUtils.putString(context, Constants.ALL_RES_URL, result);
            iOtherFragmentPresenter.processData(result);
        }else{
            LogUtils.e("获取看看网络数据失败---->"+result);
        }


    }

    @Override
    public void processDataResult(int code, List<NetAudioPagerData.ListEntity> datas) {
        other_refreshLayout.setRefreshing(false);
        if(code == 0){

            LogUtils.e("解析看看json数据成功---->"+datas.size());
            otherFragmentAdapter = new OtherFragmentAdapter(context,datas);
            other_recyclerView.setAdapter(otherFragmentAdapter);


        }else{

            LogUtils.e("解析看看json数据失败");

        }

    }

    @Override
    public void onRefresh() {
        other_refreshLayout.setRefreshing(true);
        iOtherFragmentPresenter.getData();

    }
}
