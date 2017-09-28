package io.agora.presenter;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.contract.activity.IComplain;
import io.agora.contract.utils.LogUtils;
import io.agora.model.ComplainBean;
import io.agora.model.LiveVideos;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/9/4
 * Description:
 */

public class IComplainPresenterImpl implements IComplainPresenter {

    IComplain iComplain;

    public IComplainPresenterImpl(IComplain iComplain){
        this.iComplain = iComplain;
    }

    /**
     * 上传举报内容
     * @param complainBean
     */
    @Override
    public void UpdataComplainContent(ComplainBean complainBean) {

        complainBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

                if(e==null){
                    LogUtils.e("上传举报成功！");
                    iComplain.saveResult(0,null);
                }else{
                    LogUtils.e("上传举报失败---->"+e.toString());
                    iComplain.saveResult(-1,e.toString());
                }

            }
        });

    }

    /**
     * 更新直播表
     * @param liveId
     */
    @Override
    public void UpdataLiveRoom(String liveId) {

        //先查询出对应的数据
        BmobQuery<LiveVideos> query = new BmobQuery<LiveVideos>();
        query.getObject(liveId, new QueryListener<LiveVideos>() {
            @Override
            public void done(LiveVideos liveVideos, BmobException e) {

                if(e==null){
                    LogUtils.e("获取直播对象成功！");
                    //成功获取到对象后，更新对象
                    liveVideos.increment("ComplainTimes");
                    liveVideos.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                            if(e==null){
                             LogUtils.e("更新举报数成功");
                            }else{
                                LogUtils.e("更新举报数失败");
                            }
                        }
                    });
                }else{
                    LogUtils.e("获取直播对象失败----->"+e.toString());
                }
            }
        });

    }
}
