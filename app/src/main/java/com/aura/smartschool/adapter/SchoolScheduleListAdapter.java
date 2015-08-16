package com.aura.smartschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.ScheduleData;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

/**
 * Created by Administrator on 2015-07-18.
 */
public class SchoolScheduleListAdapter extends BaseAdapter {

    private Context context;
    private Calendar monthCalendar;

    private int firstDayOfMonth;
    private ScheduleData[] scheduleDatas;

    public SchoolScheduleListAdapter(Context context, Calendar monthCalendar, ScheduleData[] scheduleDatas) {
        this.context = context;
        this.monthCalendar = monthCalendar;
        this.scheduleDatas = scheduleDatas;
        calculateMonth();
    }

    public void setScheduleDatas(ScheduleData[] scheduleDatas) {
        this.scheduleDatas = scheduleDatas;
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
        } else {
            holder.tvDate.setTextColor(Color.BLACK);
        }

        holder.linearItemWrapper.removeAllViewsInLayout();

        try {
            if (scheduleDatas != null && scheduleDatas.length > position) {
                String schedule = scheduleDatas[position].schedule;
                if (!StringUtils.isBlank(schedule)) {
                    String[] schedules = schedule.split(" ");
                    for(String s : schedules) {
                        addScheduleOnDate(holder, s);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public void calculateMonth() {
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK);
    }

    private void addScheduleOnDate(ViewHolder holder, String schedule) {
        TextView shedule = new TextView(context);
        shedule.setText(schedule);
        holder.linearItemWrapper.addView(shedule);
    }

    private class ViewHolder {
        TextView tvDate;
        LinearLayout linearItemWrapper;
    }
}
