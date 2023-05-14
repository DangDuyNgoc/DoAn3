package com.example.thucphamxanh2.Model;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private int codeProduct;
    private int codeCategory;
    private int priceProduct;
    private String nameProduct,imgProduct,userPartner, productDiscount;

    public Product() {
    }

    public int getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(int codeProduct) {
        this.codeProduct = codeProduct;
    }

    public int getCodeCategory() {
        return codeCategory;
    }

    public void setCodeCategory(int codeCategory) {
        this.codeCategory = codeCategory;
    }

    public String getUserPartner() {
        return userPartner;
    }

    public void setUserPartner(String userPartner) {
        this.userPartner = userPartner;
    }

    public int getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(int priceProduct) {
        this.priceProduct = priceProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("codeCategory",getCodeCategory());
        result.put("userPartner",getUserPartner());
        result.put("nameProduct",getNameProduct());
        result.put("priceProduct",getPriceProduct());
        result.put("imgProduct",getImgProduct());
        result.put("productDiscount",getProductDiscount());
        return result;
    }
}
