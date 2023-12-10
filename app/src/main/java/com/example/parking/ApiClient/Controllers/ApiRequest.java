package com.example.parking.ApiClient.Controllers;

import com.example.parking.ApiClient.AppConfig;
import com.example.parking.ApiClient.interfaces.APIInterface;
import com.example.parking.ApiClient.interfaces.IResponce;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRequest<T> {


    public    void   executeRequest(Call<T>  call, IResponce<T> callback)  {

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {

                    if(callback==null)
                        return;
                    System.out.println("Request body: " + String.valueOf(response.code()));
                    callback.onSuccess(response.body());

                } else
                {
                    callback.onFailure(String.valueOf(response.code()));
                    // Request failed
                    System.out.println("Request failed " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                // Request failed due to network or other issues
                callback.onFailure(t.getMessage());
                System.out.println("Request failed : " + t.getMessage());
            }
        });
    }
    public static APIInterface  getService()  {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(APIInterface.class);

    }

    public static APIInterface  getService(String base_url)  {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(APIInterface.class);

    }



//    public Call<T>  executeRequest( T body) throws IOException {
//
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//        RequestBody requestBody = RequestBody.create(new Gson().toJson(body), mediaType);
//
//
//        Request request = new Request.Builder()
//                .url(BuildConfig.SUB_URL)
//                .post(requestBody)
//                .build();
//
//        try {
//            // Execute the request
//            Response response = new OkHttpClient().newCall(request).execute();
//
//            BaseResponse res=null;
//            // Check the response
//            if (response.isSuccessful())
//            {
//                String responseBody = response.body().string();
//                res=new Gson().fromJson(responseBody,BaseResponse.class);
//                // Process the successful response
//                System.out.println("Request successful. Response: " + responseBody);
//
//            }
//            else
//            {
//                // Handle the error response
//                System.out.println("Request failed. Response code: " + response.code());
//            }
//
//            return (Call<T>) res;
//
//        } catch (IOException e) {
//            // Handle the exception
//            e.printStackTrace();
//        }
//
//        return  null;
//    }

}
