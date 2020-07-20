package com.example.fooddonationapplication.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

public class UpdateEventViewModel extends ViewModel {
    private Bitmap imageBitmap;
    private boolean hasImageChanged;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public boolean isHasImageChanged() {
        return hasImageChanged;
    }

    public void setHasImageChanged(boolean hasImageChanged) {
        this.hasImageChanged = hasImageChanged;
    }
}
