package io.agora.contract.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.agora.contract.LoginActivity;
import io.agora.openlive.R;
import io.agora.openlive.ui.HomeActivity;
import io.agora.openlive.ui.MainActivity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description: 所有fragment的基类
 */

public abstract  class BaseFragment extends Fragment {

    public Context context;
    FrameLayout fl_content;
    TextView tv_title;
    boolean isInitComplete = false;
    Toolbar toolbar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(context, R.layout.fragment_base,null);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        fl_content.addView(initView());
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if(setTitile().equals("我的")){
            toolbar.setVisibility(View.GONE);
        }else{
            toolbar.setVisibility(View.VISIBLE);
            tv_title.setText(setTitile());
        }
        isInitComplete = true;
        return view;

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("Com","BaseFragment------>onActivityCreated()");
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("Com","BaseFragment------>isVisibleToUser()"+isVisibleToUser);
        if(isInitComplete){
            Log.e("Com","BaseFragment------>setUserVisibleHint()");
            initData();
        }
    }

    /**
     * 初始化所有视图
     * @return
     */
    public abstract View initView();

    /**
     * 设置头部标题
     * @return
     */
    public abstract  String setTitile();

    /**
     * 初始化数据
     */
    public void initData(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("Com","BaseFragment------>onDestroyView()");
        isInitComplete = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Com","BaseFragment------>Ondestory()");
        isInitComplete = false;
    }

}
