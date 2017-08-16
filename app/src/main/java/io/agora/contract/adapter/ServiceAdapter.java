package io.agora.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import io.agora.contract.view.CircleImageView;
import io.agora.model.ChatBean;
import io.agora.model.MyUser;
import io.agora.openlive.R;

/**
 * File Name:   客服中心adapter
 * Author:      ruan
 * Write Dates: 2017/8/16
 * Description:
 */

public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 提前定义好各种类型
     *
     */
    /**
     * 我的
     */
    private static final int TYPE_MINE = 0;

    /**
     * 客服的
     */
    private static final int TYPE_SERVICE = 1;
    /**
     * 当前类型
     */
    public int currentType = TYPE_MINE;


    Context context;
    ArrayList<ChatBean> chatBeans;
    private LayoutInflater mLayoutInflater;

    public ServiceAdapter(Context context, ArrayList<ChatBean> chatBaens){

        this.context = context;
        this.chatBeans = chatBaens;
        mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_MINE){

            return new ServiceAdapter.MineViewHolder(mLayoutInflater.inflate(R.layout.item_mine_chat,null));

        }else if(viewType == TYPE_SERVICE){

            return new ServiceAdapter.ServiceViewHolder(mLayoutInflater.inflate(R.layout.item_serices_chat,null));

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == 0){
            //给用户填充界面
            MineViewHolder mineViewHolder = (MineViewHolder)holder;
            mineViewHolder.setData(chatBeans.get(position));

        }else if(getItemViewType(position) == 1){
            //给客服填充界面
            ServiceViewHolder serviceViewHolder = (ServiceViewHolder)holder;
            serviceViewHolder.setData(chatBeans.get(position));

        }

    }

    @Override
    public int getItemViewType(int position) {
        //判断是什么类型
        ChatBean chatBean = chatBeans.get(position);
        if(chatBean.getType() == 0){
            currentType = TYPE_MINE;
        }else if(chatBean.getType() == 1){
            currentType = TYPE_SERVICE;
        }
        return currentType;

    }

    @Override
    public int getItemCount() {
        return chatBeans.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder{

       TextView tv_msg;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_setvice_chat);
        }

        public void setData(final ChatBean chatBean){

            tv_msg.setText(chatBean.getMsg());

        }
    }
    class MineViewHolder extends RecyclerView.ViewHolder{

        TextView tv_msg;
        CircleImageView iv_mine_headICon;

        public MineViewHolder(View itemView) {
            super(itemView);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_mine_chat);
            iv_mine_headICon = (CircleImageView) itemView.findViewById(R.id.iv_mine_headICon);
            MyUser user = BmobUser.getCurrentUser(MyUser.class);
            if (user != null) {

                if (user.getHead_icon() != null) {

                    Glide.with(context).load(user.getHead_icon().getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_mine_headICon);
                } else {
                    Glide.with(context).load(user.getAuthIconUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_mine_headICon);
                }
            }
        }

        public void setData(final ChatBean chatBean){

            tv_msg.setText(chatBean.getMsg());

        }
    }

}
