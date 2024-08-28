package com.example.halalcheck3.model;

import java.util.List;

import java.util.List;

public class MenuCategory {

    private String categoryName;
    private List<MenuItem> menuItems;

    // No-argument constructor required by Firebase
    public MenuCategory() {
        // Default constructor required for calls to DataSnapshot.getValue(MenuCategory.class)
    }

    public MenuCategory(String categoryName, List<MenuItem> menuItems) {
        this.categoryName = categoryName;
        this.menuItems = menuItems;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
