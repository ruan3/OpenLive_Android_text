package io.agora.contract.fragment;

import android.view.View;

import io.agora.openlive.R;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class OtherFragment extends BaseFragment {
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_other,null);
        return view;
    }

    @Override
    public String setTitile() {
        return "其他";
    }
}
