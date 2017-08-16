package io.agora.contract.fragment;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.SpringBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;

import io.agora.contract.utils.DisplayUtil;
import io.agora.contract.viewpager.BasePager;
import io.agora.contract.viewpager.ContentPager;
import io.agora.contract.viewpager.FunnyPager;
import io.agora.contract.viewpager.MoivePager;
import io.agora.contract.viewpager.NewsPager;
import io.agora.openlive.R;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/27
 * Description:
 */

public class NewsFragment extends BaseFragment {

    ScrollIndicatorView spring_indicator;
    ViewPager spring_viewPager;

    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    private int unSelectColor;
    ArrayList<BasePager> pagers;
    String[] strs = {"热点","体育","娱乐","电影","金融"};
    private boolean isFrist = true;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_news,null);
        spring_indicator = (ScrollIndicatorView) view.findViewById(R.id.spring_indicator);
        spring_viewPager = (ViewPager) view.findViewById(R.id.spring_viewPager);
        return view;
    }

    @Override
    public void initData() {

        if(isFrist){

            int selectColor = Color.WHITE;
            unSelectColor = Color.DKGRAY;
            spring_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor));
            spring_indicator.setScrollBar(new SpringBar(getApplicationContext(), Color.RED));
//        indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.RED, 5));
            spring_viewPager.setOffscreenPageLimit(4);
            indicatorViewPager = new IndicatorViewPager(spring_indicator, spring_viewPager);
            inflate = LayoutInflater.from(getApplicationContext());
            indicatorViewPager.setAdapter(adapter);
            indicatorViewPager.setCurrentItem(0, false);

            pagers = new ArrayList<BasePager>();

            pagers.add(new NewsPager(context,0));
            pagers.add(new NewsPager(context,1));
            pagers.add(new FunnyPager(context));
            pagers.add(new MoivePager(context));
//            pagers.add(new ContentPager(context,"金融"));

            isFrist = false;
        }


    }

    @Override
    public String setTitile() {
        return "看看";
    }

    private IndicatorPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {

            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            int padding = DisplayUtil.dipToPix(getApplicationContext(), 30);
            textView.setPadding(padding, 0, padding, 0);
            textView.setText(strs[position]);
            textView.setTextColor(Color.WHITE);
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {

            convertView = pagers.get(position).initView();

            return convertView;
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_UNCHANGED;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
}
