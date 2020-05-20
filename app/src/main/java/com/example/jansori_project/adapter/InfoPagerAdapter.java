package com.example.jansori_project.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class InfoPagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }//getCount

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }//isViewFromObject
}//InfoPagerAdapter
