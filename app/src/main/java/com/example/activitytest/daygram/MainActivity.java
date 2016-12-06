package com.example.activitytest.daygram;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    int[] daynum_month={31,29,31,30,31,30,31,31,30,31,30,31};
    private    ListView   mListView ;
    private DiaryAdapter adpter;
    Diary tddiary=new Diary(0,"","","","");//被操作的某天数据
    ArrayList<Diary> data = new ArrayList<Diary>(); // 当月数据
    int mday;//被操作日期
    String nameFile;
    Spinner spinnerM;
    Spinner spinnerY;

    int YearNow;
    int MonthNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        getNow();
        tminit(MonthNow,YearNow);//初始化显示当月日记

       //listview 显示当月日记
        mListView   =   (ListView)findViewById(R.id.list_view);
        adpter=new DiaryAdapter(this , data);
        mListView.setAdapter(adpter); //为ListView设置适配器
        //按下list中元素，进入编辑页面，传入对象
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mday = position;
                Intent intent = new Intent(MainActivity.this, editactivity.class);
                tddiary = data.get(mday);
                tddiary.setdaycount(getweek(YearNow,tddiary.getmonth(),tddiary.getdayNum()));
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("today", tddiary);
                intent.putExtras(mBundle);
                intent.putExtra("Year",YearNow);
                startActivityForResult(intent, 1);
            }
        });

        //spinner监听
        spinnerM = (Spinner) findViewById(R.id.spinner_month);
        spinnerM.setSelection(MonthNow-1,true);
        spinnerM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                MonthNow=pos+1;
                tminit(MonthNow,YearNow);
                adpter=new DiaryAdapter(MainActivity.this , data);
                mListView.setAdapter(adpter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        spinnerY = (Spinner) findViewById(R.id.spinner_year);
        spinnerY.setSelection(YearNow-2012,true);
        spinnerY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                YearNow=pos+2012;
                tminit(MonthNow,YearNow);
                adpter=new DiaryAdapter(MainActivity.this , data);
                mListView.setAdapter(adpter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //点击加号，添加当天日记
        Button button_add=(Button)findViewById(R.id.title_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNow();
                spinnerM.setSelection(MonthNow-1,true);
                spinnerY.setSelection(YearNow-2012,true);
                tminit(MonthNow,YearNow);
                adpter=new DiaryAdapter(MainActivity.this , data);
                mListView.setAdapter(adpter);
                Intent intent=new Intent(MainActivity.this,editactivity.class);
                mday=getdaytime()-1;
                tddiary=data.get(mday);
                tddiary.setdaycount(getweek(2016,tddiary.getmonth(),tddiary.getdayNum()));
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("today",tddiary);
                intent.putExtras(mBundle);
                intent.putExtra("Year",YearNow);
                startActivityForResult(intent,1);
            }
        });

        Button button_show=(Button)findViewById(R.id.show_btn);
        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,showactivity.class);
                intent.putExtra("Month",MonthNow);
                intent.putExtra("Year",YearNow);
                startActivityForResult(intent,2);
            }
        });

    }

    //返回主界面，传出日记内容
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent datait) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String new_diary = datait.getStringExtra("new_diary");
                    tddiary.setType(1);
                    tddiary.setdiaryText(new_diary);
                    data.set(mday,tddiary);
                    saveObject(nameFile);
                    adpter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    MonthNow = datait.getIntExtra("Months",-1);
                    YearNow = datait.getIntExtra("Years",-1);
                    spinnerM.setSelection(MonthNow-1,true);
                    spinnerY.setSelection(YearNow-2012,true);
                    tminit(MonthNow,YearNow);
                    adpter=new DiaryAdapter(MainActivity.this , data);
                    mListView.setAdapter(adpter);
                }
                break;
            default:
        }
    }

    //将数据保存到本地
    private void saveObject(String name){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name,MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
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
    public void tminit(int monthNum,int yearNum)
    {
        data.clear();
       nameFile="object_"+String.valueOf(yearNum)+"_"+String.valueOf(monthNum);
        if(getObject(nameFile)==null) {
            String emptystr = "";
            String monthcount = String.valueOf(monthNum);
            int daymax = daynum_month[monthNum - 1];
            for (int i = 0; i < daymax; i++) {
                String daynum = String.valueOf(i + 1);
                String daycount=getweek(yearNum,String.valueOf(monthNum),daynum);
                data.add(new Diary(0, daynum, monthcount, daycount, emptystr));
            }
        }
        else
            {
                data=(ArrayList<Diary>)getObject(nameFile);
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

