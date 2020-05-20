package com.example.jansori_project.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.jansori_project.activity.MainActivity;
import com.example.jansori_project.database.TaskDBOpenHelper;
import com.example.jansori_project.dto.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            TaskDBOpenHelper helper = new TaskDBOpenHelper(context);
            Cursor cursor = helper.selectEnabled();

            while(cursor.moveToNext()) {
                Task tt = new Task(cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getInt(2),
                                cursor.getString(3),
                                cursor.getInt(4),
                                cursor.getInt(5),
                                cursor.getInt(6),
                                cursor.getInt(7));

                // 디바이스가 부팅되고 알람 리셋
                Intent alarmIntent = new Intent(context, TaskAlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tt.getNo(), alarmIntent, 0);

                //시스템 알람서비스 가져옴
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                //다음 알람시간 저장
                SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm", Context.MODE_PRIVATE);
                long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

                Calendar current_calendar = Calendar.getInstance();
                Calendar nextNotifyTime = new GregorianCalendar();
                nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis));

                //현재 날짜, 시간 정보와 비교해서 현재보다 전으로 설정되어 있으면 하루 추가
                if (current_calendar.after(nextNotifyTime)) {
                    nextNotifyTime.add(Calendar.DATE, 1);
                }

                Date currentDateTime = nextNotifyTime.getTime();
                String date_text = new SimpleDateFormat("yyyy-MM-dd EE요일 a hh:mm ", Locale.getDefault()).format(currentDateTime);
                Toast.makeText(context.getApplicationContext(),"[재부팅 후 확인] 다음 알람:  " + date_text, Toast.LENGTH_SHORT).show();

                if (am != null) {
                    am.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                }

            }//while

            helper.close();

        }//if
    }//onReceive
}//DeviceBootReceiver
