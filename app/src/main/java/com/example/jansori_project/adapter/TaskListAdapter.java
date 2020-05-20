package com.example.jansori_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jansori_project.R;
import com.example.jansori_project.dto.Task;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {
    Context context;
    List<Task> list;

    public TaskListAdapter(Context context, List<Task> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }//getCount

    @Override
    public Task getItem(int position) {
        return list.get(position);
    }//getItem

    @Override
    public long getItemId(int position) {
        return position;
    }//getItemId

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int idx = position;
        final Task t = list.get(position);
        taskHolder holder = null;
        TextView taskTitle = null;
        TextView taskDate = null;
        TextView taskMode = null;


        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, parent, false);

            taskTitle = convertView.findViewById(R.id.taskTitle);
            taskDate = convertView.findViewById(R.id.taskDate);
            taskMode = convertView.findViewById(R.id.taskMode);

            // 홀더 생성 및 Tag로 등록
            holder = new taskHolder();
            holder.taskTitle = taskTitle;
            holder.taskDate = taskDate;
            holder.taskMode = taskMode;
            convertView.setTag(holder);

        } else {
            holder  = (taskHolder) convertView.getTag();
            taskTitle = holder.taskTitle;
            taskDate = holder.taskDate;
            taskMode = holder.taskMode;
        }

        int mode = t.getMode();
        int color = t.getColor();

        //일정 색 바꾸기
        Drawable draw =  convertView.getResources().getDrawable(R.drawable.rounded_square);
        draw.setColorFilter(color, PorterDuff.Mode.SRC);
        convertView.setBackgroundDrawable(draw);

        //일정 제목 집어넣기
        taskTitle.setText(t.getTitle());

        //요일 설정
        String dateInfo = t.getDaysOfWeek();
        String[] dateArr = new String[]{"월", "화", "수", "목", "금", "토", "일"};
        String dateSaver = "";

        for(int i=0; i< dateInfo.length(); i++) {
            if(dateInfo.charAt(i) == '1') {
                dateSaver += dateArr[i]+" ";
            }
        }

        //모드 설정
        switch(mode) {
            case 0:
                taskDate.setText(dateSaver);
                taskMode.setText(t.getHour()+":"+t.getMinute());

                //시간 보이기
                int hour = t.getHour();
                int minute = t.getMinute();

                if(minute < 10) { // 분이 일의 자리일 때 0 붙이기
                    taskMode.setText(hour + " : 0" + minute);
                } else {
                    taskMode.setText(hour + " : " + minute);
                }
                break;

            case 1:
                taskDate.setText(dateSaver);
                taskMode.setText("잔소리 모드");

                break;
        }

        convertView.setOnLongClickListener(v -> {
            AlertDialog.Builder dlg = new AlertDialog.Builder(context);
            dlg.setTitle("일정 삭제");
            dlg.setMessage("알람을 삭제하시겠습니까?");
            dlg.setPositiveButton("삭제", (dialog, which) -> {
                list.remove(idx);
                TaskListAdapter.this.notifyDataSetChanged();

            });
            dlg.setNegativeButton("취소", null);
            dlg.show();

            return false;
        });//convertView.longClickListener

        return convertView;
    }//getView

    public void setList(List<Task> list){
        this.list = list;
    }

    public List<Task> getList(){
        return list;
    }

    private class taskHolder {
        TextView taskTitle;
        TextView taskDate;
        TextView taskMode;
    }//CustomHolder

    public void upDateItemList(List<Task> list) {
        this.list = list;
        notifyDataSetChanged();
    }


}//TaskListAdapter
