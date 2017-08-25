package io.agora.contract.activity;

import java.util.ArrayList;
import java.util.List;

import io.agora.model.Comment;
import io.agora.model.CommentBean;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/21
 * Description:
 */

public interface IComment {

    public void getDataResult(int code, ArrayList<CommentBean> comments);
    public void updateResult(int code,String result);

}
