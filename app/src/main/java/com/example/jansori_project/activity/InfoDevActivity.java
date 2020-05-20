package com.example.jansori_project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jansori_project.R;

public class InfoDevActivity extends AppCompatActivity {

    ImageView ivBack;
    LinearLayout goGit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_dev);

        ivBack = findViewById(R.id.devBack);
        goGit = findViewById(R.id.goGit);

        ivBack.setOnClickListener(v -> finish());

        goGit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://github.com/peeinnkim");
            intent.setData(uri);
            startActivity(intent);
        });

    }//onCreate
}//InfoDevActivity
