package io.agora.contract.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import io.agora.contract.view.BaseDialog;
import io.agora.model.MyUser;
import io.agora.openlive.R;


/**
 * 
 * 
 * @ClassName: DialogUtils
 * @Description: TODO(管理所有对话框)
 * @author ruan
 * @date 2017-6-13 下午1:50:00
 * 
 */
public class DialogUtils {


	BaseDialog dialog;


	private static DialogUtils dialogUtils = new DialogUtils();

	public static DialogUtils getInstance() {
		return dialogUtils;
	}

	private CallBackListener reListener; // 声明接口对象
	
	public void showLogOut(final Context context, final CallBackListener reListener){
		
		this.reListener = reListener;

		dialog = new BaseDialog(context, R.style.dialog);
		dialog.setContentView(R.layout.xjj_dialog_exit);
		
		Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
		Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
		
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reListener.cancle("cancle");
				dialog.dismiss();

			}
		});
		
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reListener.confirm("OK");
			}
		});
		dialog.setCancelable(false);
		dialog.show();
		
	}

	public void showBindEmail(final Context context, final CallBackListener reListener){

		this.reListener = reListener;
		dialog = new BaseDialog(context,R.style.dialog);
		dialog.setContentView(R.layout.xjj_dialog_bind_email);
		final EditText et_email = (EditText) dialog.findViewById(R.id.et_email);
		Button btn_bind_email = (Button) dialog.findViewById(R.id.btn_bind_email);



		btn_bind_email.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final String email = et_email.getText().toString();
				BmobUser.resetPasswordByEmail(email, new UpdateListener() {

					@Override
					public void done(BmobException e) {
						if(e==null){
							reListener.confirm(email);
						}else{
							reListener.confirm("失败:" + e.getMessage());
						}
					}
				});
                dialog.dismiss();

			}
		});

		dialog.setCancelable(false);
		dialog.show();

	}


	public void isShow() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	
	public interface CallBackListener {
		public void confirm(String result);

		public void cancle(String str);
	}

	public void onDestory() {
	}

}
