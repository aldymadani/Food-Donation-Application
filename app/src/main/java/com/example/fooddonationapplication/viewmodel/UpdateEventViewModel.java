package com.example.fooddonationapplication.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

public class UpdateEventViewModel extends ViewModel {
    private Bitmap imageBitmap;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
