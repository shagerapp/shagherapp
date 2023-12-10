package com.example.parking.Models;

public class UserAuth {

    public  String    name;
    public  String    email;
    public  String    phone;
    public  String    role;

    public UserAuth(String name, String email, String phone, String  role)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;

    }
}
