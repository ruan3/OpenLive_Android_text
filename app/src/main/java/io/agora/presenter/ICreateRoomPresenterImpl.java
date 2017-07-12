package io.agora.presenter;

import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/10
 * Description:
 */

public class ICreateRoomPresenterImpl implements ICreateRoomPresenter,FileChooserListener {

    ChosenFile choosedFile;

    @Override
    public void createRoom() {

    }

    @Override
    public void getImage() {

    }

    @Override
    public void setRoomTitle() {

    }

    /** 插入单条数据（单个BmobFile列）
     * 例如：插入单条电影
     * @return void
     * @throws
     */
    private void insertDataWithOne(){
        if(choosedFile ==null){
//            showToast("请先选择文件");
//            pickFile();
            return;
        }
    }

    /*public void pickFile() {
        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);
        try {
            fm.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onFileChosen(ChosenFile chosenFile) {

    }

    @Override
    public void onError(String s) {

    }
}
