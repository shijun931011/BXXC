package com.jgkj.bxxc.tools;

/**
 * Created by msq on 2016/9/12.
 */
public class BannerPage {

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String imagePath;

    public BannerPage(String path){
        this.imagePath = path;
    }
}
