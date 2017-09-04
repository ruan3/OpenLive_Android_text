package io.agora.presenter;

import io.agora.model.Comment;
import io.agora.model.FeedBackBean;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/21
 * Description:
 */

public interface ICommentPresenter {

    public void getData(String commentId);
    public void updataData(Comment comment);
    public void updataFeedbck(FeedBackBean feedBackBean);

}
