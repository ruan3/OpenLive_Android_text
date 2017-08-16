package io.agora.contract.viewpager;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import io.agora.contract.activity.NewsDetailActivity;
import io.agora.contract.adapter.NewsAdapter;
import io.agora.contract.utils.Apis;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.view.BaseViewHolder;
import io.agora.contract.view.EndLessOnScrollListener;
import io.agora.model.NewsEntity;
import io.agora.openlive.R;
import io.agora.presenter.INewsPagerPresenterImpl;
import io.agora.presenter.INewsPresenter;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/31
 * Description:
 */

public class NewsPager extends BasePager implements INewsPager,SwipeRefreshLayout.OnRefreshListener{

    Context context;
    SwipeRefreshLayout news_refreshLayout;
    RecyclerView news_recyclerView;
    FloatingActionButton news_floatBtn;
    INewsPresenter newsPresenter;
    private NewsAdapter mAdapter;
    ArrayList<NewsEntity> mnewsEntities;
    private int pageIndex = 0;
    GridLayoutManager manager;
    int type;

    public NewsPager(Context context,int type){
            this.context = context;
            this.type = type;
    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.pager_news,null);
        news_refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_refreshLayout);
        news_recyclerView = (RecyclerView) view.findViewById(R.id.news_recyclerView);
        news_floatBtn = (FloatingActionButton) view.findViewById(R.id.news_floatBtn);
        initData();
        return view;
    }

    @Override
    public void initData() {

        newsPresenter = new INewsPagerPresenterImpl(context,this);
        newsPresenter.getData(type,pageIndex);

        news_refreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        news_refreshLayout.setOnRefreshListener(this);
        manager = new GridLayoutManager(context, 1);
        news_recyclerView.setLayoutManager(manager);
//        other_recyclerView.setHasFixedSize(true);
        news_recyclerView.setItemAnimator(new DefaultItemAnimator());

//        mAdapter = new NewsAdapter(newsEntities,context);


        news_floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                news_recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    @Override
    public void getDataResult(int code, String result) {
        news_refreshLayout.setRefreshing(false);
        if(code == 0){

            LogUtils.d("获取到数据---->"+result);

        }

    }

    @Override
    public void processDataResult(int code, final ArrayList<NewsEntity> newsEntities) {


        if(mAdapter==null){
            mnewsEntities = newsEntities;
            mAdapter = new NewsAdapter(newsEntities,context);
            news_recyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
            if(newsEntities!=null){

                mnewsEntities.addAll(newsEntities);
            }
        }

        mAdapter.addFooterView(R.layout.layout_footer);
        news_recyclerView.addOnScrollListener(new EndLessOnScrollListener(manager) {
            @Override
            public void onLoadMore() {
                pageIndex += Apis.PAZE_SIZE;
                mAdapter.setFooterVisible(View.VISIBLE);
                newsPresenter.getData(type, pageIndex);
            }
        });

        mAdapter.setOnItemClickLitener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseViewHolder viewHolder) {
                NewsEntity entity = mnewsEntities.get(position);
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("news", entity);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public void onRefresh() {
        newsPresenter.getData(type,pageIndex);
    }
}
