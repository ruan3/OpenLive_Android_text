package io.agora.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.ValueEventListener;
import io.agora.contract.fragment.IMineFragment;
import io.agora.contract.utils.CacheUtils;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.LogUtils;
import io.agora.model.LiveVideos;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/20
 * Description:
 */

public class IMineFragmentPresentImpl implements IMineFragmentPresenter {

    IMineFragment iMineFragment;
    Context context;

    public IMineFragmentPresentImpl(IMineFragment iMineFragment,Context context){

        this.iMineFragment = iMineFragment;
        this.context = context;

    }

    /**
     * 查询当前用户对象的被点赞数，直播数
     */
    @Override
    public void getData() {

        String liveid = CacheUtils.getString(context,Constants.liveRoomID);
        BmobQuery<LiveVideos> query = new BmobQuery<LiveVideos>();
        if(!TextUtils.isEmpty(liveid)){
            query.getObject(liveid, new QueryListener<LiveVideos>() {
                @Override
                public void done(LiveVideos videos, BmobException e) {
                    if(e==null){
                        LogUtils.e("使用房间ID查询成功");
                        iMineFragment.getData(0,"",videos);
                    }else{
                        LogUtils.e("查询失败----->"+e.toString());
                    }
                }
            });
        }else{

            query.addWhereEqualTo("anchorName",BmobUser.getCurrentUser().getObjectId());
            query.findObjects(new FindListener<LiveVideos>() {
                @Override
                public void done(List<LiveVideos> list, BmobException e) {

                    if(list!=null&&list.size()>0){

                        LogUtils.e("查询成功---->"+list.get(0).getLikes());
                        iMineFragment.getData(0,"",list.get(0));

                    }else{
                        LogUtils.e("查询失败----->"+e.toString());
                    }

                }
            });
        }

    }

    @Override
    public void RealTimeCallBack() {

        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {

            @Override
            public void onDataChange(JSONObject data) {
                Log.e("Com","实时数据onDataChange：data = "+data);
                iMineFragment.RealTimeCallBack(1,null);
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.e("Com","实时数据连接成功:"+rtd.isConnected());
                if(rtd.isConnected()){
                    // 监听表更新
                    rtd.subTableUpdate("LiveVideos");
                }
            }
        });


    }
}
