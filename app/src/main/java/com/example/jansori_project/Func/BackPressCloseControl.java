package com.example.jansori_project.Func;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseControl {
    private long backKeyClickTime = 0;
    private Activity activity;

    public BackPressCloseControl(Activity activity) {
        this.activity = activity;
    }//constructor

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyClickTime + 2000) {
            backKeyClickTime = System.currentTimeMillis();
            showToast();
            return; }

        if (System.currentTimeMillis() <= backKeyClickTime + 2000) {
            activity.finish();
        }
    }//onBackPressed

    public void showToast() {
        Toast.makeText(activity, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
    }//showToast

}//BackPressCloseControl
