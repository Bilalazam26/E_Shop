package com.learn.e_shop.Threads;


import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateThread extends Thread{
    private String date = "no date";

    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        date = simpleDateFormat.format(calendar.getTime());
    }

    public String getDate() {
        run();
        return date;
    }
}
