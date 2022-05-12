package com.learn.e_shop.Threads;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimeThread extends Thread{

    private String time = "no time";
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss a");
        time = simpleDateFormat.format(calendar.getTime());
    }

    public String getTime() {
        run();
        return time;
    }
}
