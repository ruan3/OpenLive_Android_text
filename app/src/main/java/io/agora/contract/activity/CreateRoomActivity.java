package io.agora.contract.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.ProgressCallback;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.agora.model.LiveVideos;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * File Name:
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
    }

    @Override
    protected void initUIandEvent() {

        iv_imge_add = (ImageView) findViewById(R.id.iv_iamge_add);
        et_room_title = (EditText) findViewById(R.id.et_room_title);

        iv_imge_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CreateRoomActivity.this,"加入图片",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        "image/*");
//                startActivityForResult(intent, 0x1);
                insertDataWithOne();
            }
        });

        btn_confrim = (Button) findViewById(R.id.btn_confrim);
        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateRoomActivity.this,"确认",Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void deInitUIandEvent() {

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
            if (data != null) {
                iv_imge_add.setImageURI(data.getData());
            }
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
                File mp3 = new File(choosedFile.getFilePath());
                iv_imge_add.setImageURI(Uri.parse(choosedFile.getFilePath()));
                uploadMovoieFile(mp3);
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
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadObservable(new ProgressCallback() {//上传文件操作
            @Override
            public void onProgress(Integer value, long total) {
//                log("uploadMovoieFile-->onProgress:"+value);
                dialog.setProgress(value);
            }
        }).doOnNext(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                url = bmobFile.getUrl();
//                log("上传成功："+url+","+bmobFile.getFilename());

            }
        }).concatMap(new Func1<Void, Observable<String>>() {//将bmobFile保存到movie表中
            @Override
            public Observable<String> call(Void aVoid) {

                LiveVideos liveVideos = new LiveVideos();
                liveVideos.setAnchorName(BmobUser.getCurrentUser(MyUser.class));
                liveVideos.setImageUrl(bmobFile);
                liveVideos.setLiveTitle(et_room_title.getText().toString());


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
//                log("--onCompleted--");
            }

            @Override
            public void onError(Throwable e) {
//                log("--onError--:"+e.getMessage());
                Toast.makeText(CreateRoomActivity.this,"屌你出错了----->"+e.toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
                choosedFile=null;
            }

            @Override
            public void onNext(String s) {
                dialog.dismiss();
                choosedFile=null;
//                log("download的文件地址："+s);
            }
        });
    }

    /**
     * save的Observable
     * @param obj
     * @return
     */
    private Observable<String> saveObservable(BmobObject obj){
        return obj.saveObservable();
    }

}
