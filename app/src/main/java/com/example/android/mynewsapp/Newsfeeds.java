package com.example.android.mynewsapp;

import android.graphics.Bitmap;

/**
 * Created by pc on 7/29/2018.
 */

public class Newsfeeds {
    private String mSectionName;
    private String mWebTitle;
    private String mWebUrl;
    private String mWebPublicationDate;
    private String mAuthorName = "";
    private Bitmap mThumbnail = null;

    public Newsfeeds(String sectionName, String webTitle, String webUrl){
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
    }

    public void setPublicationDate(String publicationDate){
        mWebPublicationDate = publicationDate;
    }

    public void setThumbnail(Bitmap thumbnail){
        mThumbnail = thumbnail;
    }

    public void setAuthorName(String authorName){
        mAuthorName = authorName;
    }

    public String getSectionName(){
        return mSectionName;
    }

    public String getWebTitle(){
        return mWebTitle;
    }

    public String getWebUrl(){
        return mWebUrl;
    }

    public String getWebPublicationDate(){
        return mWebPublicationDate;
    }

    public String getAuthorName(){
        return mAuthorName;
    }

    public Bitmap getThumbnail() {return mThumbnail;}

}
