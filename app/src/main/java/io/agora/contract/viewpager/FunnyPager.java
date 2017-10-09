package io.agora.contract.viewpager;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import io.agora.contract.adapter.OtherFragmentAdapter;
import io.agora.contract.fragment.IOtherFragment;
import io.agora.contract.utils.CacheUtils;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.view.EndLessOnScrollListener;
import io.agora.contract.view.MyJCVideoPlayerStandard;
import io.agora.model.NetAudioPagerData;
import io.agora.openlive.R;
import io.agora.presenter.IOtherFragmentPresenter;
import io.agora.presenter.IOtherFragmentPresenterImpl;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

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

    int page = 0;
    int pageIndex = 40;

    GridLayoutManager manager;
    Context context;

    List<NetAudioPagerData.ListEntity> mDatas;

    int VisiableCount;//可见条目
    int firstItem;//第一个可见条目
    int LastItem;//最后一个可见条目

    MyJCVideoPlayerStandard videoView;//列表中视频对象

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
        iOtherFragmentPresenter.getData(page,pageIndex);
        if(isFirst){
            LogUtils.e("funnypager------>getData()");
//            iOtherFragmentPresenter.getData(page,pageIndex);
//            page = pageIndex;
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
        manager = new GridLayoutManager(context, 1);
        other_recyclerView.setLayoutManager(manager);
//        other_recyclerView.setHasFixedSize(true);
        other_recyclerView.setItemAnimator(new DefaultItemAnimator());


        other_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                switch (newState){
                    case SCROLL_STATE_IDLE://停止滚动
                        LogUtils.e("recyclerView滚动状态---->停止滚动");
                        /**在这里执行，视频的自动播放与停止*/
                        autoPlayVideo(recyclerView);
                        break;
                    case SCROLL_STATE_DRAGGING://拖动
                        LogUtils.e("recyclerView滚动状态---->拖动");
                        break;
                    case SCROLL_STATE_SETTLING://惯性滑动
                        LogUtils.e("recyclerView滚动状态---->惯性滑动");
                        break;
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LogUtils.e("recyclerView滚动距离---->x==="+dx+"-----y==="+dy);
                firstItem = manager.findFirstVisibleItemPosition();
                LastItem = manager.findLastVisibleItemPosition();
                VisiableCount = LastItem - firstItem;
                LogUtils.e("recyclerView可见条目---->第一个==="+firstItem+"-----第二个==="+LastItem+"可显示总数--->"+VisiableCount);

            }
        });


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


    /**
     * 自动播放停止视频方法
     */
    private void autoPlayVideo(RecyclerView recyclerView){


        for(int i=0;i<VisiableCount;i++){
            if(recyclerView != null && recyclerView.getChildAt(i) != null &&recyclerView.getChildAt(i).findViewById(R.id.jcv_videoplayer) != null){
                videoView = (MyJCVideoPlayerStandard) recyclerView.getChildAt(i).findViewById(R.id.jcv_videoplayer);
                Rect rect = new Rect();
                videoView.getLocalVisibleRect(rect);
                int videoHeight = videoView.getHeight();
                if(rect.top==0&&rect.bottom==videoHeight){

                    if(videoView.currentState== JCVideoPlayer.CURRENT_STATE_NORMAL||videoView.currentState==JCVideoPlayer.CURRENT_STATE_PAUSE){

                        videoView.startVideo();

                    }
                    return;
                }

                videoView.releaseAllVideos();

            }else{
                if(videoView!=null){
                    videoView.releaseAllVideos();
                }
            }

        }

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
            if(otherFragmentAdapter==null){
                otherFragmentAdapter = new OtherFragmentAdapter(context,datas);
                other_recyclerView.setAdapter(otherFragmentAdapter);
            }else{
                if(datas!=null){
                    otherFragmentAdapter.addAll(datas);
//                    otherFragmentAdapter.notifyDataSetChanged();
                }
            }

            LogUtils.e("解析funnpager---->json数据成功---->"+datas.size());
            otherFragmentAdapter.addFooterView(R.layout.layout_footer_funny);
            other_recyclerView.addOnScrollListener(new EndLessOnScrollListener(manager) {
                @Override
                public void onLoadMore() {
                    otherFragmentAdapter.setFooterVisible(View.VISIBLE);
                    LogUtils.e("funnpage请求更多走不走---->"+pageIndex);
//                    iOtherFragmentPresenter.getData(page,pageIndex+10);
//                    page = pageIndex;
                }
            });

        }else{

            LogUtils.e("解析看看json数据失败");

        }


    }

    @Override
    public void onRefresh() {

        other_refreshLayout.setRefreshing(true);
        LogUtils.e("娱乐界面更新加载！");
        iOtherFragmentPresenter.getData(0,20);

    }
}
