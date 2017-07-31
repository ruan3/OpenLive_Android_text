package io.agora.contract.viewpager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import io.agora.contract.activity.SystemVideoPlayer;
import io.agora.contract.adapter.MoiveAdapter;
import io.agora.contract.utils.LogUtils;
import io.agora.model.MediaItem;
import io.agora.openlive.R;
import io.agora.presenter.IMoivePagerPresenter;
import io.agora.presenter.IMoivePagerPresenterImpl;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/28
 * Description:
 */

public class MoivePager extends BasePager implements IMoivePager,SwipeRefreshLayout.OnRefreshListener{

    ListView listview;
    TextView tv_nonet;
    ProgressBar pb_loading;
    IMoivePagerPresenter moivePagerPresenter;
    MoiveAdapter moiveAdapter;
    ArrayList<MediaItem> mediaItems;
    Context context;
    SwipeRefreshLayout moive_pager_refreshLayout;

    public MoivePager(Context context) {
        this.context = context;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_moive,null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_nonet = (TextView) view.findViewById(R.id.tv_nonet);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        moive_pager_refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.moive_pager_refreshLayout);
        initData();
        return view ;
    }

    @Override
    public void initData() {
        moive_pager_refreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        moive_pager_refreshLayout.setOnRefreshListener(this);
        pb_loading.setVisibility(View.VISIBLE);
        moivePagerPresenter = new IMoivePagerPresenterImpl(context,this);
        moivePagerPresenter.getData();//获取网络数据
        if(mediaItems!=null){

            moiveAdapter = new MoiveAdapter(context,mediaItems);
//            listview.setAdapter(moiveAdapter);
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //3.传递列表数据-对象-序列化
                Intent intent = new Intent(context,SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist",mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public void getDataResult(int code, String result) {
        pb_loading.setVisibility(View.GONE);
        moive_pager_refreshLayout.setRefreshing(false);
        if(code == 0){

            LogUtils.e("获取电影数据成功---->"+result);
            moivePagerPresenter.processData(result);

        }else{
            LogUtils.e("获取电影数据失败----->"+result);
        }

    }

    @Override
    public void processDataResult(int code, ArrayList<MediaItem> mediaItems) {

        if(code == 0){


            this.mediaItems = mediaItems;
            moiveAdapter = new MoiveAdapter(context,mediaItems);
            LogUtils.e("解析电影数据成功----->"+mediaItems.size()+"单个数据---->"+mediaItems.get(2).getMovieName());
            listview.setAdapter(moiveAdapter);
//            moiveAdapter.notifyDataSetChanged();
        }else{

            LogUtils.e("解析电影数据失败----->");

        }

    }

    @Override
    public void onRefresh() {

        moivePagerPresenter.getData();

    }
}
