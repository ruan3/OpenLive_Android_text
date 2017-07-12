package io.agora.contract.fragment;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

import io.agora.contract.activity.CreateRoomActivity;
import io.agora.contract.adapter.ContentAdapter;
import io.agora.model.LiveVideos;
import io.agora.openlive.R;
import io.agora.presenter.IContentFragmentPresenter;
import io.agora.presenter.IContentFragmentPresenterImpl;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class ContentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,IContentFragment {


    private ContentAdapter mAdapter;
    private List<LiveVideos> videos = new ArrayList<>();
    private int pageIndex = 0;
    SwipeRefreshLayout mRefreshLayout;

    RecyclerView mRecyclerView;
    IContentFragmentPresenter iContentFragmentPresenter;
    FloatingActionButton floatBtn;
    MKLoader loading;
    TextView tv_noData_tips;
    FrameLayout fl_content;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_content,null);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        floatBtn = (FloatingActionButton) view.findViewById(R.id.floatBtn);
        loading = (MKLoader) view.findViewById(R.id.loading);
        tv_noData_tips = (TextView) view.findViewById(R.id.tv_noData_tips);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        return view;
    }

    /**
     * 数据初始化
     */
    @Override
    public void initData() {

        Log.e("Com","ContentFragment------->InitData()");

        loading.setVisibility(View.VISIBLE);
        tv_noData_tips.setVisibility(View.GONE);
        iContentFragmentPresenter = new IContentFragmentPresenterImpl(this);
        iContentFragmentPresenter.getData();
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRefreshLayout.setOnRefreshListener(this);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateRoomActivity.class);
                startActivity(intent);
            }
        });

       tv_noData_tips.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
               tv_noData_tips.setVisibility(View.GONE);
               iContentFragmentPresenter.getData();
           }
       });
        iContentFragmentPresenter.RealTimeCallBack();

    }

    @Override
    public String setTitile() {
        return "直播";
    }

    /**
     * mRefreshLayout更新操作
     */
    @Override
    public void onRefresh() {
        iContentFragmentPresenter.getData();
    }

    /**
     * 获取到返回的数据
     * @param code
     * @param videos
     */
    @Override
    public void getDatas(int code, List<LiveVideos> videos) {
        mRefreshLayout.setRefreshing(false);
        mAdapter = new ContentAdapter(context,videos);
        mRecyclerView.setAdapter(mAdapter);
        if(code == 0){
            tv_noData_tips.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
           for(LiveVideos liveVideos : videos){
               Log.e("Com","获取数据成功--->"+liveVideos.getLiveTitle());
               Log.e("Comg","数据的大小为----->"+videos.size());
           }

        }else if(code == 1){
            videos.clear();
//            mAdapter.notifyDataSetChanged();
            loading.setVisibility(View.GONE);
            tv_noData_tips.setVisibility(View.VISIBLE);
            tv_noData_tips.setText("暂时没有直播");
            Log.e("Com","沒有列表数据展示");
            mAdapter.notifyDataSetChanged();

        }

    }

    /**
     * 写入数据，暂时还没有用到
     * @param code
     * @param result
     */
    @Override
    public void setDataResult(int code, String result) {
        if(code == 0){
            Toast.makeText(context,"保存成功-----"+result,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 数据获取失败操作
     * @param code
     * @param errorMsg
     */
    @Override
    public void getDataFailed(int code, String errorMsg) {
        mRefreshLayout.setRefreshing(false);
        if(code < 0){
            loading.setVisibility(View.GONE);
            Log.e("Com","获取直播列表数据失败------->"+errorMsg);
            tv_noData_tips.setVisibility(View.VISIBLE);
            tv_noData_tips.setText("获取数据失败，点击屏幕重试！");
//            Snackbar.make(mRecyclerView,"获取列表数据失败---->"+errorMsg,Snackbar.LENGTH_LONG).show();
            Toast.makeText(context,"获取列表数据失败---->"+errorMsg,Toast.LENGTH_LONG).show();
            mAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 监听数据实时更新
     * @param code
     * @param videos
     */
    @Override
    public void RealTimeCallBack(int code, List<LiveVideos> videos) {
            //监听到数据更新后重新加载
            iContentFragmentPresenter.getData();
    }
}
