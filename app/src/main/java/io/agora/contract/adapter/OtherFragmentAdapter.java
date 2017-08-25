package io.agora.contract.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.shuyu.frescoutil.FrescoHelper;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import io.agora.common.Constant;
import io.agora.contract.activity.CommentActivity;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.utils.Utils;
import io.agora.contract.view.CircleImageView;
import io.agora.contract.view.MyJCVideoPlayerStandard;
import io.agora.model.NetAudioPagerData;
import io.agora.onekeyshare.OnekeyShare;
import io.agora.openlive.R;
import pl.droidsonroids.gif.GifImageView;

/**
 * File Name: 看看页面的适配器
 * Author:      ruan
 * Write Dates: 2017/7/24
 * Description:
 */

public class OtherFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 提前定义好各种类型
     *
     */
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

    private final List<NetAudioPagerData.ListEntity> mDatas;
    private Context mContext;
    private Utils utils;
    private LayoutInflater mLayoutInflater;

    public OtherFragmentAdapter(Context context,List<NetAudioPagerData.ListEntity> datas){

        mDatas = datas;
        this.mContext = context;
        utils = new Utils();
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){

            case TYPE_AD:
                return new ADViewHolder(mLayoutInflater.inflate(R.layout.item_ad_all,null));
            case TYPE_GIF:
                return new GIFViewHolder(mLayoutInflater.inflate(R.layout.item_gif_all, null));
            case TYPE_IMAGE:
                return new ImageViewHolder(mLayoutInflater.inflate(R.layout.item_image_all,null));
            case TYPE_VIDEO:
                return new VideoViewHolder(mLayoutInflater.inflate(R.layout.item_video_all,null));
            case TYPE_TEXT:
                return new TextViewHolder(mLayoutInflater.inflate(R.layout.item_text_all,null));

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)){
            case TYPE_IMAGE:
                ImageViewHolder imageViewHolder = (ImageViewHolder)holder;
                imageViewHolder.setData(mDatas.get(position));
                break;
            case TYPE_TEXT:
                TextViewHolder textViewHolder = (TextViewHolder)holder;
                textViewHolder.setData(mDatas.get(position));
                break;
            case TYPE_VIDEO:
                VideoViewHolder videoViewHolder = (VideoViewHolder)holder;
                videoViewHolder.setData(mDatas.get(position));
                break;
            case TYPE_GIF:
                GIFViewHolder gifViewHolder = (GIFViewHolder)holder;
                gifViewHolder.setData(mDatas.get(position));
        }

