package io.agora.contract.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.smile.filechoose.api.ChooserType;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ProgressCallback;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.agora.contract.utils.CacheUtils;
import io.agora.contract.utils.LogUtils;
import io.agora.model.Comment;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.BaseActivity;
import io.agora.openlive.ui.LiveRoomActivity;
import io.agora.openlive.ui.MainActivity;
import io.agora.presenter.ICreateRoomPresenter;
import io.agora.presenter.ICreateRoomPresenterImpl;
import io.agora.rtc.Constants;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * File Name:   创建房间界面
 * Author:      ruan
 * Write Dates: 2017/7/7
 * Description:
 */

public class CreateRoomActivity extends BaseActivity implements FileChooserListener{

    ImageView iv_imge_add;
    EditText et_room_title;
    Button btn_confrim;
    ChosenFile choosedFile;
    private FileChooserManager fm;

    ICreateRoomPresenter iCreateRoomPresenter;
    File Image;

    BmobFile bmobFile;

    int ComplainTimes;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        context = this;
        iCreateRoomPresenter = new ICreateRoomPresenterImpl();
    }

    @Override
    protected void initUIandEvent() {

        iv_imge_add = (ImageView) findViewById(R.id.iv_iamge_add);
        et_room_title = (EditText) findViewById(R.id.et_room_title);

        iv_imge_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iCreateRoomPresenter.getImage();
                insertDataWithOne();
            }
        });

        btn_confrim = (Button) findViewById(R.id.btn_confrim);
        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(et_room_title.getText().toString())&&Image!=null){

                    /**
                     * 上传图片
                     */
//                    uploadMovoieFile(Image);
                    UpdateRoomState(Image);
                }
            }
        });

    }

    public void UpdateRoomState(final File file){

        String roomId = CacheUtils.getString(context,io.agora.contract.utils.Constants.liveRoomID);
        if(!TextUtils.isEmpty(roomId)){
            LogUtils.e("有roomid--->"+roomId);
            BmobQuery<LiveVideos> query = new BmobQuery<LiveVideos>();
            query.getObject(roomId, new QueryListener<LiveVideos>() {

                @Override
                public void done(LiveVideos liveVideos, BmobException e) {
                    if(e==null){
                        LogUtils.e("数据中有房间--->");
                        UpdateRoom(file,liveVideos);
                    }else{
                        LogUtils.e("查询房间出错--->"+e.toString());
                    }
                }

            });
        }else{
            LogUtils.e("没有有roomid--->"+roomId);
            //先查询到数据的id
            BmobQuery<LiveVideos> query = new BmobQuery<>();
            query.addWhereEqualTo("anchorName", BmobUser.getCurrentUser().getObjectId());
            query.findObjects(new FindListener<LiveVideos>() {
                @Override
                public void done(List<LiveVideos> list, BmobException e) {
                    if(list!=null&&list.size()>0){

                        //查询成功，得到当前liveVideoId
                        String liveId = list.get(0).getObjectId();
                        CacheUtils.putString(context, io.agora.contract.utils.Constants.liveRoomID,liveId);
                        final LiveVideos liveVideos = list.get(0);
                        Log.e("Com","更新图片上一句走不走----->");
                        ComplainTimes = liveVideos.getComplainTimes();
                        if(ComplainTimes>10){
                            Toast.makeText(CreateRoomActivity.this,"大佬，你被多次举报，不能再进行直播了，除非给我钱！",Toast.LENGTH_LONG).show();
                        }else{

                            UpdateRoom(file,liveVideos);
                        }

                    }else if(e!=null){
                        Log.e("Com","查找報錯了------>");
                        uploadMovoieFile(Image);
                    }else if(e == null){
                        Log.e("Com","查找報錯了------>e==null");
                        uploadMovoieFile(Image);
                    }
                }
            });
        }


    }


    @Override
    protected void deInitUIandEvent() {

    }

    public void UpdateRoom(File file,final LiveVideos liveVideos){

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("创建房间中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogOnKeyListener());
        dialog.show();
        bmobFile = new BmobFile(file);


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

//                            liveVideos.setAnchorName(BmobUser.getCurrentUser(MyUser.class));
                liveVideos.setLiving(true);
                liveVideos.setLiveTitle(et_room_title.getText().toString());
                liveVideos.setImageUrl(bmobFile);
                liveVideos.increment("LiveTimes",1);
                liveVideos.setAudience(0);

                return upDateObservable(liveVideos);
            }
        }).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {


            }

            @Override
            public void onError(Throwable e) {
                Log.e("Com", "屌你更新图片出错了----->"+e.toString());
                Toast.makeText(CreateRoomActivity.this,"创建房间失败----->"+e.toString(),Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                choosedFile=null;
            }

            @Override
            public void onNext(Void s) {
                                dialog.dismiss();
                                choosedFile=null;
                                Log.e("Com","完成？？");
                                Intent i = new Intent(CreateRoomActivity.this, LiveRoomActivity.class);
                                i.putExtra(ConstantApp.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                                i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, BmobUser.getCurrentUser().getObjectId());
                                startActivity(i);
                finish();
//
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
                fm.setFileChooserListener(this);
            }
            Log.i("Com", "Probable file size: " + fm.queryProbableFileSize(data.getData(), this));
            /*if (data != null) {
                iv_imge_add.setImageURI(data.getData());
            }*/
            fm.submit(requestCode, data);
        }

        if (requestCode == 0x1 && resultCode == RESULT_OK)
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFileChosen(final ChosenFile file) {
        choosedFile = file;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("life", choosedFile.getFilePath());
//                showFileDetails(file);
                Image = new File(choosedFile.getFilePath());
                iv_imge_add.setImageURI(Uri.parse(choosedFile.getFilePath()));
//                uploadMovoieFile(Image);
            }
        });
    }

    /** 插入单条数据（单个BmobFile列）
     * 例如：插入单条电影
     * @return void
     * @throws
     */
    private void insertDataWithOne(){
        if(choosedFile ==null){
//            showToast("请先选择文件");
            pickFile();
            return;
        }
    }

    public void pickFile() {
        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);
        try {
            fm.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String s) {

    }

    private static String url="";

    ProgressDialog dialog =null;

    /** 上传指定路径下的电影文件
     * @param file
     * @return void
     */
    private void uploadMovoieFile(File file) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("创建房间中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogOnKeyListener());
        dialog.show();
        bmobFile = new BmobFile(file);
        //这个用到了类似rxjava的方式开发
        bmobFile.uploadObservable(new ProgressCallback() {//上传文件操作
            @Override
            public void onProgress(Integer value, long total) {
                dialog.setProgress(value);
            }
        }).doOnNext(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                url = bmobFile.getUrl();

            }
        }).concatMap(new Func1<Void, Observable<String>>() {//将bmobFile保存到movie表中
            @Override
            public Observable<String> call(Void aVoid) {

                LiveVideos  liveVideos = new LiveVideos();
                liveVideos.setAnchorName(BmobUser.getCurrentUser(MyUser.class));
                liveVideos.setImageUrl(bmobFile);
                liveVideos.setLiveTitle(et_room_title.getText().toString());
                liveVideos.setLiving(true);
                liveVideos.increment("LiveTimes",1);
                liveVideos.setGift_times(0);
                liveVideos.setLikes(0);

                return saveObservable(liveVideos);
            }
        }).concatMap(new Func1<String, Observable<String>>() {//下载文件
            @Override
            public Observable<String> call(String s) {
                return bmobFile.downloadObservable(new ProgressCallback() {
                    @Override
                    public void onProgress(Integer value, long total) {
//                        log("download-->onProgress:"+value+","+total);
                    }
                });
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {


            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CreateRoomActivity.this,"创建房间失败----->"+e.toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
                choosedFile=null;
            }

            @Override
            public void onNext(String s) {
                dialog.dismiss();
                choosedFile=null;
                Log.e("Com","完成？？");
                Intent i = new Intent(CreateRoomActivity.this, LiveRoomActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_BROADCASTER);
                i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, BmobUser.getCurrentUser().getObjectId());
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * 先查询是否有房间了，没有就新开一个房间，如果有了，就更新状态就可以了
     */
    /*private liveVideos saveOrUpdateRoom(BmobFile bmobFile){

        final LiveVideos liveVideos;
        //先查询到数据的id
        BmobQuery<LiveVideos> query = new BmobQuery<>();
        query.addWhereEqualTo("AnchorName", BmobUser.getCurrentUser().getObjectId());
        query.findObjects(new FindListener<LiveVideos>() {
            @Override
            public void done(List<LiveVideos> list, BmobException e) {
                if(list!=null&&list.size()>0){


                    //查询成功，得到当前liveVideoId
                    String liveId = list.get(0).getObjectId();
                    liveVideos = new LiveVideos();
                    liveVideos.setLiving(false);
                    //更新主播状态
                    liveVideos.update(liveId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                return liveVideos;
                            }else{
                                Log.e("Com","离开主播界面失败---->"+e.toString());
                            }
                        }
                    });

                }else if(e==null){

                }
            }
        });
        return
    }*/

    public void back(View view){

        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            LogUtils.e("创建房间点击返回---->");


        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Dialog 监听返回事件
     * @author lizhiting
     *
     */
    public class DialogOnKeyListener implements DialogInterface.OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
                LogUtils.e("创建房间点击返回dialog---->");
                if(bmobFile != null){
                    /**
                     * 这样是不行的，因为dialog会消费activity的返回事件，所以在这里写，没有作用(activity)
                     * 通过重写，DialogInterfacce.OnekeyListener重写返回键监听
                     */
                    LogUtils.e("创建房间取消---->");
                    //当在创建房间过程中，点击返回，取消创建房间
                    bmobFile.cancel();
                    return true;
                }

            }
            return false;
        }

    }

    /**
     * save的Observable
     * @param obj
     * @return
     */
    private Observable<String> saveObservable(BmobObject obj){
        return obj.saveObservable();
    }

    private Observable<Void> upDateObservable(BmobObject obj){ return obj.updateObservable();}

    private Observable<Void> updateObservableByID(BmobObject obj,String id){
        return obj.updateObservable(id);
    }

}
