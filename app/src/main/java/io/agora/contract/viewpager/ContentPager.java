package io.agora.contract.viewpager;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/27
 * Description:
 */

public class ContentPager extends BasePager{

    Context context;
    String title;

    public ContentPager(Context context,String title){

        this.context = context;
        this.title = title;

    }

    public TextView initView(){

        TextView tv_title = new TextView(context);
        tv_title.setText(title);
        tv_title.setGravity(Gravity.CENTER);
        return tv_title;

    }

}
