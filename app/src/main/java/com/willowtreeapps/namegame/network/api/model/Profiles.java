package com.willowtreeapps.namegame.network.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Profiles implements Parcelable {

    private List<Person> items;
    private Metadata meta;

    public Profiles(List<Person> items, Metadata meta) {
        this.items = items;
        this.meta = meta;
    }

    private Profiles(Parcel in) {
        this.items = new LinkedList<>();
        in.readList(this.items, Person.class.getClassLoader());
        this.meta = in.readParcelable(Metadata.class.getClassLoader());
    }

    public List<Person> getPeople() {
        return items;
    }
    public void setPeople(List<Person> people){
        this.items=people;
    }
    public Metadata getMetadata() {
        return meta;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.items);
        dest.writeParcelable(this.meta, flags);
    }

    public static final Creator<Profiles> CREATOR = new Creator<Profiles>() {
        @Override
        public Profiles createFromParcel(Parcel source) {
            return new Profiles(source);
        }

        @Override
        public Profiles[] newArray(int size) {
            return new Profiles[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}