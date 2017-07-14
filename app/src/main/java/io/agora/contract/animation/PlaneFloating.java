package io.agora.contract.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;

import com.ufreedom.floatingview.spring.SimpleReboundListener;
import com.ufreedom.floatingview.spring.SpringHelper;
import com.ufreedom.floatingview.transition.BaseFloatingPathTransition;
import com.ufreedom.floatingview.transition.FloatingPath;
import com.ufreedom.floatingview.transition.PathPosition;
import com.ufreedom.floatingview.transition.YumFloating;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/14
 * Description:
 */

public class PlaneFloating extends BaseFloatingPathTransition {

    int mScreenHeight;

    public PlaneFloating(int mScreenHeight){
        this.mScreenHeight = mScreenHeight;
    }

    @Override
    public FloatingPath getFloatingPath() {
        Path path = new Path();
        path.moveTo(0, 0);//只是控制画笔 的位移
        path.quadTo(100, -300, 0, -600);//控制画笔画曲线
        path.rLineTo(0, -mScreenHeight - 300);//这个是直线
        return FloatingPath.create(path, false);
    }

    @Override
    public void applyFloating(final YumFloating yumFloating) {

        ValueAnimator translateAnimator = ObjectAnimator.ofFloat(getStartPathPosition(), getEndPathPosition());
        translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                PathPosition floatingPosition = getFloatingPosition(value);
                yumFloating.setTranslationX(floatingPosition.x);
                yumFloating.setTranslationY(floatingPosition.y);

            }
        });
        translateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                yumFloating.setTranslationX(0);
                yumFloating.setTranslationY(0);
                yumFloating.setAlpha(0f);
                yumFloating.clear();
            }
        });


        SpringHelper.createWithBouncinessAndSpeed(0.0f, 1.0f, 14, 15)
                .reboundListener(new SimpleReboundListener() {
                    @Override
                    public void onReboundUpdate(double currentValue) {
                        yumFloating.setScaleX((float) currentValue);
                        yumFloating.setScaleY((float) currentValue);
                    }
                }).start(yumFloating);

        translateAnimator.setDuration(3000);
        translateAnimator.start();
    }


}
