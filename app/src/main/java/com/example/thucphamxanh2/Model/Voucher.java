package com.example.thucphamxanh2.Model;

import java.util.HashMap;
import java.util.Map;

public class Voucher {
    private int idVoucher;
    private double codeVoucher_double;
    private String codeVoucher, imgVoucher;

    public Voucher() {
    }

    public double getCodeVoucher_double() {
        return codeVoucher_double;
    }

    public void setCodeVoucher_double(double codeVoucher_double) {
        this.codeVoucher_double = codeVoucher_double;
    }

    public int getIdVoucher() {
        return idVoucher;
    }

    public void setIdVoucher(int idVoucher) {
        this.idVoucher = idVoucher;
    }

    public String getCodeVoucher() {
        return codeVoucher;
    }

    public void setCodeVoucher(String codeVoucher) {
        this.codeVoucher = codeVoucher;
    }

    public String getImgVoucher() {
        return imgVoucher;
    }

    public void setImgVoucher(String imgVoucher) {
        this.imgVoucher = imgVoucher;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("idVoucher",getIdVoucher());
        result.put("codeVoucher",getCodeVoucher());
        result.put("imgVoucher",getImgVoucher());

        return result;
    }
}
