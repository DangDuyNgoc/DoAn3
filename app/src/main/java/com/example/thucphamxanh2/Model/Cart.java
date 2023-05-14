package com.example.thucphamxanh2.Model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private int idProduct,idCart,idCategory;
    private String imgProduct;
    private String idPartner;
    private String productDiscount;
    private String nameProduct,userClient;
    private int priceProduct;
    private int numberProduct;
    private int totalPrice;

    public Cart() {
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public int getIdCart() {
        return idCart;
    }

    public void setIdCart(int idCart) {
        this.idCart = idCart;
    }

    public String getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(String idPartner) {
        this.idPartner = idPartner;
    }

    public String getUserClient() {
        return userClient;
    }

    public void setUserClient(String userClient) {
        this.userClient = userClient;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(int priceProduct) {
        this.priceProduct = priceProduct;
    }

    public int getNumberProduct() {
        return numberProduct;
    }

    public void setNumberProduct(int numberProduct) {
        this.numberProduct = numberProduct;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nameProduct",getNameProduct());
        result.put("numberProduct",getNumberProduct());
        result.put("totalPrice",getTotalPrice());
        result.put("idPartner",getIdPartner());
        result.put("idProduct",getIdProduct());
        result.put("imgProduct",getImgProduct());
        result.put("priceProduct",getPriceProduct());
        result.put("productDiscount",getProductDiscount());
        return result;
    }
}
