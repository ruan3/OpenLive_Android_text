package io.agora.presenter;

import android.content.Context;

import com.smile.filechoose.api.FileChooserListener;

import java.io.File;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/19
 * Description:
 */

public interface IMineSettingPresenter {

    public void pickFile(FileChooserListener fileChooserListener);

    public void uploadHeadIcon(File file,Context context);

    public void upDateUserName(String name);

    public void loginOut(Context context);

}
