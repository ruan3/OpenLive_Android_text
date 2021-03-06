package io.agora.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/6
 * Description:
 */

public class LiveVideos extends BmobObject{
    public MyUser getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(MyUser anchorName) {
        this.anchorName = anchorName;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public Integer getAudience() {
        return audience;
    }

    public void setAudience(Integer audience) {
        this.audience = audience;
    }

    public String getLiveTitle() {
        return LiveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        LiveTitle = liveTitle;
    }

    MyUser anchorName;
    String RoomID;
    Integer audience;
    String LiveTitle;

    public Integer getGift_times() {
        return gift_times;
    }

    public void setGift_times(Integer gift_times) {
        this.gift_times = gift_times;
    }

    Integer gift_times;

    public Integer getLiveTimes() {
        return LiveTimes;
    }

    public void setLiveTimes(Integer liveTimes) {
        LiveTimes = liveTimes;
    }

    Integer LiveTimes;


    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    Integer likes;

    public boolean isLiving() {
        return isLiving;
    }

    public void setLiving(boolean living) {
        isLiving = living;
    }

    boolean isLiving;

    public BmobFile getImageUrl() {
        return Image;
    }

    public void setImageUrl(BmobFile imageUrl) {
        Image = imageUrl;
    }

    BmobFile Image;

}
