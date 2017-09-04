package io.agora.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import io.agora.contract.view.CircleImageView;
import io.agora.model.FeedBackBean;
import io.agora.openlive.R;

/**
 * File Name:   意见反馈页面适配器
 * Author:      ruan
 * Write Dates: 2017/8/31
 * Description:
 */

public class FeedBackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private LayoutInflater mLayoutInflater;
    List<FeedBackBean> feedBackBeen;
    String url;

    public FeedBackAdapter(Context context, List<FeedBackBean> feedBackBeen){
        this.context = context;
        this.feedBackBeen = feedBackBeen;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DetailViewHolder(mLayoutInflater.inflate(R.layout.item_comment,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DetailViewHolder detailViewHolder = (DetailViewHolder)holder;
        detailViewHolder.setData(feedBackBeen.get(position));
    }

    @Override
    public int getItemCount() {
        return feedBackBeen.size();
    }

    /**
     * 评论详情页面
     */
    class DetailViewHolder extends RecyclerView.ViewHolder{

        TextView tv_comment_name;
        TextView tv_comment_time;
        TextView tv_comment_content;
        CircleImageView cv_comment_icon;
        TextView tv_like;
        String url;

        public DetailViewHolder(View itemView) {
            super(itemView);
            tv_comment_name = (TextView) itemView.findViewById(R.id.tv_comment_name);
            tv_comment_time = (TextView) itemView.findViewById(R.id.tv_comment_time);
            cv_comment_icon = (CircleImageView) itemView.findViewById(R.id.cv_comment_icon);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
        }

        public void setData(FeedBackBean feedBackBean){
            url = feedBackBean.getIcon_url();
            tv_comment_time.setText(feedBackBean.getTime());
            tv_comment_name.setText(feedBackBean.getName());
            tv_comment_content.setText(feedBackBean.getContent());
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(cv_comment_icon);


        }

    }

}
