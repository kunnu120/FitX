package com.example.fitx;

public class Data {
    String userHeight;
    String userWeight;
    String Age;
    String Gender;

    public void Data() {

    }

    public Data(String userHeight, String userWeight, String age, String gender) {
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        Age = age;
        Gender = gender;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public String getAge() {
        return Age;
    }

    public String getGender() {
        return Gender;
    }
}
