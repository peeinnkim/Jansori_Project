package com.example.jansori_project.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jansori_project.Func.BackPressCloseControl;
import com.example.jansori_project.Func.UIActionControl;
import com.example.jansori_project.R;
import com.example.jansori_project.adapter.TaskRecyclerAdapter;
import com.example.jansori_project.database.TaskDBOpenHelper;
import com.example.jansori_project.dto.Task;
import com.example.jansori_project.Func.AlarmControl;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    View drawerView;
    TextView tvToday, tvEmpty, tvPfName;
    ImageView ivUserInfo, ivAddTask, ivPfIcon;
    RecyclerView taskListView;
    BackPressCloseControl backHandler;

    Intent intent;
    private static final String TAG = "MainActivity";
    private TaskDBOpenHelper helper;
    private Cursor cursor;
    private Task task;
    private ArrayList<Task> taskList;
    private TaskRecyclerAdapter taskRecyclerAdapter;

    public static final int MSG_CLICK_ENABLED = 0;
    public static final int MSG_CLICK_DISABLED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.mainBg);
        drawerView = findViewById(R.id.drawer);
        ivUserInfo = findViewById(R.id.ivMenu);
        ivAddTask = findViewById(R.id.ivAdd);
        ivPfIcon = findViewById(R.id.pfIcon);
        tvPfName = findViewById(R.id.pfName);

        //뒤로가기 버튼 핸들러
        backHandler = new BackPressCloseControl(this);

        //오늘 날짜 설정하기
        tvToday = findViewById(R.id.tvToday);
        SimpleDateFormat df = new SimpleDateFormat("MM월 dd일");
        String date = df.format(Calendar.getInstance().getTime());
        tvToday.setText(date);

        //메뉴버튼 누르면 drawer로 회원정보 탭 열림
        ivUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        //리스트에 DB정보 집어넣기
        taskList = new ArrayList<>();
        putDBDatasOnList();

        //리스트뷰 연결
        taskListView = findViewById(R.id.taskListView);
        taskRecyclerAdapter = new TaskRecyclerAdapter(this, taskList, mainHandler);
        taskListView.setLayoutManager(new LinearLayoutManager(this));
        taskListView.setAdapter(taskRecyclerAdapter);

        //리스트뷰가 비었을때
        tvEmpty = findViewById(R.id.emptyText);
        IsListEmpty(taskList);

        //추가화면 전환
        ivAddTask.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), TaskAddActivity.class);
            startActivityForResult(intent, 303);
            overridePendingTransition(R.anim.slide_up, R.anim.stay);
        });

        DeleteAndUndo();

    }//onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode) {
            case 303: //태스크 추가부분 requestCode == 303
                if(resultCode == RESULT_OK) {
                    //마지막으로 추가된 아이디 받아서 태스크 객체 받아오기
                    long id = data.getLongExtra("lastID", 1);
                    task = selectTaskById(id);
                    Toast.makeText(this, data.getStringExtra("taskAddResult"), Toast.LENGTH_SHORT).show();

                    //알람 설정
                    AlarmControl.setAlarm(this, task);
                }
            break;

            case 202: //프로필 수정 requestCode == 202
                if(resultCode == RESULT_OK) {
                    String upName = data.getStringExtra("upName");
                    tvPfName.setText(upName);

                    Toast.makeText(this, data.getStringExtra("infoUpdateResult"), Toast.LENGTH_SHORT).show();
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);

    }//onActivityResult

    @Override
    protected void onResume() {
        super.onResume();

        putDBDatasOnList();
        taskRecyclerAdapter.upDateItemList(taskList);
        IsListEmpty(taskList);
    }//onResume

    @Override public void onBackPressed() {
        backHandler.onBackPressed();
    }//onBackPressed

    /** 리스트에 DB정보 집어넣기*/
    public void putDBDatasOnList(){
        helper = new TaskDBOpenHelper(this);
        helper.open();
        taskList.clear();
        cursor = helper.selectAll();

        while(cursor.moveToNext()) {
            task = new Task(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7));
            taskList.add(task);
        }

        cursor.close();
        helper.close();
    }

    /** 아이디로 태스크 찾기*/
    public Task selectTaskById(long no){
        helper = new TaskDBOpenHelper(this);
        helper.open();
        cursor = helper.selectTaskById(no);
        task = new Task(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            cursor.getInt(7));
        cursor.close();
        helper.close();

        return task;
    }

    /**프로필 메뉴*/
    public void pfOnClick(View view) {
        Intent pfIntent;

        switch(view.getId()) {
            case R.id.pfMenuUpdate: //정보수정
                pfIntent = new Intent(this, InfoUpdateActivity.class);
                pfIntent.putExtra("curName", tvPfName.getText().toString());
                startActivityForResult(pfIntent, 202);

                break;

            case R.id.pfMenuSetting:
                break;

            case R.id.pfMenuFeedBack: //피드백 보내기
                Uri uri = Uri.parse("mailto:peeinnkim@gmail.com");
                pfIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(pfIntent);

                break;

            case R.id.pfMenuReview:
                break;

            case R.id.pfMenuIntroduce:
                pfIntent = new Intent(this, InfoDevActivity.class);
                startActivity(pfIntent);

                break;

        }//switch

    }//pfOnClick

    /**활성화 상태 변경*/
    public void enabledChange(int no){
        long longNo = no;
        Task ebtask = selectTaskById(longNo);

        helper = new TaskDBOpenHelper(this);
        helper.open();

        switch(ebtask.getEnabled()) {
            case 0:
                AlarmControl.setAlarm(this, ebtask);
                break;
            case 1:
                AlarmControl.cancelAlarm(this, ebtask);
                break;
        }

        helper.updateEnabled(longNo, ebtask.getEnabled());

        cursor.close();
        helper.close();
    }

    public void DeleteAndUndo() {
        UIActionControl swipeToDeleteCallback = new UIActionControl(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Task selTask = taskRecyclerAdapter.getList().get(position);
                taskRecyclerAdapter.removeItem(position);

                helper = new TaskDBOpenHelper(MainActivity.this);
                helper.open();

                Snackbar snackbar = Snackbar.make(drawerLayout, "삭제되었습니다.", Snackbar.LENGTH_LONG);
                snackbar.setAction("실행취소", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        taskRecyclerAdapter.restoreItem(selTask, position);
                        taskListView.scrollToPosition(position);

                        helper = new TaskDBOpenHelper(MainActivity.this);
                        helper.open();
                        helper.restoreColumn(selTask);
                        helper.close();

                        AlarmControl.setAlarm(MainActivity.this, selTask);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

                helper.deleteColumn(selTask.getNo());
                helper.close();

                AlarmControl.cancelAlarm(MainActivity.this, selTask);
                IsListEmpty(taskList);
            }

        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(taskListView);
    }

    public void IsListEmpty(List<Task> taskList){
        if(taskList.size() == 0){
            taskListView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }else{
            tvEmpty.setVisibility(View.GONE);
            taskListView.setVisibility(View.VISIBLE);
        }
    }

    private Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            int msgId = msg.arg1;
            enabledChange(msgId);

            onResume();

            switch(msg.what) {
                case MSG_CLICK_ENABLED:
                    Toast.makeText(getApplicationContext(), "알람 OFF", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_CLICK_DISABLED:
                    Toast.makeText(getApplicationContext(), "알람 ON", Toast.LENGTH_SHORT).show();
                    break;
            }

        }//handleMessage

    };//mainHandler

}//MainActivity