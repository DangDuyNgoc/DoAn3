package com.example.thucphamxanh2.Model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String strUriAvatar;
    private String id;
    private Bitmap bitmapAvatar;
    private Uri uriAvatar;
    private String phoneNumber;
    private String name;
    private String email;
    private String address;
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStrUriAvatar() {
        return strUriAvatar;
    }

    public void setStrUriAvatar(String strUriAvatar) {
        this.strUriAvatar = strUriAvatar;
    }
    @Deprecated
    public Uri getUriAvatar() {
        return uriAvatar;
    }
    @Deprecated
    public void setUriAvatar(Uri uriAvatar) {
        this.uriAvatar = uriAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getBitmapAvatar() {
        return bitmapAvatar;
    }

    public void setBitmapAvatar(Bitmap bitmapAvatar) {
        this.bitmapAvatar = bitmapAvatar;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("id", id);
        res.put("name", name);
        res.put("strUriAvatar", strUriAvatar);
        res.put("email", email);
        res.put("address", address);
        res.put("phoneNumber", phoneNumber);
        res.put("password", password);
        return res;
    }
    @Override
    public String toString() {
        return "User{" +
                "strUriAvatar='" + strUriAvatar + '\'' +
                ", id='" + id + '\'' +
                ", bitmapAvatar=" + bitmapAvatar +
                ", uriAvatar=" + uriAvatar +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}