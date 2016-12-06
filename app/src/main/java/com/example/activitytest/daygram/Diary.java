package com.example.activitytest.daygram;

import java.io.Serializable;

/**
 * Created by apple on 2016/9/21.
 */
public class Diary implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private int type ; //类型，用于表示是否写过日记
    private String dayNum;//日期
    private String daycount;//星期表示
    private String diaryText ;   //文本信息
    private String month;//月份

    public Diary(int type,String dayNum,String month,String daycount,String diaryText){
        this.type   =   type ;
        this.dayNum   =   dayNum ;
        this.month    =      month;
        this.daycount   =   daycount ;
        this.diaryText   =   diaryText ;
    }

    //五个get方法和五个set方法
    public int getType(){
        return  type ;
    }
    public void setType(int type){
        this.type   =   type ;
    }

    public String getdayNum(){
        return  dayNum ;
    }
    public void setdayNum(String dayNum){
        this.dayNum   =   dayNum ;
    }

    public String getdiaryText(){
        return diaryText ;
    }
    public void setdiaryText(String diaryText){
        this.diaryText   =   diaryText ;
    }

    public String getdaycount(){
        return daycount ;
    }
    public void setdaycount(String daycount){
        this.daycount   =   daycount ;
    }

    public String getmonth(){
        return month ;
    }
    public void setmonth(String month){
        this.month   =   month ;
    }

}

