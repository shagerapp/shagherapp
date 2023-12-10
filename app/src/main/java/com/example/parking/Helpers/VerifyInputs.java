package com.example.parking.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public   class VerifyInputs {

  public  static String  EmailError="!!صيغة البريد الالكتروني غير صالحة";
  public  static String  CarPlateError="!!رقم لوحة السيارة غير  صالح يجب ان يتكون من ثلاثة احرف  واربعة ارقام !!";
  public  static String  ConfirmPasswordError="كلمة السر غير مطابقة !!";
  public  static String  inputEmpty="لايمكن ترك هذا الحقل فارغ";
  public  static String  PhoneError=" رقم الهاتف غير صالح يجب ان يكون رقم الهاتف 10 ارقام ويجب ان يبداء برقم 0! ";
  public  static String  PasswordError="كلمة السر  يجب ان لا تقل عن 8 احرف وتحتوي  على حروف كبيرة وصغيرة وعلى ارقام ويجب ان تحتوي على رمز واحد على الاقل ";
  //and the size  greater than 8 character
    private static boolean CheckedPattren(String  regex,String value)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    public  static  boolean  VerifyEmail(String email) {

        if(email==null  || email.isEmpty())
            return false;

        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)*$";
        return CheckedPattren(regex,email);

    }

    public  static  boolean  VerifyPassword(String password)
    {
        String regex ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        return CheckedPattren(regex,password);
    }


    public  static  boolean  VerifyPhoneNumber(String phone)
    {
        String regex ="[0-9][0-9]{9}";
        return CheckedPattren(regex,phone);
    }

    public  static  boolean  VerifyCarPlate(String carId)
    {
        String regex = "^[A-Za-z]{3}\\d{4}$";
//         regex = "^[\\u0600-\\u06FF]{3}\\d{4}$";
        return carId.matches(regex);
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
