package io.agora.presenter;

import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.agora.contract.fragment.IContentFragment;
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

        final BmobQuery<LiveVideos> liveVideos = new BmobQuery<>();
        liveVideos.findObjects(new FindListener<LiveVideos>() {
            @Override
            public void done(List<LiveVideos> list, BmobException e) {

                if(list != null){
                    //查询数据成功
                    IContentFragment.getDatas(0,list);
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
}
