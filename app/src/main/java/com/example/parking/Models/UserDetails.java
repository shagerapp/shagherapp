package com.example.parking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserDetails  implements Serializable {

    private  transient String  userId;

    @SerializedName("phone")
    @Expose
    private  String  phone;

    @SerializedName("role")
    @Expose
    private  String  role;

    @SerializedName("cars")
    @Expose
    private List<String> carNumbers;

    public UserDetails()
    {
//        carNumbers=new ArrayList<>();
    }


//    @Exclude
//    public Map<String,Object>  toMap()
//    {
//
//        HashMap<String, Object> result = new HashMap<>();
//
//        result.put("role", Roles.USER.getValue());
//
//        if(!phone.isEmpty())
//            result.put("phone", carNumbers);
//
//        if(!carNumbers.isEmpty())
//            result.put("cars", carNumbers);
//
//
//
//        return result;
//    }



    public void setUserId(String userId)
    {
        this.userId = userId;
    }
//    public void setPhoneNumber(String phone)
//    {
//        this.phone = phone;
//    }

//    public void setCarNumber(String carNum)
//    {
//        this.carNumbers.add(carNum) ;
//    }

    public List<String> getCarNumbers( ) {
        return this.carNumbers;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCarNumbers(List<String> carNumbers) {
        this.carNumbers = carNumbers;
    }

}
