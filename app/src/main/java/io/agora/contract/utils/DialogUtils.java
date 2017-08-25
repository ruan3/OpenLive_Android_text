package io.agora.contract.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
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

				if(Validator.isEmail(email)){
					LogUtils.e("使用邮箱找回密码");
					//使用邮箱找回密码
					BmobUser.resetPasswordByEmail(email, new UpdateListener() {

						@Override
						public void done(BmobException e) {
							if(e==null){
								reListener.confirm(email);
								dialog.dismiss();
							}else{
								reListener.confirm("失败:" + e.getMessage());
								dialog.dismiss();
							}
						}
					});
				}else if(Validator.isMobile(email)){
					//使用手机验证找回密码
					BmobSMS.requestSMSCode(email,"小鸡鸡密码重置", new QueryListener<Integer>() {

						@Override
						public void done(Integer smsId,BmobException ex) {
							if(ex==null){//验证码发送成功
								LogUtils.i("短信id："+smsId);//用于查询本次短信发送详情
								//跳转到重置密码界面
								dialog.dismiss();
								ShowResetPwd(context,reListener,smsId);
							}else{
								LogUtils.e("手机密码重置失败--->"+ ex.toString());
							}
						}
					});
				}else{
					reListener.confirm("失败：输入并不是邮箱或者手机号！");
					LogUtils.e("输入的不是邮箱或者手机号");
				}
			}
		});

		dialog.show();

	}


	public void isShow() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	/**
	 * 忘记密码重置（目前那个手机号重置居然没有收到信息，我也是醉了）
	 * @param context
	 * @param reListener
	 * @param smsId
	 */
	public void ShowResetPwd(Context context,final CallBackListener reListener,Integer smsId){

		this.reListener = reListener;
		dialog = new BaseDialog(context,R.style.dialog);
		dialog.setContentView(R.layout.xjj_dialog_reset_pwd);
		EditText et_code = (EditText) dialog.findViewById(R.id.et_code);
		EditText et_new_pwd = (EditText) dialog.findViewById(R.id.et_new_pwd);
		Button btn_reset = (Button) dialog.findViewById(R.id.btn_reset);
		if(smsId!=null){
			et_code.setText(smsId);
		}
		final String code = et_code.getText().toString();
		final String newPwd = et_new_pwd.getText().toString();

		btn_reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(!TextUtils.isEmpty(code)&&!TextUtils.isEmpty(newPwd)){

					BmobUser.resetPasswordBySMSCode(code,newPwd, new UpdateListener() {

						@Override
						public void done(BmobException ex) {
							if(ex==null){
								reListener.confirm("密码重置成功！");
								LogUtils.e("手机号密码重置成功！");
							}else{
								reListener.confirm("重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
								LogUtils.e("手机号密码重置失败--->"+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
							}
						}
					});
				}

			}
		});
		dialog.setCancelable(false);
		dialog.show();


	}

	/**
	 * 修改密码
	 * @param context
	 * @param reListener
	 */
	public void showChangePwd(Context context,final CallBackListener reListener){

		this.reListener = reListener;
		dialog = new BaseDialog(context,R.style.dialog);
		dialog.setContentView(R.layout.xjj_dialog_change_pwd);
		final EditText et_old_pwd = (EditText) dialog.findViewById(R.id.et_old_pwd);
		final EditText et_new_pwd = (EditText) dialog.findViewById(R.id.et_new_pwd);
		Button btn_change = (Button) dialog.findViewById(R.id.btn_change);
		btn_change.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 String oldPwd = et_old_pwd.getText().toString();
				 String newPwd = et_new_pwd.getText().toString();
				if(!TextUtils.isEmpty(oldPwd)&&!TextUtils.isEmpty(newPwd)){
					//当旧密码和新密码都不为空时
					BmobUser.updateCurrentUserPassword(oldPwd, newPwd, new UpdateListener() {

						@Override
						public void done(BmobException e) {
							if(e==null){
								LogUtils.e("修改成功！下次请使用新密码登录");
								reListener.confirm("修改成功！下次请使用新密码登录");
								dialog.dismiss();
							}else{
								LogUtils.e("修改成功！下次请使用新密码登录");
								reListener.cancle("修改失败--->"+e.toString());
								dialog.dismiss();
							}
						}

					});

				}else{
					reListener.cancle("旧密码或新密码为空！");
				}

			}
		});
		dialog.show();


	}
	
	public interface CallBackListener {
		public void confirm(String result);

		public void cancle(String str);
	}

	public void onDestory() {
	}

}
