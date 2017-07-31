package io.agora.model;

import java.util.ArrayList;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/31
 * Description:
 */

public class NewVideoPagerBean {

    public ArrayList<MediaItem> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<MediaItem> trailers) {
        this.trailers = trailers;
    }

    ArrayList<MediaItem> trailers;

}