//        CommonViewHolder commonViewHolder = (CommonViewHolder)holder;
//        commonViewHolder.setData(mDatas.get(position));


    }

    @Override
    public int getItemViewType(int position) {

        NetAudioPagerData.ListEntity listEntity = mDatas.get(position);
        String type = listEntity.getType();//video,text,image,gif,ad
        if ("video".equals(type)) {
            currentType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            currentType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            currentType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            currentType = TYPE_GIF;
        } else {
            currentType = TYPE_AD;//广播
        }
        return  currentType;

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ADViewHolder extends RecyclerView.ViewHolder{

        public ADViewHolder(View itemView) {
            super(itemView);
        }
    }

    class GIFViewHolder extends RecyclerView.ViewHolder{

        GifImageView iv_image_gif;
        TextView tv_context;

        CircleImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        TextView tv_video_kind_text;
        TextView tv_transmit;
        TextView tv_comment;
        TextView tv_like;
        LinearLayout ll_ding;
        LinearLayout ll_comment;

        public GIFViewHolder(View itemView) {
            super(itemView);
            iv_image_gif = (GifImageView) itemView.findViewById(R.id.iv_image_gif);
            tv_context = (TextView) itemView.findViewById(R.id.tv_context);

            iv_headpic = (CircleImageView) itemView.findViewById(R.id.iv_headpic);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time_refresh = (TextView) itemView.findViewById(R.id.tv_time_refresh);
            tv_video_kind_text = (TextView) itemView.findViewById(R.id.tv_video_kind_text);
            tv_transmit = (TextView) itemView.findViewById(R.id.tv_transmit);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);

            ll_ding = (LinearLayout) itemView.findViewById(R.id.ll_ding);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);

        }

        public void setData(final NetAudioPagerData.ListEntity mediaItem){


            Glide.with(mContext).load(mediaItem.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_image_gif);
            tv_context.setText(mediaItem.getText());


            if(mediaItem.getU()!=null&& mediaItem.getU().getHeader()!=null&&mediaItem.getU().getHeader().get(0)!=null){
                Glide.with(mContext).load(mediaItem.getU().getHeader().get(0)).into(iv_headpic);
            }
            if(mediaItem.getU() != null&&mediaItem.getU().getName()!= null){
                tv_name.setText(mediaItem.getU().getName()+"");
            }

            tv_time_refresh.setText(mediaItem.getPasstime());

            //设置标签
            List<NetAudioPagerData.ListEntity.TagsEntity> tagsEntities = mediaItem.getTags();
            if (tagsEntities != null && tagsEntities.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsEntities.size(); i++) {
                    buffer.append(tagsEntities.get(i).getName() + " ");
                }
                tv_video_kind_text.setText(buffer.toString());
            }

            //设置点赞，踩,转发
            tv_like.setText(mediaItem.getUp());
            tv_comment.setText(mediaItem.getDown() + "");
            tv_transmit.setText(mediaItem.getForward()+"");

            ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra(Constants.TYPE_COMMENT,TYPE_GIF);
                    intent.putExtra(Constants.TYPE_URL,mediaItem.getGif().getImages().get(0));
                    intent.putExtra(Constants.COMMENT_TITLE,mediaItem.getText());
                    intent.putExtra(Constants.COMMENT_ORGIN,mediaItem.getU().getName()+"  "+mediaItem.getPasstime());
                    intent.putExtra(Constants.CONTENT_ID,mediaItem.getId());
                    mContext.startActivity(intent);
                }
            });


            ll_ding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        showShare(mediaItem);
                }
            });
        }
    }

    /**
     * 视频的holder
     */
    class VideoViewHolder extends RecyclerView.ViewHolder{

        TextView tv_video_duration;
        TextView tv_play_nums;
        MyJCVideoPlayerStandard jcv_videoplayer;
        TextView tv_context;


        CircleImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        TextView tv_video_kind_text;
        TextView tv_transmit;
        TextView tv_comment;
        TextView tv_like;
        LinearLayout ll_ding;
        LinearLayout ll_comment;

        public VideoViewHolder(View itemView) {
            super(itemView);
            tv_play_nums = (TextView) itemView.findViewById(R.id.tv_play_nums);
            tv_video_duration = (TextView) itemView.findViewById(R.id.tv_video_duration);
            jcv_videoplayer = (MyJCVideoPlayerStandard) itemView.findViewById(R.id.jcv_videoplayer);
            tv_context = (TextView) itemView.findViewById(R.id.tv_context);

            iv_headpic = (CircleImageView) itemView.findViewById(R.id.iv_headpic);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time_refresh = (TextView) itemView.findViewById(R.id.tv_time_refresh);
            tv_video_kind_text = (TextView) itemView.findViewById(R.id.tv_video_kind_text);
            tv_transmit = (TextView) itemView.findViewById(R.id.tv_transmit);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
            ll_ding = (LinearLayout) itemView.findViewById(R.id.ll_ding);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);

        }

        public void setData(final NetAudioPagerData.ListEntity mediaItem){

            //第一个参数是视频播放地址，第二个参数是显示封面的地址，第三参数是标题
            jcv_videoplayer.setUp(mediaItem.getVideo().getVideo().get(0), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "小鸡鸡");
            Glide.with(mContext).load(mediaItem.getVideo().getThumbnail().get(0)).into(jcv_videoplayer.thumbImageView);
            tv_play_nums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
            tv_video_duration.setText(utils.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");
            tv_context.setText(mediaItem.getText());


            if(mediaItem.getU()!=null&& mediaItem.getU().getHeader()!=null&&mediaItem.getU().getHeader().get(0)!=null){
                Glide.with(mContext).load(mediaItem.getU().getHeader().get(0)).into(iv_headpic);
            }
            if(mediaItem.getU() != null&&mediaItem.getU().getName()!= null){
                tv_name.setText(mediaItem.getU().getName()+"");
            }

            tv_time_refresh.setText(mediaItem.getPasstime());

            //设置标签
            List<NetAudioPagerData.ListEntity.TagsEntity> tagsEntities = mediaItem.getTags();
            if (tagsEntities != null && tagsEntities.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsEntities.size(); i++) {
                    buffer.append(tagsEntities.get(i).getName() + " ");
                }
                tv_video_kind_text.setText(buffer.toString());
            }

            //设置点赞，踩,转发
            tv_like.setText(mediaItem.getUp());
            tv_comment.setText(mediaItem.getDown() + "");
            tv_transmit.setText(mediaItem.getForward()+"");

            ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra(Constants.TYPE_COMMENT,TYPE_VIDEO);
                    intent.putExtra(Constants.TYPE_URL,mediaItem.getVideo().getVideo().get(0));
                    intent.putExtra(Constants.COMMENT_TITLE,mediaItem.getText());
                    intent.putExtra(Constants.DURATION_VIDEO,utils.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");
                    intent.putExtra(Constants.PLAY_TIMES,mediaItem.getVideo().getPlaycount() + "次播放");
                    intent.putExtra(Constants.VIDEO_THUMBS,mediaItem.getVideo().getThumbnail().get(0));
                    intent.putExtra(Constants.COMMENT_ORGIN,mediaItem.getU().getName()+"  "+mediaItem.getPasstime());
                    intent.putExtra(Constants.CONTENT_ID,mediaItem.getId());
                    mContext.startActivity(intent);
                }
            });

            ll_ding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShare(mediaItem);
                }
            });


        }
    }

    /**
     * 文字的holder
     */
    class TextViewHolder extends  RecyclerView.ViewHolder{

        TextView tv_content;

        CircleImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        TextView tv_video_kind_text;
        TextView tv_transmit;
        TextView tv_comment;
        TextView tv_like;
        LinearLayout ll_ding;
        LinearLayout ll_comment;
        String content;

        public TextViewHolder(View itemView) {
            super(itemView);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);

            iv_headpic = (CircleImageView) itemView.findViewById(R.id.iv_headpic);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time_refresh = (TextView) itemView.findViewById(R.id.tv_time_refresh);
            tv_video_kind_text = (TextView) itemView.findViewById(R.id.tv_video_kind_text);
            tv_transmit = (TextView) itemView.findViewById(R.id.tv_transmit);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
            ll_ding = (LinearLayout) itemView.findViewById(R.id.ll_ding);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);

        }

        public void setData(final NetAudioPagerData.ListEntity mediaItem){

            //设置文本
            tv_content.setText(mediaItem.getText());


            if(mediaItem.getU()!=null&& mediaItem.getU().getHeader()!=null&&mediaItem.getU().getHeader().get(0)!=null){
                Glide.with(mContext).load(mediaItem.getU().getHeader().get(0)).into(iv_headpic);
            }
            if(mediaItem.getU() != null&&mediaItem.getU().getName()!= null){
                tv_name.setText(mediaItem.getU().getName()+"");
            }

            tv_time_refresh.setText(mediaItem.getPasstime());

            //设置标签
            List<NetAudioPagerData.ListEntity.TagsEntity> tagsEntities = mediaItem.getTags();
            if (tagsEntities != null && tagsEntities.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsEntities.size(); i++) {
                    buffer.append(tagsEntities.get(i).getName() + " ");
                }
                tv_video_kind_text.setText(buffer.toString());


            }

            //设置点赞，踩,转发
            tv_like.setText(mediaItem.getUp());
            tv_comment.setText(mediaItem.getDown() + "");
            tv_transmit.setText(mediaItem.getForward()+"");
            content = mediaItem.getText();
            LogUtils.e("文字传输---->"+content);
            ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra(Constants.TYPE_COMMENT,TYPE_TEXT);
                    intent.putExtra(Constants.TEXT_CONTENT,content);
                    intent.putExtra(Constants.COMMENT_TITLE,mediaItem.getText());
                    intent.putExtra(Constants.COMMENT_ORGIN,mediaItem.getU().getName()+"  "+mediaItem.getPasstime());
                    intent.putExtra(Constants.CONTENT_ID,mediaItem.getId());
                    mContext.startActivity(intent);
                }
            });

            ll_ding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShare(mediaItem);
                }
            });


        }
    }

    /**
     * 图片的holder
     */
    class ImageViewHolder extends RecyclerView.ViewHolder{

        SubsamplingScaleImageView iv_image_icon;
        TextView tv_context;

        CircleImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        TextView tv_video_kind_text;
        TextView tv_transmit;
        TextView tv_comment;
        TextView tv_like;
        LinearLayout ll_ding;
        LinearLayout ll_comment;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_image_icon = (SubsamplingScaleImageView) itemView.findViewById(R.id.iv_image_icon);
            tv_context = (TextView) itemView.findViewById(R.id.tv_context);

            iv_headpic = (CircleImageView) itemView.findViewById(R.id.iv_headpic);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time_refresh = (TextView) itemView.findViewById(R.id.tv_time_refresh);
            tv_video_kind_text = (TextView) itemView.findViewById(R.id.tv_video_kind_text);
            tv_transmit = (TextView) itemView.findViewById(R.id.tv_transmit);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
            ll_ding = (LinearLayout) itemView.findViewById(R.id.ll_ding);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
        }

        public void setData(final NetAudioPagerData.ListEntity mediaItem){


//            iv_image_icon.setImageResource(R.drawable.bg_item);
            int  height = mediaItem.getImage().getHeight()<= getScreenHeight()*0.75?mediaItem.getImage().getHeight(): (int) (getScreenHeight() * 0.75);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getScreenWidth(),height);
            iv_image_icon.setLayoutParams(params);
            if(mediaItem.getImage() != null &&  mediaItem.getImage().getBig()!= null&&mediaItem.getImage().getBig().size() >0){
//                    x.image().bind(viewHolder.iv_image_icon, mediaItem.getImage().getBig().get(0));
                tv_context.setText(mediaItem.getText());
//                Glide.with(mContext).load(mediaItem.getImage().getBig().get(0)).placeholder(R.drawable.bg_item).error(R.drawable.bg_item).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_image_icon);
                FrescoHelper.loadBigImage(mContext, iv_image_icon,  mediaItem.getImage().getBig().get(0), R.drawable.bg_item);
            }

            LogUtils.e("标题文字---->"+mediaItem.getText()+"图片路径---->"+mediaItem.getImage().getBig().get(0)+"图片分享路径---->"+mediaItem.getShare_url());

            if(mediaItem.getU()!=null&& mediaItem.getU().getHeader()!=null&&mediaItem.getU().getHeader().get(0)!=null){
                Glide.with(mContext).load(mediaItem.getU().getHeader().get(0)).into(iv_headpic);
            }
            if(mediaItem.getU() != null&&mediaItem.getU().getName()!= null){
                tv_name.setText(mediaItem.getU().getName()+"");
            }

            tv_time_refresh.setText(mediaItem.getPasstime());

            //设置标签
            List<NetAudioPagerData.ListEntity.TagsEntity> tagsEntities = mediaItem.getTags();
            if (tagsEntities != null && tagsEntities.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsEntities.size(); i++) {
                    buffer.append(tagsEntities.get(i).getName() + " ");
                }
                tv_video_kind_text.setText(buffer.toString());
            }

            //设置点赞，踩,转发
            tv_like.setText(mediaItem.getUp());
            tv_comment.setText(mediaItem.getDown() + "");
            tv_transmit.setText(mediaItem.getForward()+"");


            ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra(Constants.TYPE_COMMENT,TYPE_IMAGE);
                    intent.putExtra(Constants.TYPE_URL,mediaItem.getImage().getBig().get(0));
                    intent.putExtra(Constants.COMMENT_TITLE,mediaItem.getText());
                    intent.putExtra(Constants.COMMENT_ORGIN,mediaItem.getU().getName()+"  "+mediaItem.getPasstime());
                    intent.putExtra(Constants.CONTENT_ID,mediaItem.getId());
                    mContext.startActivity(intent);
                }
            });

            ll_ding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShare(mediaItem);
                }
            });
        }
    }

    class CommonViewHolder extends  RecyclerView.ViewHolder{

        CircleImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        TextView tv_video_kind_text;
        TextView tv_transmit;
        TextView tv_comment;
        TextView tv_like;

        public CommonViewHolder(View itemView) {
            super(itemView);
            iv_headpic = (CircleImageView) itemView.findViewById(R.id.iv_headpic);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time_refresh = (TextView) itemView.findViewById(R.id.tv_time_refresh);
            tv_video_kind_text = (TextView) itemView.findViewById(R.id.tv_video_kind_text);
            tv_transmit = (TextView) itemView.findViewById(R.id.tv_transmit);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
        }

        public void setData(NetAudioPagerData.ListEntity mediaItem){

            if(mediaItem.getU()!=null&& mediaItem.getU().getHeader()!=null&&mediaItem.getU().getHeader().get(0)!=null){
                Glide.with(mContext).load(mediaItem.getU().getHeader().get(0)).into(iv_headpic);
            }
            if(mediaItem.getU() != null&&mediaItem.getU().getName()!= null){
                tv_name.setText(mediaItem.getU().getName()+"");
            }

                tv_time_refresh.setText(mediaItem.getPasstime());

            //设置标签
            List<NetAudioPagerData.ListEntity.TagsEntity> tagsEntities = mediaItem.getTags();
            if (tagsEntities != null && tagsEntities.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsEntities.size(); i++) {
                    buffer.append(tagsEntities.get(i).getName() + " ");
                }
                tv_video_kind_text.setText(buffer.toString());
            }

            //设置点赞，踩,转发
            tv_like.setText(mediaItem.getUp());
            tv_comment.setText(mediaItem.getDown() + "");
            tv_transmit.setText(mediaItem.getForward()+"");

        }
    }


    public int getScreenHeight(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int height2 = outMetrics.heightPixels;
        return height2;
    }

    public int getScreenWidth(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int Srceenwidth = outMetrics.widthPixels;
        return Srceenwidth;
    }


    private void showShare(NetAudioPagerData.ListEntity mediaItem) {
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(mediaItem.getText());
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(mediaItem.getShare_url());
// text是分享文本，所有平台都需要这个字段
        oks.setText(mediaItem.getText()+mediaItem.getShare_url());
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mediaItem.getShare_url());
        LogUtils.e("获取到分享的地址----->"+mediaItem.getShare_url());
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mediaItem.getComment());
        LogUtils.e("获取到分享的地址----->"+mediaItem.getComment());
// site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mediaItem.getShare_url());
        if(mediaItem.getImage()!=null){
            LogUtils.e("获取到图片分享的地址----->"+mediaItem.getImage().getBig().get(0));
            oks.setImageUrl(mediaItem.getImage().getBig().get(0));
        }

        if(mediaItem.getVideo()!=null){
            LogUtils.e("获取到视频分享的地址----->"+mediaItem.getVideo().getVideo().get(0));
            oks.setVideoUrl(mediaItem.getVideo().getVideo().get(0));
        }

        oks.setAddress(mediaItem.getShare_url());


// 启动分享GUI
        oks.show(mContext);
    }

}
