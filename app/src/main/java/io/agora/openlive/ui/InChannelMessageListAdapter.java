package io.agora.openlive.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.agora.openlive.R;
import io.agora.openlive.model.Message;

/**
 * File Name:
 * Author:      阮
 * Write Dates: 2017/6/27
 * Description:
 * Change log:
 * 2017/6/27-17:23---[公司]---[姓名]
 * ......Added|Changed|Delete......
 * --------------------------------
 */

public class InChannelMessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Message> list;
    protected final LayoutInflater mInflater;


    public InChannelMessageListAdapter(Context context, ArrayList<Message> list){
        this.context = context;
        this.list = list;
        mInflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =mInflater.inflate(R.layout.in_channel_message,parent,false);
        return new MessageHodler(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message msg = list.get(position);

        MessageHodler messageHodler = (MessageHodler) holder;

        String sender = msg.getSender().name;

        if(TextUtils.isEmpty(sender)){
            messageHodler.tv_content.setBackgroundResource(R.drawable.rounded_bg_blue);
        }else{
            messageHodler.tv_content.setBackgroundResource(R.drawable.rounded_bg);
        }
        messageHodler.tv_content.setText(msg.getContent());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    public class MessageHodler extends RecyclerView.ViewHolder{
        public TextView tv_content;


        public MessageHodler(View itemView) {
            super(itemView);
            tv_content = (TextView) itemView.findViewById(R.id.msg_content);
        }
    }
}
