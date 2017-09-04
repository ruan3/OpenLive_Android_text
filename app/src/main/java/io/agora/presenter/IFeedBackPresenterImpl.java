package io.agora.presenter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.agora.contract.activity.IFeedBack;
import io.agora.contract.utils.LogUtils;
import io.agora.model.Comment;
import io.agora.model.FeedBackBean;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/30
 * Description:
 */

public class IFeedBackPresenterImpl implements ICommentPresenter {

    IFeedBack iFeedBack;

    public IFeedBackPresenterImpl(IFeedBack iFeedBack){
            this.iFeedBack = iFeedBack;
    }

    @Override
    public void getData(String commentId) {


        BmobQuery<FeedBackBean> query = new BmobQuery<FeedBackBean>();
//查询playerName叫“比目”的数据
//        query.setLimit(50);
//执行查询方法
        query.findObjects(new FindListener<FeedBackBean>() {
            @Override
            public void done(List<FeedBackBean> feedBackBeen, BmobException e) {
                if(e==null){

                    if(feedBackBeen != null&&feedBackBeen.size()>0){
                        LogUtils.e("查询意见反馈成功！");
                        iFeedBack.getDataResult(0,feedBackBeen);
                    }

                }else{
                    LogUtils.e("查询意见反馈失败："+e.getMessage()+","+e.getErrorCode());
                    iFeedBack.getDataResult(-1,null);
                }
            }
        });

    }

    @Override
    public void updataData(Comment comment) {

    }

    @Override
    public void updataFeedbck(FeedBackBean feedBackBean) {

        feedBackBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    LogUtils.e("上传意见反馈成功！");
                    iFeedBack.updataResult(0,null);
                }else{
                    LogUtils.e("上传意见反馈失败--->"+e.toString());
                    iFeedBack.updataResult(-1,e.toString());
                }
            }
        });

    }
}
