package com.example.fooddonationapplication.util;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        Window window = activity.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static String convertToFullDate(String date) {
        Calendar calendar = Calendar.getInstance();
        Date oldDate;
        SimpleDateFormat srcDf = new SimpleDateFormat("dd/MM/yyyy", new Locale("en", "EN"));
        SimpleDateFormat dstDf = new SimpleDateFormat("EEEE, MMMM d, yyyy", new Locale("en", "EN"));

        // parse the date string into Date object
        try {
            oldDate = srcDf.parse(date);
            if (oldDate != null) {
                return dstDf.format(oldDate.getTime());
            } else {
                return date;
            }
        } catch (ParseException e) {
            return date;
        }
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }
}
