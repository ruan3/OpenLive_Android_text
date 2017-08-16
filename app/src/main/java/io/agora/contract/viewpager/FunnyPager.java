package io.agora.contract.viewpager;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.RequestQueue;

import java.util.List;

import io.agora.contract.adapter.OtherFragmentAdapter;
import io.agora.contract.fragment.IOtherFragment;
import io.agora.contract.utils.CacheUtils;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.model.NetAudioPagerData;
import io.agora.openlive.R;
import io.agora.presenter.IOtherFragmentPresenter;
import io.agora.presenter.IOtherFragmentPresenterImpl;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/28
 * Description:
 */

public class FunnyPager extends BasePager implements IOtherFragment,SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout other_refreshLayout;
    RecyclerView other_recyclerView;
    FloatingActionButton other_floatBtn;
    IOtherFragmentPresenter iOtherFragmentPresenter;
    OtherFragmentAdapter otherFragmentAdapter;
    boolean isFirst = true;


    Context context;

    public FunnyPager(Context context){
        this.context = context;

    }

    public View initView(){

        LogUtils.e("funnyInitView()");
        View view = View.inflate(context, R.layout.fragment_other,null);
        other_floatBtn = (FloatingActionButton) view.findViewById(R.id.other_floatBtn);
        other_recyclerView = (RecyclerView) view.findViewById(R.id.other_recyclerView);
        other_refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.other_refreshLayout);
        initData();
        return view;

    }

    public void initData() {

        LogUtils.e("funnpager---->InitData()");
        iOtherFragmentPresenter = new IOtherFragmentPresenterImpl(context,this);
        iOtherFragmentPresenter.getData();
        if(isFirst){
            LogUtils.e("funnypager------>getData()");
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
        GridLayoutManager manager = new GridLayoutManager(context, 1);
        other_recyclerView.setLayoutManager(manager);
//        other_recyclerView.setHasFixedSize(true);
        other_recyclerView.setItemAnimator(new DefaultItemAnimator());

        /**
         * 浮点监听
         */
        other_floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(context, SearchActivity.class);
//                startActivity(intent);
                other_recyclerView.smoothScrollToPosition(0);

            }
        });

    }

    @Override
    public void getDataResult(int code, String result) {


        other_refreshLayout.setRefreshing(false);
        if(code == 0){
            LogUtils.e("获取funnypage-->网路数据--->"+result);

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

            LogUtils.e("解析funnpager---->json数据成功---->"+datas.size());
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
