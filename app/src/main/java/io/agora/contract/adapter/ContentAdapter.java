package io.agora.contract.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import io.agora.model.LiveVideos;
import io.agora.openlive.R;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((contentHodler) holder).tv_title.setText(videos.get(position).getLiveTitle());
        if(videos.get(position).getAnchorName()!=null){
            Log.e("Com","获取到的用户名----->"+videos.get(position).getAnchorName().getObjectId());
            //注意：在这里获取到的只有objectid,只能再通过objectid去查询具体信息
            BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
            query.addWhereEqualTo("objectId",videos.get(position).getAnchorName().getObjectId());
            query.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> list, BmobException e) {
                    if(list != null){
                        String userName = list.get(0).getUsername();
                        Log.e("Com","获取到的用户名通过----->"+userName);
                        ((contentHodler) holder).source.setText(userName);
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

            file.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(s!=null){
                        Log.e("Com","对应图片下载完成------>"+s);
                        ((contentHodler) holder).cover.setImageURI(Uri.parse(s));
                    }else{
                        Log.e("Com","对应图片下载失败------->"+e.toString());
                    }

                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });

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


        public contentHodler(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.title);
            source = (TextView) itemView.findViewById(R.id.source);
            cover = (ImageView) itemView.findViewById(R.id.cover);
        }


    }
}
