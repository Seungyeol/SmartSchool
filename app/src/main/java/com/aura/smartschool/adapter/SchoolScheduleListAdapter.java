package com.aura.smartschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.SchoolNotiVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Administrator on 2015-07-18.
 */
public class SchoolScheduleListAdapter extends BaseAdapter {

    private Context context;
    private Calendar monthCalendar;

    private int firstDayOfMonth;

    private Map<String, ArrayList<SchoolNotiVO>> mScheduleMap;

    public SchoolScheduleListAdapter(Context context, Calendar monthCalendar, Map<String, ArrayList<SchoolNotiVO>> scheduleMap) {
        this.context = context;
        this.monthCalendar = monthCalendar;
        this.mScheduleMap = scheduleMap;

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setScheduleMap(Map<String, ArrayList<SchoolNotiVO>> scheduleMap) {
        this.mScheduleMap = scheduleMap;
    }

    @Override
    public int getCount() {
        return monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_schedule_list, null);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.linearItemWrapper = (LinearLayout) convertView.findViewById(R.id.ll_schedule_list_wrapper);
            holder.linearItemWrapper.removeAllViewsInLayout();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvDate.setText(String.valueOf(position + 1));

        if ((position+firstDayOfMonth-1)%7 == 0) {
            holder.tvDate.setTextColor(Color.RED);
        } else if ((position+firstDayOfMonth-1)%7 == 6) {
            holder.tvDate.setTextColor(Color.BLUE);
        }

        int year = monthCalendar.get(Calendar.YEAR);
        int month = monthCalendar.get(Calendar.MONTH) + 1;

        holder.linearItemWrapper.removeAllViewsInLayout();
        ArrayList<SchoolNotiVO> notiList = mScheduleMap.get(String.format("%d-%02d-%02d", year, month, (position+1)));
        if (notiList != null) {
            for(SchoolNotiVO noti:notiList) {
                addScheduleOnDate(holder, noti.title);
            }
        }

        return convertView;
    }

    private void addScheduleOnDate(ViewHolder holder, String scheduleTitle) {
        TextView shedule = new TextView(context);
        shedule.setText(scheduleTitle);
        holder.linearItemWrapper.addView(shedule);
    }

    private class ViewHolder {
        TextView tvDate;
        LinearLayout linearItemWrapper;
    }
}
