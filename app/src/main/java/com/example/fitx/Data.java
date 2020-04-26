package com.example.fitx;

public class Data {
    String userHeight;
      String userWeight;
      String Age;
     String Gender;
     String BMI;

    public void Data() {

    }

    public Data(String userHeight, String userWeight, String age, String gender, String bmi) {

        //adding new stuff
        String userHeightX = "";
        for(int i = 0;i<userHeight.length();i++) {
            if(userHeight.charAt(i) == 's') {
                userHeightX+=userHeight.charAt(i);
                break;
            }
            else
                userHeightX+=userHeight.charAt(i);
        }


        //weight

        String userWeightX = "";
        for(int i = 0;i<userWeight.length();i++) {
            if(userWeight.charAt(i) == 's') {
                userWeightX+=userWeight.charAt(i);
                break;
            }
            else
                userWeightX+=userWeight.charAt(i);
        }

        //age

        String userAgeX = "";
        for(int i = 0;i<age.length();i++) {
            if(age.charAt(i) == 's') {
                userAgeX+=age.charAt(i);
                break;
            }
            else
                userAgeX+=age.charAt(i);
        }



        this.userHeight = userHeightX;
         this.userWeight = userWeightX;
         Age = userAgeX;
          Gender = gender;
          BMI = bmi;
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

    public String getBMI() {
        return BMI;
    }


}


