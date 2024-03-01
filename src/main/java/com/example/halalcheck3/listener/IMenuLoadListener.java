package com.example.halalcheck3.listener;
import com.example.halalcheck3.model.MenuItem;
import java.util.List;
public interface IMenuLoadListener {
    void onMenuLoadSuccess(List<MenuItem> menuItemList);
    void onMenuLoadFailed(String message);


}
