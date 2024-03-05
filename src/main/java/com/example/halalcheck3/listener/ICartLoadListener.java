package com.example.halalcheck3.listener;

import com.example.halalcheck3.model.CartModel;
import com.example.halalcheck3.model.MenuItem;

import java.util.List;

public interface ICartLoadListener {

    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);

   // void OnCartLoadFailed(String message);
}
