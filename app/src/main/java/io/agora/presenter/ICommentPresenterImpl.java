package io.agora.presenter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.agora.contract.activity.IComment;
import io.agora.contract.utils.LogUtils;
import io.agora.model.Comment;
import io.agora.model.CommentBean;
import io.agora.model.FeedBackBean;

/**
 * File Name:   评论的presenter
 * Author:      ruan
 * Write Dates: 2017/8/21
 * Description:
 */

public class ICommentPresenterImpl implements ICommentPresenter {

    IComment iComment;
    ArrayList<CommentBean> commentBeans;
    CommentBean commentBean;

    public ICommentPresenterImpl(IComment iComment){

        this.iComment = iComment;

    }
    @Override
    public void getData(String commentId) {

        commentBeans = new ArrayList<CommentBean>();

        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereEqualTo("contentId",commentId);
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> comments, BmobException e) {
                if(e==null){

                    if(comments.size()>0){
                        for(Comment comment : comments){
                            commentBean = new CommentBean();
                            commentBean.setTime(comment.getUpdatedAt());
                            commentBean.setIcon_url(comment.getIcon_url());
                            commentBean.setName(comment.getName());
                            commentBean.setContent(comment.getContent());
                            commentBean.setLike_time(comment.getLike_time());
                            commentBean.setReplyName(comment.getReplyName());
                            commentBeans.add(commentBean);
                        }
                        iComment.getDataResult(0,commentBeans);
                        LogUtils.e("获取到评论数据--->"+comments.size());
                    }else{
                        iComment.getDataResult(1,null);
                        LogUtils.e("没有评论数据");
                    }
                }else{
                        LogUtils.e("获取评论数据出错--->"+e.toString());
                        iComment.getDataResult(-1,null);
                }
            }
        });

    }

    @Override
    public void updataData(Comment comment) {

        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    LogUtils.e("上传评论数据成功");
                    iComment.updateResult(0,s);
                }else{
                    LogUtils.e("上传评论数据失败--->"+e.toString());
                    iComment.updateResult(-1,e.toString());
                }
            }
        });

    }

    @Override
    public void updataFeedbck(FeedBackBean feedBackBean) {

    }


}
