package com.project.naveen.smarteventsshare;

public class TrendData {
    String user_name,img_url,profileImgUrl,comment,date,views,likes,post_id;
    Boolean like;

    public TrendData(String user_name, String img_url, String profileImgUrl, String comment, String date, String views, String likes, String post_id, Boolean like) {
        this.user_name = user_name;
        this.img_url = img_url;
        this.profileImgUrl = profileImgUrl;
        this.comment = comment;
        this.date = date;
        this.views = views;
        this.likes = likes;
        this.post_id = post_id;
        this.like = like;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }
}
