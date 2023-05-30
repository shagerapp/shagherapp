package com.example.parking.Models;

public class Account {


    private  String fname;
    private  String lname;
    private  String  phoneNum;
    private  String pass;

    public Account(String fname, String lname, String phoneNum, String pass) {
        this.fname = fname;
        this.lname = lname;
        this.phoneNum = phoneNum;
        this.pass = pass;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
