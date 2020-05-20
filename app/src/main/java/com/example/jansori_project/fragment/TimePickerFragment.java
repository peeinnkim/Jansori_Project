package com.example.jansori_project.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.jansori_project.R;

import java.text.DateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    Calendar calendar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // 캘린더 객체화
        calendar = Calendar.getInstance();
        // 현재 시, 분 설정
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog tp = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,this,hour,minute,false);

        return tp;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        Calendar calSet =  (Calendar) calendar.clone();

        //피커에서 설정한 시간 설정
        calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calSet.set(Calendar.MINUTE, minute);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if(calSet.compareTo(calendar) <= 0) {
            //설정한 시간이 현재 시간보다 이전이면 설정 시간에 하루 플러스
            calSet.add(Calendar.DATE, 1);
        }

        //시간 보이기
        TextView taskAlarm = getActivity().findViewById(R.id.addAlarm);
        if(minute < 10) { // 분이 일의 자리일 때 0 붙이기
            taskAlarm.setText(hourOfDay + " : 0" + minute);
        } else {
            taskAlarm.setText(hourOfDay + " : " + minute);
        }


    }//onTimeSet

}//TimePickerFragment
