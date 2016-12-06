package com.example.activitytest.daygram;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2016/9/23.
 */

public class showDiaryAdapter extends BaseAdapter {
    private List<Diary> mData=new ArrayList<>();       //创建Diary类型的List表
    private LayoutInflater mInflater;               //定义线性布局过滤器

    public showDiaryAdapter(Context context, List<Diary> data) {
        for(int i=0;i<data.size();i++)
        {
            Diary diarymem = data.get(i);
            if(diarymem.getType()==1)
            {
               this.mData.add(diarymem);
            }
        }
        mInflater = LayoutInflater.from(context);       //获取布局
    }

    /**
     * 得到列表长度
     *
     * @return
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;    //得到子项位置id
    }


    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }


    //@Override
    // public int getViewTypeCount(){
    //    return 2 ;
    // }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.show_item, null);
            //绑定id
            holder.daycount = (TextView) convertView.findViewById(R.id.show_day);
            holder.diarycontent = (TextView) convertView.findViewById(R.id.show_listtext);
            convertView.setTag(holder);         //为View设置tag
            //设置布局中控件要显示的视图
            String showDay=mData.get(position).getdayNum()+" "+mData.get(position).getdaycount()+" /";
            holder.daycount.setText(showDay);
            holder.diarycontent.setText("                            "+mData.get(position).getdiaryText());
            if (mData.get(position).getdaycount().equals("SUNDAY") || mData.get(position).getdaycount().equals("SATURDAY"))
                holder.daycount.setTextColor(Color.parseColor("#FFE43440"));
            return convertView;     //返回一个view
    }


    /**
     * 实体类
     */
    public final class ViewHolder{
        public TextView daycount;
        public TextView diarycontent;
    }
}



