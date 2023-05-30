package com.example.parking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public   class VerifyInputs {

  public  static String  EmailError="Please enter the valid e-mail";

  public  static String  ConfirmPasswordError="password does not match";
  public  static String  PasswordError="password must contain latters ,numbers and symbols ";
  //and the size  greater than 8 character
    private static boolean CheckedPattren(String  regex,String value)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    public  static  boolean  VerifyEmail(String email) {

        if(email==null)
            return false;

        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return CheckedPattren(regex,email);

    }
    public  static  boolean  VerifyPassword(String password)
    {
        String regex ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        return CheckedPattren(regex,password);
    }

    public  static  boolean  VerifyDate(String date)
    {
        String regex = "^((2000|2400|2800|(19|2[0-9])(0[48]|[2468][048]|[13579][26]))-02-29)$" +
                         "|^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$" +
                         "|^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$" +
                         "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$";

        return CheckedPattren(regex,date);
    }

    public  static  boolean  VerifyTime(String time)
    {
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        return CheckedPattren(regex,time);
    }

}
