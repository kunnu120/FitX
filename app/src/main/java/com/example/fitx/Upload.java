package com.example.fitx;

public class Upload {
    private String name;
    private String imageurl;

    public Upload(){
        //empty constructor needed
    }

    public Upload(String _name, String _imageurl){
        if(name.trim().equals("")){
            name = "No Name";
        }
        name = _name;
        imageurl = _imageurl;
    }

    public String getName(){
        return name;
    }

    public void setName(String _name){
        name = _name;
    }

    public String getImageurl(){
        return imageurl;
    }

    public void setImageurl(String _imgurl){
        imageurl = _imgurl;
    }
}
