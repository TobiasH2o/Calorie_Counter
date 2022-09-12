package com.example.caloriecounter;

import java.time.LocalDateTime;

public class SavedProduct {

    private LocalDateTime day;
    private int portionSize;
    private Product product;

    public SavedProduct(LocalDateTime day, int portionSize, Product product){
        this.product = product;
        this.portionSize = portionSize;
        this.day = day;
    }

    public int getPortionSize() {
        return portionSize;
    }

    public void setPortionSize(int portionSize) {
        this.portionSize = portionSize;
    }

    public LocalDateTime getDay() {
        return day;
    }

    public void setDay(LocalDateTime day) {
        this.day = day;
    }
}
