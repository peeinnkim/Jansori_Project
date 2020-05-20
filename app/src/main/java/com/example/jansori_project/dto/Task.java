package com.example.jansori_project.dto;

import java.io.Serializable;

public class Task implements Serializable {
    private int no; //알람 번호
    private String title; //알람 제목
    private int color; //색깔
    private String daysOfWeek; //요일
    private int mode; // 모드 (0:일반 1:잔소리)
    private int hour; //시
    private int minute; //분
    private int enabled; //활성화여부 (0:비활성화 1:활성화)

    public Task(int no, String title, int color, String daysOfWeek, int mode, int hour, int minute, int enabled) {
        this.no = no;
        this.title = title;
        this.color = color;
        this.daysOfWeek = daysOfWeek;
        this.mode = mode;
        this.hour = hour;
        this.minute = minute;
        this.enabled = enabled;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Task{" +
                "no=" + no +
                ", title='" + title + '\'' +
                ", color=" + color +
                ", daysOfWeek='" + daysOfWeek + '\'' +
                ", mode=" + mode +
                ", hour=" + hour +
                ", minute=" + minute +
                ", enabled=" + enabled +
                '}';
    }
}//Task
