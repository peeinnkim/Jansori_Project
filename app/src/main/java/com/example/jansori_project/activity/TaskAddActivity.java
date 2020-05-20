package com.example.jansori_project.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jansori_project.R;
import com.example.jansori_project.database.TaskDBOpenHelper;
import com.example.jansori_project.dto.Task;
import com.example.jansori_project.fragment.TimePickerFragment;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.colormode.ColorMode;

public class TaskAddActivity extends AppCompatActivity {

    ImageView ivCancel, ivColorPicker;
    EditText taskTitle;
    TextView taskAlarm, taskMode;
    Button btnAdd;
    RelativeLayout taskTitleBg;

    int mode = 0; // 잔소리모드 여부
    String[] isClicked; //요일 선택여부
    String daysOfWeek = ""; //요일 선택여부 DB에 넣을 값
    int hour; //시간-시
    int minute; //시간-분
    String title; //태스크 제목
    int color = 0xff78aac3; //태스크 색깔
    int enabled; //태스크 활성화여부 (0:비활성화 1:활성화)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        ivCancel = findViewById(R.id.ivCancel);
        taskTitle = findViewById(R.id.addTitle);
        ivColorPicker = findViewById(R.id.ivPickColor);
        taskAlarm = findViewById(R.id.addAlarm);
        taskMode = findViewById(R.id.addMode);
        btnAdd = findViewById(R.id.btnTaskAdd);
        taskTitleBg = findViewById(R.id.addTitleBg);

        //팝업창 크기 셋팅
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.98); //Display 사이즈의 70%
        int height = (int) (display.getHeight() * 0.95);  //Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        //취소 아이콘 -> X버튼 누르면 종료
        ivCancel.setOnClickListener(v -> {
            Intent cancelIntent = new Intent();
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_down);
        });

        //컬러픽커 아이콘 -> 누르면 컬러픽커
        ivColorPicker.setOnClickListener(v -> new ColorOMaticDialog.Builder()
                .initialColor(color)
                .colorMode(ColorMode.RGB)
                .indicatorMode(IndicatorMode.DECIMAL)
                .onColorSelected(i -> {
                    color = i;
                    taskTitleBg.setBackgroundDrawable(GetDrawable(R.drawable.rounded_square, color, "src"));
                    ivColorPicker.setColorFilter(color);
                })
                .showColorIndicator(true)
                .create()
                .show(getSupportFragmentManager(), "Pick a Color"));


        //모드 설정
        taskMode.setOnClickListener(v -> {
            if(mode == 0) {
                taskMode.setText("잔소리 모드");
                mode = 1;
            } else {
                taskMode.setText("일반 모드");
                mode = 0;
            }
        });

        //알람 시간
        taskAlarm.setOnClickListener(v -> {
            DialogFragment dFragment = new TimePickerFragment();
            dFragment.show(getFragmentManager(),"Time Setting");
        });

        //요일 버튼
        Button[] btnArr = new Button[7];
        int[] sBtnArr = {R.id.btnSun, R.id.btnMon, R.id.btnTue, R.id.btnWed, R.id.btnThu, R.id.btnFri, R.id.btnSat};
        isClicked = new String[]{"0", "0", "0", "0", "0", "0", "0"};

        for(int i=0; i<btnArr.length; i++) {
            final int index = i;

            btnArr[index] = findViewById(sBtnArr[index]);
            btnArr[index].setOnClickListener(v -> {
                StateListDrawable state = new StateListDrawable();

                if(isClicked[index].equals("0")) {
                    btnArr[index].setBackgroundDrawable(GetDrawable(R.drawable.rounded_btn, Color.parseColor("#BDBDBD"), "multi"));
                    btnArr[index].setTextColor(Color.parseColor("#ffffff"));
                    isClicked[index] = "1";

                } else {
                    btnArr[index].setBackgroundDrawable(GetDrawable(R.drawable.rounded_btn, Color.parseColor("#FFFFFF"), "multi"));
                    btnArr[index].setTextColor(Color.parseColor("#747474"));
                    isClicked[index] = "0";
                }
            });
        }


        //저장버튼
        //onClick
        btnAdd.setOnClickListener(v -> {
            //제목 집어넣기
            title = taskTitle.getText().toString().trim();

            //제목 유효성 검사
            if(title.equals("")) {
                Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            //요일
            daysOfWeek = "";
            for(String c : isClicked) {
                daysOfWeek += c;
            }

            if(daysOfWeek.equals("0000000")) {
                Toast.makeText(getApplicationContext(), "요일을 선택하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            //모드 & 시간 - 잔소리 모드가 설정되었으면 시간은 0으로 들어가게
            String time = taskAlarm.getText().toString();
            hour = Integer.parseInt(time.split(" : ")[0]);
            minute = Integer.parseInt(time.split(" : ")[1]);

            //활성화여부 -> 등록되자마자 무조건 활성화로
            enabled = 1;

            //DB에 값 집어넣기
            TaskDBOpenHelper helper = new TaskDBOpenHelper(TaskAddActivity.this);
            helper.open();
            Task newTask = new Task(0, title, color, daysOfWeek, mode, hour, minute, enabled);
            long resultIdx = helper.insertColumn(newTask); //Last Insert id
            helper.close();

            //종료
            Intent resultIntent = getIntent();
            resultIntent.putExtra("lastID", resultIdx);
            resultIntent.putExtra("taskAddResult", "추가되었습니다.");
            setResult(RESULT_OK, resultIntent);
            finish();

        });//btnAdd.clickListener



    }//onCreate

    //Drawable 색 바꾸는 메소드
    public Drawable GetDrawable(int drawableResId, int color, String mode) {
        Drawable drawable =  getResources().getDrawable(drawableResId);
        switch(mode) {
            case "multi":
                drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                break;
            case "in":
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                break;
            case "out":
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_OUT);
                break;
            case "src":
                drawable.setColorFilter(color, PorterDuff.Mode.SRC);
                break;
        }

        return drawable;
    }//GetDrawable

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }//onBackPressed

    @Override

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}//TaskAddActivity
