package io.agora.presenter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.agora.contract.fragment.IMineFragment;
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

    public IMineFragmentPresentImpl(IMineFragment iMineFragment){

        this.iMineFragment = iMineFragment;

    }

    /**
     * 查询当前用户对象的被点赞数，直播数
     */
    @Override
    public void getData() {

        BmobQuery<LiveVideos> query = new BmobQuery<LiveVideos>();
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
