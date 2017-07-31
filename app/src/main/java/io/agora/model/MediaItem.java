package io.agora.model;

import java.io.Serializable;

/**
 * 作者 ruan on 2016/7/18 09:16
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：代表一个视频和音频
 */
public class MediaItem implements Serializable {

    private String movieName;

    private long duration;

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getHightUrl() {
        return hightUrl;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    private String hightUrl;


    private String videoTitle;

    private String coverImg;




    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + movieName + '\'' +
                ", duration=" + duration +
                ", data='" + hightUrl + '\'' +
                ", desc='" + videoTitle + '\'' +
                ", imageUrl='" + coverImg + '\'' +
                '}';
    }
}
