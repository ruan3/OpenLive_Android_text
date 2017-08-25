package io.agora.model;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/21
 * Description:
 */

public class CommentBean {

    String url;
    String TextContent;
    String videoDuration;
    String videoPlayNums;
    String videoThumbnail;
    String title;
    String origin;

    String icon_url;
    String name;
    String time;
    String like_time;
    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getLike_time() {
        return like_time;
    }

    public void setLike_time(String like_time) {
        this.like_time = like_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTextContent() {
        return TextContent;
    }

    public void setTextContent(String textContent) {
        TextContent = textContent;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoPlayNums() {
        return videoPlayNums;
    }

    public void setVideoPlayNums(String videoPlayNums) {
        this.videoPlayNums = videoPlayNums;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    int currentType;

}
