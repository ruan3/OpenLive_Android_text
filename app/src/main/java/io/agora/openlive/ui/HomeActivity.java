package io.agora.openlive.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;
import io.agora.contract.fragment.ContentFragment;
import io.agora.contract.fragment.MineFragment;
import io.agora.contract.fragment.OtherFragment;
import io.agora.openlive.R;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description: 登录完成后进入的主界面
 */

public class HomeActivity extends BaseActivity {

    List<Fragment> fragments;
    SpaceTabLayout tabLayout;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    @Override
    protected void initUIandEvent() {

        fragments = new ArrayList<>();
        fragments.add(new ContentFragment());
        fragments.add(new OtherFragment());
        fragments.add(new MineFragment());

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);
        tabLayout.initialize(viewPager,getSupportFragmentManager(),fragments);
        tabLayout.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });
        tabLayout.setTabOneText("直播");
        tabLayout.setTabOneIcon(R.drawable.icon_video);
        tabLayout.setTabTwoText("其他");
        tabLayout.setTabTwoIcon(R.drawable.other);
        tabLayout.setTabThreeText("我");
        tabLayout.setTabThreeIcon(R.drawable.mine);
        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "" + tabLayout.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void deInitUIandEvent() {

    }

}
