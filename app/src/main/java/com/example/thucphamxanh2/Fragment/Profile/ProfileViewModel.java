package com.example.thucphamxanh2.Fragment.Profile;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thucphamxanh2.Model.Partner;
import com.example.thucphamxanh2.Model.User;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> user;
    private final MutableLiveData<Partner> partner;
    private final MutableLiveData<Bitmap> bitmapImageAvatar;
    public ProfileViewModel() {
        user = new MutableLiveData<>();
        partner = new MutableLiveData<>();
        bitmapImageAvatar = new MutableLiveData<>();
    }
    public void setBitmapImageAvatar(Bitmap bitmap) {
        this.bitmapImageAvatar.setValue(bitmap);
    }
    public LiveData<Bitmap> getBitmapLiveData() {
        return this.bitmapImageAvatar;
    }
    public void setUser(User user) {
        this.user.setValue(user);
    }
    public LiveData<User> getUser() {
        return user;
    }
    public void setPartner(Partner partner) {
        this.partner.setValue(partner);
    }
    public LiveData<Partner> getPartner(){
        return this.partner;
    }
    @Override
    public String toString() {
        return "ProfileViewModel{" +
                "user=" + user +
                ", partner=" + partner +
                '}';
    }
}
