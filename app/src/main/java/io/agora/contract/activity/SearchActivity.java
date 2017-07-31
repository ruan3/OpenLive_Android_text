package io.agora.contract.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.agora.contract.adapter.SearchAdapter;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.model.SearchBean;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/26
 * Description:
 */

public class SearchActivity extends BaseActivity {

    EditText et_input;
    TextView tv_search;
    ListView listview;
    Context context;
    RequestQueue mQueue;
    TextView tvNodata;
    private SearchAdapter adapter;
    private ProgressBar progressBar;

    private ArrayList<SearchBean.ItemData> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
    }

    @Override
    protected void initUIandEvent() {


        et_input = (EditText) findViewById(R.id.et_input);
        tv_search = (TextView) findViewById(R.id.tv_search);
        listview = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
        mQueue = new Volley().newRequestQueue(context);

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchText();

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //3.传递列表数据-对象-序列化
                Intent intent = new Intent(context,SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist",items);
                intent.putExtras(bundle);
                intent.putExtra("position",position-1);
                context.startActivity(intent);
            }
        });

    }

    @Override
    protected void deInitUIandEvent() {


    }

    public void getData(String url){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                LogUtils.e("获取到搜索网络数据---->"+result);
                processData(result);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                LogUtils.e("获取网络数据异常----->"+volleyError.toString());
                progressBar.setVisibility(View.GONE);
            }
        });

        mQueue.add(request);
    }

    /**
     * 搜索
     */
    private void searchText() {
        String text = et_input.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {

            if(items != null && items.size() >0){
                items.clear();
            }

            try {
                text = URLEncoder.encode(text, "UTF-8");

                String url = Constants.SEARCH_URL + text;
                LogUtils.e("搜索新闻的地址---->"+url);
                getData(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context,"请输入搜索内容",Toast.LENGTH_SHORT).show();
        }
    }

    private void processData(String result) {
        SearchBean searchBean = parsedJson(result);
        items =  searchBean.getItems();

        showData();
    }

    private void showData() {
        if(items != null && items.size() >0){
            //设置适配器
            adapter = new SearchAdapter(this,items);
            listview.setAdapter(adapter);
            tvNodata.setVisibility(View.GONE);
        }else{
            tvNodata.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }


        progressBar.setVisibility(View.GONE);
    }

    /**
     * 解析json数据
     * @param result
     * @return
     */
    private SearchBean parsedJson(String result) {
        return new Gson().fromJson(result, SearchBean.class);
    }

}
