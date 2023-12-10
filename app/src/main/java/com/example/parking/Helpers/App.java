package com.example.parking.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parking.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class App {


    public static void logOut(Context context) {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public  static void setupLanguageCode(AppCompatActivity activity,String languageCode)
    {
        Locale locale = new Locale(languageCode); // قم بتعيين قيمة languageCode إلى رمز اللغة الجديدة المطلوبة
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
        activity.recreate();
    }
}
