package io.agora.contract.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.shuyu.frescoutil.FrescoHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobUser;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import io.agora.contract.adapter.CommentAdapter;
import io.agora.contract.utils.Constants;
import io.agora.contract.utils.HighLightKeyWordUtil;
import io.agora.contract.utils.LogUtils;
import io.agora.contract.view.MyJCVideoPlayerStandard;
import io.agora.model.Comment;
import io.agora.model.CommentBean;
import io.agora.model.MyUser;
import io.agora.openlive.R;
import io.agora.openlive.ui.BaseActivity;
import io.agora.presenter.ICommentPresenter;
import io.agora.presenter.ICommentPresenterImpl;
import pl.droidsonroids.gif.GifImageView;

/**
 * File Name:  评论详情页
 * Author:      ruan
 * Write Dates: 2017/8/17
 * Description:
 */

public class CommentActivity extends BaseActivity implements IComment{

    RecyclerView rv_comment;
    EditText et_comment_content;
    Button btn_send;

    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 当前类型
     */
    public int currentType = TYPE_IMAGE;

    Context context;
    String url;

    CommentBean commentBean;
    ArrayList<CommentBean> commentBeens;
    CommentAdapter commentAdapter;
    String contentId;

    ICommentPresenter iCommentPresenter;
    Comment comment;

