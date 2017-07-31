package io.agora.contract.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * File Name:    自定义系统VideoView
 * Author:      ruan
 * Write Dates: 2017/7/27
 * Description:
 */

public class SystemVideoView extends VideoView {


    /**
     * 在代码创建时，一般用到这个方法
     * @param context
     */
    public SystemVideoView(Context context) {
        this(context,null);
    }

    /**
     * 但这个类当布局文件的时候，系统会通过这个方法实例化这个类
     * @param context
     * @param attrs
     */
    public SystemVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设置样式的时候，会调用这个方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SystemVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 设置视频宽高
     * @param videoWith 指定视频的宽
     * @param videoHeight 指定视频的高
     */
    public void setVideoSize(int videoWith,int videoHeight){

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = videoWith;
        layoutParams.height = videoHeight;
        setLayoutParams(layoutParams);
    }
}
