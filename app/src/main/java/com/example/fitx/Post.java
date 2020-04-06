package com.example.fitx;

public class Post {
    private String userid;
    private String postid;
    private String postText;
    private boolean hasImg;

    public Post() {}

    public Post(String _userid, String _postid, String _postText, boolean _hasImg) {
        userid = _userid;
        postid = _postid;
        postText = _postText;
        hasImg =_hasImg;
    }

    public String getUserid() {
        return userid;
    }

    public String getPostid() {
        return postid;
    }

    public String getPostText() {
        return postText;
    }

    public boolean getHasImg() {
        return hasImg;
    }

}