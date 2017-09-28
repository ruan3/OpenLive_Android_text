package io.agora.contract.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.shuyu.frescoutil.FrescoHelper;

import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import io.agora.contract.utils.HighLightKeyWordUtil;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.view.CircleImageView;
import io.agora.contract.view.MyJCVideoPlayerStandard;
import io.agora.model.CommentBean;
import io.agora.openlive.R;
import pl.droidsonroids.gif.GifImageView;

/**
 * File Name:  评论页面的适配器
 * Author:      ruan
 * Write Dates: 2017/8/21
 * Description:
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    /**
     * 当前类型
     */
    public int currentType = TYPE_IMAGE;

    private int TYPE_TOP = 5;
    private int TYPE_COMMENT = 6;
    private int TYPE_CURRENT = TYPE_TOP;

    Context context;
    private LayoutInflater mLayoutInflater;
    ArrayList<CommentBean> commentBeans;
    String url;

    private OnItemClickListener mOnItemClickListener;

    public CommentAdapter(Context context,ArrayList<CommentBean> commentBeans){
        this.context = context;
        this.commentBeans = commentBeans;
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_TOP){
            //头部布局初始化
            return new TopViewHolder(mLayoutInflater.inflate(R.layout.item_comment_top,null));
        }else if(viewType == TYPE_COMMENT){
            return new DetailViewHolder(mLayoutInflater.inflate(R.layout.item_comment,null));
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(position > 0){

            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(position, holder);
                    }
                });
            }
        }
        if(position == 0 ){

            TopViewHolder topViewHolder = (TopViewHolder) holder;
            topViewHolder.setData(commentBeans.get(position));

        }else{
            DetailViewHolder detailViewHolder = (DetailViewHolder)holder;
            detailViewHolder.setData(commentBeans.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return commentBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(position == 0){
           //位置第一个就是头部的内容
           TYPE_CURRENT = TYPE_TOP;
       }else{
           //剩下的位置就是评论
           TYPE_CURRENT = TYPE_COMMENT;
       }
       return TYPE_CURRENT;
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

        public void setData(CommentBean commentBean){
            url = commentBean.getIcon_url();
            tv_comment_time.setText(commentBean.getTime());
            tv_comment_name.setText(commentBean.getName());
            tv_like.setText(commentBean.getLike_time());
            String content = commentBean.getContent();
            String replyName = commentBean.getReplyName();
            if(!TextUtils.isEmpty(replyName)){
                LogUtils.e("获取到replyName-->"+replyName);
                tv_comment_content.setText(HighLightKeyWordUtil.getHighLightKeyWord(Color.BLUE,content,replyName));
            }else{
                LogUtils.e("没有获取replyName");
                tv_comment_content.setText(content);
            }
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(cv_comment_icon);


        }

    }

    /**
     * 头部的viewholder
     */
    class TopViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_origin_time;

        View include_gif;
        View include_image;
        View include_video;
        View incule_text;

        public TopViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_origin_time = (TextView) itemView.findViewById(R.id.tv_origin_time);

            include_gif =  itemView.findViewById(R.id.include_gif);

            include_image =  itemView.findViewById(R.id.include_image);

            include_video =  itemView.findViewById(R.id.include_video);

            incule_text =  itemView.findViewById(R.id.include_text);

        }

        public void setData(final CommentBean commentBean){

            currentType = commentBean.getCurrentType();

            tv_title.setText(commentBean.getTitle());
            tv_origin_time.setText(commentBean.getOrigin());

            if(currentType == TYPE_TEXT){
                //显示文本格式
                incule_text.setVisibility(View.VISIBLE);
                TextView tv_video_kind_text = (TextView) itemView.findViewById(R.id.tv_content);
                String content = commentBean.getTextContent();
                tv_video_kind_text.setText(content);

            }else if(currentType == TYPE_IMAGE){
                //显示图片格式
                TextView tv_context_image = (TextView)include_image.findViewById(R.id.tv_context);
                tv_context_image.setVisibility(View.GONE);
                include_image.setVisibility(View.VISIBLE);
                SubsamplingScaleImageView iv_image_icon = (SubsamplingScaleImageView) itemView.findViewById(R.id.iv_image_icon);
                url = commentBean.getUrl();
                FrescoHelper.loadBigImage(context, iv_image_icon,  url, R.drawable.bg_item);

            }else if(currentType == TYPE_GIF){

                //如果是gif,就是显示gif
                TextView tv_context_gif = (TextView)include_gif.findViewById(R.id.tv_context);
                tv_context_gif.setVisibility(View.GONE);
                include_gif.setVisibility(View.VISIBLE);
                GifImageView iv_image_gif = (GifImageView)itemView.findViewById(R.id.iv_image_gif);

                url = commentBean.getUrl();
                LogUtils.e("评论界面gigUrl--->"+url);
                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.ic_default_header)
                        .error(R.drawable.ic_default_header).into(iv_image_gif);

            }else if(currentType == TYPE_VIDEO){

                //显示视频
                TextView tv_context_video = (TextView)include_video.findViewById(R.id.tv_context);//为什么要这样拿呢，因为你如果只写一个的话，你是拿不到真实对应的，如果你操作显示隐藏不行
                tv_context_video.setVisibility(View.GONE);
                include_video.setVisibility(View.VISIBLE);
                MyJCVideoPlayerStandard jcv_videoplayer = (MyJCVideoPlayerStandard)itemView.findViewById(R.id.jcv_videoplayer);
                TextView tv_video_duration;
                TextView tv_play_nums;
                tv_play_nums = (TextView)itemView.findViewById(R.id.tv_play_nums);
                tv_video_duration = (TextView)itemView.findViewById(R.id.tv_video_duration);
                String times = commentBean.getVideoPlayNums();
                tv_play_nums.setText(times);
                String duration = commentBean.getVideoDuration();
                tv_video_duration.setText(duration);
                String thumbnail = commentBean.getVideoThumbnail();
                url = commentBean.getUrl();
                jcv_videoplayer.setUp(url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "小鸡鸡");
                Glide.with(context).load(thumbnail).into(jcv_videoplayer.thumbImageView);
            }


        }
    }

    //设置点击事件
    public void setOnItemClickLitener(OnItemClickListener mLitener) {
        mOnItemClickListener = mLitener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, RecyclerView.ViewHolder viewHolder);
    }
}
