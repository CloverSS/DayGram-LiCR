package com.example.activitytest.daygram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class showactivity extends Activity {
    int[] daynum_month={31,29,31,30,31,30,31,31,30,31,30,31};
    private    ListView   showListView ;
    private showDiaryAdapter showadpter;
    Button button_main;
    ArrayList<Diary> showdata = new ArrayList<Diary>(); // 当月数据
    String nameFile;
    int YearNow;
    int MonthNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show);
        Intent intent = getIntent();
        MonthNow = intent.getIntExtra("Month",-1);
        YearNow = intent.getIntExtra("Year",-1);
        tminit(MonthNow,YearNow);


        //listview 显示当月日记
        showListView = (ListView) findViewById(R.id.show_view);
        showadpter = new showDiaryAdapter(this, showdata);
        showListView.setAdapter(showadpter); //为ListView设置适配器

        //spinner监听
        final Spinner spinnerM = (Spinner) findViewById(R.id.spinner_month);
        final Spinner spinnerY = (Spinner) findViewById(R.id.spinner_year);
        spinnerM.setSelection(MonthNow - 1, true);
        spinnerM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                MonthNow = pos + 1;
                tminit(MonthNow, YearNow);
                showadpter = new showDiaryAdapter(showactivity.this, showdata);
                showListView.setAdapter(showadpter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        spinnerY.setSelection(YearNow - 2012, true);
        spinnerY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                YearNow = pos + 2012;
                tminit(MonthNow, YearNow);
                showadpter = new showDiaryAdapter(showactivity.this, showdata);
                showListView.setAdapter(showadpter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        button_main = (Button) findViewById(R.id.show_btn);
        button_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Months", MonthNow);
                intent.putExtra("Years", YearNow);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    //将数据保存到本地
    public void saveObject(String name){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name,MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(showdata);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    private Object getObject(String name){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }

    //初始化一个月数据
    private void tminit(int monthNum,int yearNum)
    {
        showdata.clear();
       nameFile="object_"+String.valueOf(yearNum)+"_"+String.valueOf(monthNum);
        if(getObject(nameFile)==null) {
            String emptystr = "";
            String monthcount = String.valueOf(monthNum);
            int daymax = daynum_month[monthNum - 1];
            for (int i = 0; i < daymax; i++) {
                String daynum = String.valueOf(i + 1);
                String daycount=getweek(yearNum,String.valueOf(monthNum),daynum);
                showdata.add(new Diary(0, daynum, monthcount, daycount, emptystr));
            }
        }
        else
            {
                showdata=(ArrayList<Diary>)getObject(nameFile);
            }
    }


    String getweek(int year,String wmonth,String wday)
    {
        String weekOfday[]={"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
        int wmonthInt=Integer.parseInt( wmonth );
        int wdayInt=Integer.parseInt( wday);
        Calendar cal = Calendar.getInstance();
        cal.set(year,wmonthInt-1,wdayInt);
        Date dt = cal.getTime();
        cal.setTime(dt);
        int week_selected = cal.get(Calendar.DAY_OF_WEEK);
        return weekOfday[week_selected-1];
    }

    //得到今天日期
    private int getdaytime()
    {
        int mday;
        final Calendar c = Calendar.getInstance();
        mday=c.get(Calendar.DAY_OF_MONTH);
        return mday;
    }
    private void getNow()
    {
        final Calendar c = Calendar.getInstance();
        MonthNow=c.get(Calendar.MONTH)+1;
        YearNow=c.get(Calendar.YEAR);
    }
}

