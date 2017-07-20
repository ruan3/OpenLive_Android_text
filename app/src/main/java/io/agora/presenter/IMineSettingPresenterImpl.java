package io.agora.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

import java.io.File;
import java.security.DigestOutputStream;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.ProgressCallback;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.contract.LoginActivity;
import io.agora.contract.activity.CreateRoomActivity;
import io.agora.contract.activity.IMineSetting;
import io.agora.contract.activity.MineSettingActivity;
import io.agora.contract.utils.DialogUtils;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.LiveRoomActivity;
import io.agora.rtc.Constants;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/19
 * Description:
 */

public class IMineSettingPresenterImpl implements IMineSettingPresenter {

    FileChooserManager fm;
    ProgressDialog dialog =null;
    private static String url="";
    IMineSetting iMineSetting;

    public IMineSettingPresenterImpl(FileChooserManager fm, IMineSetting iMineSetting){
        this.fm = fm;
        this.iMineSetting = iMineSetting;
    }

    @Override
    public void pickFile(FileChooserListener fileChooserListener) {

        fm.setFileChooserListener(fileChooserListener);

        try {
            fm.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新头像
     * @param file
     * @param context
     */
    @Override
    public void uploadHeadIcon(File file,Context context) {

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("头像上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final BmobFile bmobFile = new BmobFile(file);

        bmobFile.uploadObservable(new ProgressCallback() {
            @Override
            public void onProgress(Integer integer, long l) {
                dialog.setProgress(integer);
            }
        }).doOnNext(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                url = bmobFile.getUrl();

            }
        }).concatMap(new Func1<Void, Observable<Void>>() {//将bmobFile保存到movie表中
            @Override
            public Observable<Void> call(Void aVoid) {
                Log.e("Com", "使用rxjava更新有没有");

                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                user.setHead_icon(bmobFile);

                return upDateObservable(user);
            }
        }).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {


            }

            @Override
            public void onError(Throwable e) {
                Log.e("Com", "屌你上传头像出错了----->"+e.toString());
                dialog.dismiss();
                iMineSetting.updateIconFalid(e.toString());
            }

            @Override
            public void onNext(Void s) {
                dialog.dismiss();
                Log.e("Com","上传头像完成");
                iMineSetting.updateIconSuccess("上传完成");
//
            }
        });

    }

    /**
     * 更新昵称
     * @param name
     */
    @Override
    public void upDateUserName(String name) {

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        user.setUser_id_name(name);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null){
                    Log.e("Com","更换昵称失败----->"+e.toString());
                    iMineSetting.UpdateUserName(-1,e.toString());
                }else{
                    Log.e("Com","更换昵称成功");
                    iMineSetting.UpdateUserName(0,"");
                }
            }
        });

    }

    /**
     * 退出登录对话框
     * @param context
     */
    @Override
    public void loginOut(final Context context) {

        DialogUtils.getInstance().showLogOut(context, new DialogUtils.CallBackListener() {


            @Override
            public void confirm(String result) {

                BmobUser.logOut();
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                if(user==null){
                    //登出成功
                    DialogUtils.getInstance().isShow();
                    iMineSetting.LogOut(0);
                }else{
                    iMineSetting.LogOut(-1);
                }

            }

            @Override
            public void cancle(String str) {

            }
        });

    }

    private Observable<Void> upDateObservable(BmobObject obj){ return obj.updateObservable();}
}
