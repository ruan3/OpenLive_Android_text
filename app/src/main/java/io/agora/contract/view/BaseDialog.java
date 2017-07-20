package io.agora.contract.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.agora.openlive.R;


/**
 * @ClassName: BaseDialog
 * @Description: TODO(所有对话框的基类)
 * @author ruan
 * @date 2017-6-13 上午10:05:42
 *
 */
public class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context, R.style.dialog);
		// TODO Auto-generated constructor stub
	}

	public BaseDialog(Context context, int style) {
		super(context, style);
	}

	/**
	 * @Description: TODO(基类的布局文件)
	 * @param @return 设定文件
	 * @return （View）
	 * @throws
	 */
	public FrameLayout getContentView() {
		View v = this.findViewById(R.id.base_dialog_layout);
		if (v != null) {
			return (FrameLayout) v;
		}
		return null;
	}

	Drawable drawable = new ColorDrawable() {
		Paint paint = new Paint();

		@Override
		public void draw(Canvas canvas) {
			Rect rect = canvas.getClipBounds();
			paint.setStyle(Paint.Style.STROKE);

			paint.setStrokeWidth(0.5f);
			paint.setColor(Color.argb(200, 0, 0, 40));
			canvas.drawRoundRect(new RectF(0, 0, rect.width(), rect.height()),
					10, 10, paint);

			paint.setStrokeWidth(1.0f);
			paint.setColor(Color.argb(200, 200, 200, 200));
			for (int i = 1; i < 2; i++) {
				canvas.drawRoundRect(
						new RectF(i, i, rect.width() - i * 2,
								rect.height() - 1 * 2), 10, 10, paint);
			}

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.argb(200, 0, 0, 40));

			canvas.drawRoundRect(
					new RectF(2, 2, rect.width() - 4, rect.height() - 4), 10,
					10, paint);

			paint.setStyle(Paint.Style.STROKE);

			float height = rect.height() * 0.2f;

			for (int i = 1; i <= height; i++) {
				paint.setColor(Color.argb(255 - i * 3, 200, 200, 200));
				canvas.drawCircle(rect.width() / 2, i - rect.width() * 2,
						rect.width() * 2, paint);
			}

		}
	};

	/**
	 * @Description: TODO(设置外部布局文件)
	 * @return （返回类型）
	 * @throws
	 */
	@SuppressLint("NewApi") public void setContentView(int layoutResID) {

		/*
		 * LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		 * ViewGroup view = getContentView(); if(view != null){
		 * layoutInflater.inflate(layoutResID, view); super.setContentView(view,
		 * new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		 * ViewGroup.LayoutParams.WRAP_CONTENT)); }
		 */
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		FrameLayout contentView = (FrameLayout) layoutInflater.inflate(
				R.layout.dialog_layout_base, null);

//		contentView.setBackground(drawable);
		layoutInflater.inflate(layoutResID, contentView);
		super.setContentView(contentView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * 点击对话框右上角可以关闭对话框
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getWindow().getDecorView();
			float disX = v.getWidth() - event.getX();
			float disY = event.getY();
			if ((disX > 0 && disX < v.getWidth() * 0.1)
					&& (disY > 0 && disY < v.getHeight() * 0.2)) {
				if (beforeDismiss()) {
					this.dismiss();
				}
				return true;
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 点击对话框右上角时，是否关闭对话框，默认为不关闭
	 *
	 * @return
	 */
	public boolean beforeDismiss() {
		return false;
	}

}
