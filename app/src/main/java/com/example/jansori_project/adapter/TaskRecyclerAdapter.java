package com.example.jansori_project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jansori_project.R;
import com.example.jansori_project.activity.MainActivity;
import com.example.jansori_project.activity.TaskUpdateActivity;
import com.example.jansori_project.dto.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder> {
    Context context;
    Activity activity;
    List<Task> list;
    Handler handler;
    View viewview;

    public TaskRecyclerAdapter(Activity activity, List<Task> list, Handler handler) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.list = list;
        this.handler = handler;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout taskBg;
        LinearLayout disableBg;
        TextView taskTitle;
        TextView taskDate;
        TextView taskMode;
        TextView taskId;
        ImageView taskIcon;

        public TaskViewHolder(View itemView) {
            super(itemView);

            viewview = itemView;
            taskBg = itemView.findViewById(R.id.task_item_bg);
            disableBg = itemView.findViewById(R.id.disableLayout);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskMode = itemView.findViewById(R.id.taskMode);
            taskId = itemView.findViewById(R.id.taskId);
            taskIcon = itemView.findViewById(R.id.taskIcon);
        }
    }//MyViewHolder

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);

        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task t = list.get(position);

        holder.taskTitle.setText(t.getTitle());
        int mode = t.getMode();
        int color = t.getColor();
        final int no = t.getNo();

        //아이디 집어넣기
        holder.taskId.setText(no+"");

        //일정 색 바꾸기
        Drawable draw =  holder.taskBg.getResources().getDrawable(R.drawable.rounded_square);
        draw.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        holder.taskBg.setBackgroundDrawable(draw);


        //일정 제목 집어넣기
        holder.taskTitle.setText(t.getTitle());

        //활성화 상태에 따라 아이콘 바꾸기
        holder.itemView.setClickable(true);
        holder.disableBg.setVisibility(View.GONE);
        switch(t.getEnabled()) {
            case 0:
                holder.taskIcon.setImageResource(R.drawable.new_sleep);
                holder.itemView.setClickable(false);
                holder.disableBg.setVisibility(View.VISIBLE);
                break;

            case 1:
                holder.taskIcon.setImageResource(R.drawable.new_activate);
                holder.itemView.setClickable(true);
                break;
        }

        //요일 설정
        String dateInfo = t.getDaysOfWeek();
        String[] dateArr = new String[]{"일", "월", "화", "수", "목", "금", "토"};
        String dateSaver = "";

        //날짜 조금 깔끔하게 바꾸기
        switch(dateInfo) {
            case "1000001":
                dateSaver = "주말";
                break;
            case "0111110":
                dateSaver = "주중";
                break;
            case "1111111":
                dateSaver = "매일";
                break;
            default:
                for(int i=0; i< dateInfo.length(); i++) {
                    if(dateInfo.charAt(i) == '1') {
                        dateSaver += dateArr[i]+" ";
                    }
                }
                break;
        }

        //모드 설정
        switch(mode) {
            case 0:
                holder.taskDate.setText(dateSaver);
                holder.taskMode.setText(t.getHour()+":"+t.getMinute());

                //시간 보이기
                int hour = t.getHour();
                int minute = t.getMinute();

                if(minute < 10) { // 분이 일의 자리일 때 0 붙이기
                    holder.taskMode.setText(hour + " : 0" + minute);
                } else {
                    holder.taskMode.setText(hour + " : " + minute);
                }
                break;

            case 1:
                holder.taskDate.setText(dateSaver);
                holder.taskMode.setText("잔소리 모드");

                break;
        }

        //수정화면
        holder.taskBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t.getEnabled() == 1) {
                    Intent intent = new Intent(activity, TaskUpdateActivity.class);
                    Task upTask = list.get(position);
                    intent.putExtra("upTask", upTask);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else {
                    Toast.makeText(context, "비활성화 상태입니다", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //아이콘 클릭시
        holder.taskIcon.findViewById(R.id.taskIcon);
        holder.taskIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable currImg = holder.taskIcon.getDrawable();
                Drawable imgActivate = viewview.getResources().getDrawable(R.drawable.new_activate);
                Drawable imgSleep = viewview.getResources().getDrawable(R.drawable.new_sleep);

                Bitmap currImgBit = ((BitmapDrawable)currImg).getBitmap();
                Bitmap imgActivateBit = ((BitmapDrawable)imgActivate).getBitmap();
                Bitmap imgSleepBit = ((BitmapDrawable)imgSleep).getBitmap();

                Message msg = new Message();

                if(currImgBit.equals(imgActivateBit)) { //현재: 활성화 상태
                    holder.taskIcon.setImageResource(R.drawable.new_sleep);
                    holder.itemView.setClickable(false);
                    holder.disableBg.setVisibility(View.VISIBLE);

                    msg.what = MainActivity.MSG_CLICK_ENABLED;
                    msg.arg1 = list.get(position).getNo();

                }else if(currImgBit.equals(imgSleepBit)){ //현재: 비활성화 상태
                    holder.taskIcon.setImageResource(R.drawable.new_activate);
                    holder.itemView.setClickable(true);
                    holder.disableBg.setVisibility(View.GONE);

                    msg.what = MainActivity.MSG_CLICK_DISABLED;
                    msg.arg1 = list.get(position).getNo();
                }

                handler.sendMessage(msg);

            }//onClick
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }//removeItem

    public void restoreItem(Task task, int position) {
        list.add(position, task);
        notifyItemInserted(position);
    }

    public long getItemId (int position) {
        return list.get(position).getNo();
    }

    public void setList(List<Task> list){
        this.list = list;
    }

    public List<Task> getList(){
        return list;
    }

    public void upDateItemList(List<Task> list) {
        this.list = list;
        notifyDataSetChanged();
    }





}//TaskRecycleAdapter