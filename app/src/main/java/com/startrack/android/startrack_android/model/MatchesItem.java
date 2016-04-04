package com.startrack.android.startrack_android.model;

/**
 * Created by vrogovskiy on 2/14/16.
 */
public class MatchesItem {
    private String name;
    private String title;
    private String location;
    private String source;
    private String photoSrc;
    private boolean checked;

    public MatchesItem(String name, String title, String location, String source, String photoSrc, boolean checked) {
        this.name = name;
        this.title = title;
        this.location = location;
        this.source = source;
        this.photoSrc = photoSrc;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPhotoSrc() {
        return photoSrc;
    }

    public void setPhotoSrc(String photoSrc) {
        this.photoSrc = photoSrc;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
