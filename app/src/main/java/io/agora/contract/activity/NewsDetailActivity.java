package io.agora.contract.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import io.agora.contract.utils.LogUtils;
import io.agora.model.NewsDetialEntity;
import io.agora.model.NewsEntity;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;
import io.agora.presenter.INewsDetailPresenter;
import io.agora.presenter.INewsDetialPresenterImpl;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/1
 * Description:
 */

public class NewsDetailActivity extends BaseActivity implements INewsDetail{

    CollapsingToolbarLayout collapsing_toolbar;
    ImageView ivImage;
    Toolbar toolbar;
    HtmlTextView htNewsContent;

    INewsDetailPresenter newsDetailPresenter;
    Context context;
    private NewsEntity mEntity;//详情数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        context = this;
    }

    @Override
    protected void initUIandEvent() {
        Intent intent = getIntent();
        mEntity = (NewsEntity) getIntent().getSerializableExtra("news");
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        htNewsContent = (HtmlTextView) findViewById(R.id.htNewsContent);
        newsDetailPresenter = new INewsDetialPresenterImpl(context,this);
        newsDetailPresenter.getData(mEntity.getDocid());
        toolbar.setTitle(mEntity.getTitle());
        Glide.with(context).load(mEntity.getImgsrc())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_image_loading)
                .into(ivImage);
    }

    @Override
    protected void deInitUIandEvent() {

    }

    @Override
    public void getDataResult(int code, String result) {

        if(code == 0){
            LogUtils.d("获取到新闻详情也数据------>"+result);
        }else{
            LogUtils.d("获取到新闻详情数据出错------>"+result);
        }

    }

    @Override
    public void processDataResult(int code, NewsDetialEntity newsDetialEntity) {

        if(code == 0){
            LogUtils.d("解析新闻具体数据成功----->");
            htNewsContent.setHtmlFromString(newsDetialEntity.getBody(), new HtmlTextView.LocalImageGetter());
        }else{
            LogUtils.d("解析新闻具体数据失败----->");
        }

    }
}
