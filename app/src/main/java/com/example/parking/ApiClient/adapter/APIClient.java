package com.example.parking.ApiClient.adapter;

import android.content.Context;
import android.util.Log;

import com.example.parking.ApiClient.AppConfig;
import com.example.parking.ApiClient.Enums.RequestMethod;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;




    public static Retrofit getClient(Context context, RequestMethod requestMethod) {



        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttp(context,requestMethod))
                    .build();

        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        return retrofit;
    }

    public static OkHttpClient getHttp(Context context, RequestMethod requestMethod) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (!AppConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


//        X509TrustManager trustManager = new X509TrustManager() {
//            public void checkClientTrusted(X509Certificate[] xcs, String string)
//                    throws CertificateException {}
//            public void checkServerTrusted(X509Certificate[] xcs, String string)
//                    throws CertificateException {}
//            public X509Certificate[] getAcceptedIssuers() {
//                //Here
//                return new X509Certificate[]{};
//            }
//        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new APIinterceptor(context,requestMethod))
                .hostnameVerifier((hostname,session)->true)
                .build();

        Log.d("UserController","UserController");
        return okHttpClient;

    }


}
