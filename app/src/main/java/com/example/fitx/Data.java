package com.example.fitx;

//Creating class data that has the information of the BMI - the user information is contained in string - height, weight, age and gender.
public class Data {
    String userHeight;
      String userWeight;
      String Age;
     String Gender;
     String BMI;

    public void Data() {

    }

    //Data passing the values of the user's information
    public Data(String userHeight, String userWeight, String age, String gender, String bmi) {

        //This is to make sure the string information i.e height string doesn't get copied over and over again while getting it from firebase!
        String userHeightX = "";
        for(int i = 0;i<userHeight.length();i++) {
            if(userHeight.charAt(i) == 's') {
                userHeightX+=userHeight.charAt(i);
                break;
            }
            else
                userHeightX+=userHeight.charAt(i);
        }


        //This is to make sure the string information i.e weight string doesn't get copied over and over again while getting it from firebase!
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
        //This is to make sure the string information i.e age string doesn't get copied over and over again while getting it from firebase!
        String userAgeX = "";
        for(int i = 0;i<age.length();i++) {
            if(age.charAt(i) == 's') {
                userAgeX+=age.charAt(i);
                break;
            }
            else
                userAgeX+=age.charAt(i);
        }


        //setting the this. values to the values provided in the parameter
        this.userHeight = userHeightX;
         this.userWeight = userWeightX;
         Age = userAgeX;
          Gender = gender;
          BMI = bmi;
    }
    //getter function to get userheight
    public String getUserHeight() {
        return userHeight;
    }

    //getter function to get userweight
    public String getUserWeight() {
        return userWeight;
    }

    //getter function to get userage
    public String getAge() {
        return Age;
    }

    //getter function to get usergender
    public String getGender() {
        return Gender;
    }

    //getter function to get userBMI
    public String getBMI() {
        return BMI;
    }


}


