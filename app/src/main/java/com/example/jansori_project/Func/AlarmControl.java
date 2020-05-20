package com.example.jansori_project.Func;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.jansori_project.dto.Task;
import com.example.jansori_project.receiver.DeviceBootReceiver;
import com.example.jansori_project.receiver.TaskAlarmReceiver;
import com.example.jansori_project.receiver.TaskJansoriAlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmControl {
    private Activity activity;

    public AlarmControl(Activity activity) {
        this.activity = activity;
    }//constructor

    /** 알람 설정*/
    public static void setAlarm (Context context, Task t){
        AlarmManager am = null;
        Intent intent = null;
        PendingIntent pendingIntent = null;
        PackageManager pm = null;
        ComponentName receiver = null;
        Calendar calendar = null;
        SharedPreferences.Editor editor = null;

        final int REQUEST_CODE = t.getNo();

        //날짜 조각조각 땃땃따
        int[] daysArr = new int[7];
        String dd = "";
        for(int i=0; i< t.getDaysOfWeek().length(); i++) {
            if(i == t.getDaysOfWeek().length()) {
                dd = t.getDaysOfWeek().substring(i);
            } else {
                dd = t.getDaysOfWeek().substring(i, i+1);
            }
            daysArr[i] = Integer.parseInt(dd);
        }

        switch(t.getMode()) {
            case 0: //일반모드
                am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                intent = new Intent(context, TaskAlarmReceiver.class);
                intent.putExtra("reqCode" ,REQUEST_CODE);
                intent.putExtra("daysArr" ,daysArr);
                intent.putExtra("taskTitle", t.getTitle());

                pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                pm = context.getPackageManager();
                receiver = new ComponentName(context, DeviceBootReceiver.class);

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, t.getHour());
                calendar.set(Calendar.MINUTE, t.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                //이미 지난 날을 설정하면 date에 +1 해줌
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                //  Preference에 설정한 값 저장
                editor = context.getSharedPreferences("daily alarm", context.MODE_PRIVATE).edit();
                editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                editor.apply();

                if (am != null) {
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                    //버전에 따라 다르게 작업해줌
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    } else if(Build.VERSION.SDK_INT >= 19) {
//                        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    } else {
//                        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    }

                    //시간체크
                    Date dt = new Date();
                    SimpleDateFormat full_sdf = new SimpleDateFormat("hh:mm:ss");
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    Log.d("TIMETEST[일반 설정완료]", "시간: " + sdf.format(dt));

                    //재부팅되도 알람 살아있게하기
                    pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                }

                break;

            case 1: //잔소리모드 (현재는 알람 시작시간만 설정되어있음)
                am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                intent = new Intent(context, TaskJansoriAlarmReceiver.class);
                intent.putExtra("reqCode" ,REQUEST_CODE);
                intent.putExtra("daysArr" ,daysArr);
                intent.putExtra("taskTitle", t.getTitle());

                pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                pm = context.getPackageManager();
                receiver = new ComponentName(context, DeviceBootReceiver.class);

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                //  Preference에 설정한 값 저장
                editor = context.getSharedPreferences("jansori_alarm", context.MODE_PRIVATE).edit();
                editor.putLong("nextJansoriTime", (long)calendar.getTimeInMillis());
                editor.apply();

                if (am != null) {
                    int rdStart = (int) (Math.random() * 60)+1; //1~60
                    int rdInterval = (int) (Math.random() * 87)+5; //5~87분 사이

                    intent.putExtra("rdInterval", rdInterval);

                    am.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis() + (rdStart * 60 * 1000), //설정한 시간부터 한시간 이내에 실행 (랜덤)
                            rdInterval * 60 * 1000, //알람이 울리고 5분 ~ 한시간 23분 이내에 실행 (랜덤)
                                    pendingIntent); //최소 INTEVAL TIME -> 60000ms

                    //버전에 따라 다르게 작업해줌
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (rdStart * 60 * 1000), pendingIntent);
//                    } else if(Build.VERSION.SDK_INT >= 19) {
//                        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (rdStart * 60 * 1000), pendingIntent);
//                    } else {
//                        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (rdStart * 60 * 1000), pendingIntent);
//                    }

                    //재부팅되도 알람 살아있게하기
                    pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                    //시간체크
                    Date dt = new Date();
                    SimpleDateFormat full_sdf = new SimpleDateFormat("hh:mm:ss");
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    Log.d("TIMETEST[잔소리 설정완료]", "시간: " + sdf.format(dt));

                }

                break;
        }


    }//setAlarm

    /** 알람 삭제(일반)*/
    public static void cancelAlarm (Context context, Task task){
        final int REQUEST_CODE = (int) task.getNo();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = null;

        switch(task.getMode()) {
            case 0:
                intent = new Intent(context, TaskAlarmReceiver.class);
                break;
            case 1:
                intent = new Intent(context, TaskJansoriAlarmReceiver.class);
                break;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (pendingIntent != null && am != null) {
            am.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }//cancelAlarm


}//AlarmControl
