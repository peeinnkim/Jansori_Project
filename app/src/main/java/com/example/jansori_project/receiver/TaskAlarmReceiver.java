package com.example.jansori_project.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.jansori_project.Func.AlarmControl;
import com.example.jansori_project.R;
import com.example.jansori_project.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskAlarmReceiver extends BroadcastReceiver {
    Context context;
    int REQUEST_CODE = 0;
    int[] daysArr;
    String title;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Noti ->안드로이드 상태바에 메세지를 던지기위한 서비스 불러오기
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        REQUEST_CODE = intent.getIntExtra("reqCode", 0);
        daysArr = intent.getIntArrayExtra("daysArr");
        title = intent.getStringExtra("taskTitle");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();

        if (daysArr[calendar.get(Calendar.DAY_OF_WEEK)-1] == 0) { //설정한 요일이 아니면 다음날로 알람 넘기기
            // 내일 같은 시간으로 알람시간 결정(확인용임)
            calendar.add(Calendar.DATE, 1);

        } else {
            //설정한 요일일때 노티 빌드
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

            //OREO API 26 이상에서는 채널 필요
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder.setSmallIcon(R.drawable.ic_jansori); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                String channelName = "매일 알람";
                String description = "매일 울리는 알람";
                int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

                NotificationChannel channel = new NotificationChannel("default", channelName, importance);
                channel.setDescription(description);

                if (notificationManager != null) {
                    // 노티피케이션 채널을 시스템에 등록
                    notificationManager.createNotificationChannel(channel);
                }

            } else {
                builder.setSmallIcon(R.mipmap.ic_logo_round); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
            }

            //노티 셋팅
            builder.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("잔소리 타임")
                    .setContentTitle(title)
                    .setContentText("얼른하자으ㅏ아아아")
                    .setContentInfo("잔소리")
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            if (notificationManager != null) {
                //화면 꺼짐 상태에서 화면 켜기
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "My:Tag");
                wakeLock.acquire(5000);

                // 노티피케이션 동작시킴
                notificationManager.notify(1234, builder.build());

                //시간 체크
                Date dt = new Date();
                SimpleDateFormat full_sdf = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                Log.d("TIMETEST[일반]", "시간: " + sdf.format(dt));

                // 내일 같은 시간으로 알람시간 결정
                calendar.add(Calendar.DATE, 1);

            }

        } //요일설정 if

        //  Preference에 설정한 값 저장
        SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", Context.MODE_PRIVATE).edit();
        editor.putLong("nextNotifyTime", calendar.getTimeInMillis());
        editor.apply();

//        Date currentDateTime = calendar.getTime();
//        String date_text = new SimpleDateFormat("yyyy-MM-dd EE요일 a hh:mm ", Locale.getDefault()).format(currentDateTime);
//        Toast.makeText(context.getApplicationContext(), "[설정확인용] 다음 알람:  " + date_text , Toast.LENGTH_SHORT).show();

    }//onReceive

}//TaskAlarmReceiver