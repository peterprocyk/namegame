package com.willowtreeapps.namegame.network.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialLink implements Parcelable{
    String type;
    String action;
    String url;

    public SocialLink(String type, String action, String url){
        this.type=type;
        this.action=action;
        this.url=url;
    }

    protected SocialLink(Parcel in) {
        type = in.readString();
        action = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(action);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocialLink> CREATOR = new Creator<SocialLink>() {
        @Override
        public SocialLink createFromParcel(Parcel in) {
            return new SocialLink(in);
        }

        @Override
        public SocialLink[] newArray(int size) {
            return new SocialLink[size];
        }
    };
}
