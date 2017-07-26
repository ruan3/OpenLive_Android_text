package io.agora.presenter;

import android.util.Log;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;
import io.agora.contract.fragment.IContentFragment;
import io.agora.contract.utils.LogUtils;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/7
 * Description:
 */

public class IContentFragmentPresenterImpl implements IContentFragmentPresenter {

    IContentFragment IContentFragment;

    public IContentFragmentPresenterImpl(IContentFragment IContentFragment){
        this.IContentFragment = IContentFragment;
    }

    @Override
    public void getData() {

        LogUtils.e("查询数据开始");
        final BmobQuery<LiveVideos> liveVideos = new BmobQuery<>();
        liveVideos.addWhereEqualTo("isLiving",true);
        liveVideos.findObjects(new FindListener<LiveVideos>() {
            @Override
            public void done(List<LiveVideos> list, BmobException e) {

                if(list != null){
                    //查询数据成功
                    if(list.size()>0){
                        IContentFragment.getDatas(0,list);
                        LogUtils.e("查询数据成功");
                    }else{
//                        Log.e("Com","查詢列表返回沒有數據");
                        LogUtils.e("查詢列表返回沒有數據");
                        IContentFragment.getDatas(1,list);
                    }
                }else{
                    IContentFragment.getDataFailed(-1,e.toString());
                }

            }
        });

    }

    @Override
    public void setData() {

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        LiveVideos liveVideo = new LiveVideos();
        liveVideo.setRoomID("123456");
        liveVideo.setLiveTitle("阮仔不搞事情");
        liveVideo.setAnchorName(user);
        liveVideo.setAudience(1000);
        liveVideo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(s!=null){
                    Log.e("Com","提交成功-----"+s.toString());
                    IContentFragment.setDataResult(0,s.toString());
                }else{
                    Log.e("Com","提交失败-----"+e.toString());
                    IContentFragment.setDataResult(-1,e.toString());
                }

            }
        });

    }

    @Override
    public void RealTimeCallBack() {

        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {

            @Override
            public void onDataChange(JSONObject data) {
                Log.e("Com","实时数据onDataChange：data = "+data);
                IContentFragment.RealTimeCallBack(1,null);
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
