package com.unfollowerapp.mobilweb;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class URLDATA {
    public   int kulID;
    public String kulURL;


    public int getKulID() {
        return kulID;
    }

    public String getKulURL() {
        return kulURL;
    }

    public void setKulID(int kulID) {
        this.kulID = kulID;
    }

    public void setKulURL(String kulURL) {
        this.kulURL = kulURL;
    }
}
