package com.example.thucphamxanh2.Model;

import androidx.recyclerview.widget.RecyclerView;

public class FavoviteModel {

//    private List<String> favoriteList;

    RecyclerView recyclerViewTop;
    private String itemText;
    private boolean isHidden;

    public FavoviteModel(RecyclerView recyclerView, String itemText) {
        this.recyclerViewTop = recyclerView;
        this.itemText = itemText;
        isHidden = false;
    }

//    public List<String> getFavoriteList() {
//        return favoriteList;
//    }


    public RecyclerView getRecyclerViewTop() {
        return recyclerViewTop;
    }

    public String getItemText() {
        return itemText;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