    int reply = 0;//标识是回复，还是评论。0：评论，1：回复
    String replyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        context = this;
    }

    @Override
    protected void initUIandEvent() {

        iCommentPresenter = new ICommentPresenterImpl(this);
        Intent intent = getIntent();
        currentType = intent.getIntExtra(Constants.TYPE_COMMENT,TYPE_GIF);
        contentId = intent.getStringExtra(Constants.CONTENT_ID);
        commentBean = new CommentBean();
        commentBeens = new ArrayList<CommentBean>();
        commentBean.setCurrentType(currentType);


        et_comment_content = (EditText) findViewById(R.id.et_comment_content);
        btn_send = (Button) findViewById(R.id.btn_send);
        rv_comment = (RecyclerView) findViewById(R.id.rv_comment);

        String title = intent.getStringExtra(Constants.COMMENT_TITLE);
        commentBean.setTitle(title);
        String origin = intent.getStringExtra(Constants.COMMENT_ORGIN);
        commentBean.setOrigin(origin);
        if(currentType == TYPE_GIF){
            //如果是gif,就是显示gif
            url = intent.getStringExtra(Constants.TYPE_URL);
            commentBean.setUrl(url);
            LogUtils.e("评论界面gigUrl--->"+url);
        }else if(currentType == TYPE_IMAGE){
            //显示图片
            url = intent .getStringExtra(Constants.TYPE_URL);
            commentBean.setUrl(url);
//            FrescoHelper.loadBigImage(context, iv_image_icon,  url, R.drawable.bg_item);
        }else if(currentType == TYPE_TEXT){
            //显示文本
            String content = intent.getStringExtra(Constants.TEXT_CONTENT);
            commentBean.setTextContent(content);
        }else if(currentType == TYPE_VIDEO){
            //显示视频
            String times = intent.getStringExtra(Constants.PLAY_TIMES);
            commentBean.setVideoPlayNums(times);
            String duration = intent.getStringExtra(Constants.DURATION_VIDEO);
            commentBean.setVideoDuration(duration);
            String thumbnail = intent.getStringExtra(Constants.VIDEO_THUMBS);
            url = intent .getStringExtra(Constants.TYPE_URL);
            commentBean.setVideoThumbnail(thumbnail);
            commentBean.setUrl(url);
        }
        //这个可是必加的一个commentbean
        commentBeens.add(commentBean);
        commentAdapter = new CommentAdapter(context,commentBeens);
        GridLayoutManager manager = new GridLayoutManager(context, 1);
        rv_comment.setLayoutManager(manager);
        rv_comment.setItemAnimator(new DefaultItemAnimator());
        rv_comment.setAdapter(commentAdapter);

        final MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_comment_content.getText().toString();
                comment = new Comment();//这是往后台存储的Bean对象
                commentBean = new CommentBean();//这个是界面更新需要用到的Bean对象
                comment.setContentId(contentId);//唯一标识
                LogUtils.e("获取评论传过来的id--->"+contentId);
                comment.setName(myUser.getUser_id_name());//评论人名字
                commentBean.setName(myUser.getUser_id_name());
                if(!TextUtils.isEmpty(myUser.getAuthIconUrl())){
                    //不为空才传入第三方头像链接
                    comment.setIcon_url(myUser.getAuthIconUrl());
                    commentBean.setIcon_url(myUser.getAuthIconUrl());
                }else if(myUser.getHead_icon()!=null){
                    //不为空时传入本地注册头像链接
                    comment.setIcon_url(myUser.getHead_icon().getUrl());
                    commentBean.setIcon_url(myUser.getHead_icon().getUrl());
                }else{
                    //当都为空时，传入空
                    comment.setIcon_url("");
                }
                String currentTime= DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date()).toString();//当前更新时间
                comment.setLike_time("1");//点赞数
                commentBean.setLike_time("1");//点赞数
                if(reply == 1){

                    content = "回复"+replyName+"："+content;
                    commentBean.setReplyName(replyName);
                    comment.setReplyName(replyName);
                    comment.setContent(content);
                    reply = 0;
                    et_comment_content.setHint("不能忍了，让我说两句");
                }else{

                    comment.setContent(content);//评论内容
                }
                commentBean.setContent(content);
                commentBean.setTime(currentTime);
                commentBeens.add(commentBean);//加入到list中
                iCommentPresenter.updataData(comment);//更新到后台
                commentAdapter.notifyDataSetChanged();//刷新界面
                et_comment_content.setText("");//发表成功后，设置输入框为空
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//收起软键盘
                imm.hideSoftInputFromWindow(et_comment_content.getWindowToken(), 0) ;
            }
        });
        iCommentPresenter.getData(contentId);

        commentAdapter.setOnItemClickLitener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder viewHolder) {

                replyName = commentBeens.get(position).getName();
                et_comment_content.setHint("回复"+replyName+":");
                reply = 1;
                showInputMethod(et_comment_content,true,500);
            }
        });

    }

    @Override
    protected void deInitUIandEvent() {
        //把数据清空
        commentBeens.clear();
        commentBeens = null;
        comment = null;
        commentBean = null;

    }

    @Override
    public void getDataResult(int code, ArrayList<CommentBean> comments) {
        //获取到数据结果
        if(code==0&&comments.size()>0){

            commentBeens.addAll(comments);
            commentAdapter.notifyDataSetChanged();

        }else if(code == -1){
            Toast.makeText(context,"获取评论数据出错！",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 更新评论数据结果返回
     * @param code
     * @param result
     */
    @Override
    public void updateResult(int code, String result) {

        if(code == 0){
            LogUtils.e("评论界面上传评论成功-->"+result);
            Toast.makeText(context,"发表成功！",Toast.LENGTH_SHORT).show();

        }else{
            LogUtils.e("评论界面上传评论失败-->"+result);
            Toast.makeText(context,"发表失败！"+result,Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将焦点移到输入框，弹起输入法
     */
    public void focusKeywordView(EditText edt) {
        if (edt != null) {
            edt.requestFocus();
            edt.setSelection(getKeywordText(edt).length());
            showInputMethod(edt, true, 500);
        }
    }

    /**
     * 弹起输入法
     * @param edit
     * @param delay
     * @param delayTime
     */
    private void showInputMethod(final EditText edit, boolean delay, int delayTime) {
        if (delay) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(edit, 0);
                    }

                }
            }, delayTime);
        } else {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, 0);
        }
    }
    public String getKeywordText(EditText edt) {
        return edt.getText().toString().trim();
    }

}
