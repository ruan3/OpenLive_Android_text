package io.agora.contract.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.contract.view.CircleImageView;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.LiveRoomActivity;
import io.agora.openlive.ui.MainActivity;
import io.agora.rtc.Constants;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class ContentAdapter extends RecyclerView.Adapter {


    private final LayoutInflater mLayoutInflater;
    Context context;
    List<LiveVideos> videos;

    public ContentAdapter(Context context, List<LiveVideos> videos){

        this.context = context;
        this.videos = videos;
        mLayoutInflater = LayoutInflater.from(context);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video,parent,false);
        return new contentHodler(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((contentHodler) holder).tv_title.setText(videos.get(position).getLiveTitle());
        if(videos.get(position).getAnchorName()!=null){
            Log.e("Com","获取到的用户名----->"+videos.get(position).getAnchorName().getObjectId());
            //注意：在这里获取到的只有objectid,只能再通过objectid去查询具体信息
            BmobQuery<MyUser> query = new BmobQuery<MyUser>();
            query.addWhereEqualTo("objectId",videos.get(position).getAnchorName().getObjectId());
            query.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if(list != null){
                        String userName = list.get(0).getUser_id_name();
                        Log.e("Com","获取到的用户名通过----->"+userName);
                        ((contentHodler) holder).source.setText(userName);
                        BmobFile user_icon = list.get(0).getHead_icon();
                        Glide.with(context).load(user_icon.getUrl()).into( ((contentHodler)holder).cv_user_icon);

                    }else{
                        Log.e("Com","获取到的用户名报错----->"+e.toString());
                    }
                }
            });
            ((contentHodler) holder).source.setText(videos.get(position).getAnchorName().getUsername());
        }
        //先要获取到文件，然后根据文件的具体信息再去下载图片，这里最好用一下框架
        BmobFile file = videos.get(position).getImageUrl();

        if(file != null){
            //判断不为空就加载图片下来
            Glide.with(context).load(file.getFileUrl()).into( ((contentHodler) holder).cover);
            ((contentHodler) holder).rl_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = videos.get(position).getObjectId();
                    LiveVideos liveVideos = videos.get(position);
//                    liveVideos.setLiving(true);
                    liveVideos.increment("audience",1);
                    liveVideos.update(id,new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                    Intent i = new Intent(context, LiveRoomActivity.class);
                    i.putExtra(ConstantApp.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                    i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, videos.get(position).getAnchorName().getObjectId());
                    context.startActivity(i);
                }
            });

            ((contentHodler)holder).online_audience.setText("在线人数："+videos.get(position).getAudience());
//            ((contentHodler) holder).cover.setImageURI();
        }


    }



    @Override
    public int getItemCount() {
        return videos.size();
    }

    class contentHodler extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView source;
        ImageView cover;
        RelativeLayout rl_root;
        TextView online_audience;
        CircleImageView cv_user_icon;


        public contentHodler(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.title);
            source = (TextView) itemView.findViewById(R.id.source);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            rl_root = (RelativeLayout) itemView.findViewById(R.id.rl_root);
            online_audience = (TextView) itemView.findViewById(R.id.online_audience);
            cv_user_icon = (CircleImageView) itemView.findViewById(R.id.cv_user_icon);
        }


    }
}
