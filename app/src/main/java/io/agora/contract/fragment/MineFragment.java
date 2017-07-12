package io.agora.contract.fragment;

import android.view.View;

import io.agora.openlive.R;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class MineFragment extends BaseFragment {
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_mine,null);
        return view;
    }

    @Override
    public String setTitile() {
        return "我的";
    }
}
