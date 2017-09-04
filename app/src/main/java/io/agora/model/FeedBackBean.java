package io.agora.model;

import cn.bmob.v3.BmobObject;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/30
 * Description:
 */

public class FeedBackBean extends BmobObject {

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String contentId;
    String icon_url;
    String name;
    String time;
    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
