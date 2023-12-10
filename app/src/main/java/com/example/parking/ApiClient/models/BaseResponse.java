package com.example.parking.ApiClient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("resultCode")
    @Expose
    private int resultCode;

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("resultMessage")
    @Expose
    private String resultMessage;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getResultCode() {
        return resultCode;
    }
    public String getResultMessage() { return resultMessage;}

    public void setResultCode(int resultCode) {this.resultCode = resultCode;}

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

}
