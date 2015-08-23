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
public class SchoolScheduleCalendarAdapter extends BaseAdapter {

    private Context context;
    private Calendar monthCalendar;

    private int columnNum;
    private int firstDayOfMonth;

    private ScheduleData[] scheduleDatas;

    public SchoolScheduleCalendarAdapter(Context context, Calendar monthCalendar, ScheduleData[] scheduleDatas) {
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
        return columnNum;
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
            convertView = View.inflate(context, R.layout.item_schedule_calendar, null);
            holder.linearItemWrapper = convertView.findViewById(R.id.ll_item_wrapper);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvSchedule1 = (TextView) convertView.findViewById(R.id.tv_schedule1);
            holder.tvSchedule2 = (TextView) convertView.findViewById(R.id.tv_schedule2);
            holder.tvSchedule3 = (TextView) convertView.findViewById(R.id.tv_schedule3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.linearItemWrapper.getLayoutParams();
        params.height = (int) (parent.getMeasuredHeight() / (Math.ceil((double)columnNum/7)));

        holder.tvSchedule1.setText("");
        holder.tvSchedule2.setText("");
        holder.tvSchedule3.setText("");

        int date = position - firstDayOfMonth + 2;
        if (date > 0 && date <= monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            holder.tvDate.setText(String.valueOf(date));
            try {
                if (scheduleDatas != null && scheduleDatas.length >= monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    String schedule = scheduleDatas[date-1].schedule;
                    if (!StringUtils.isBlank(schedule)) {
                        String[] schedules = schedule.split(" ");
                        for(int i = 0; i < schedules.length; i++) {
                            if (i==0) {
                                holder.tvSchedule1.setText(schedules[i]);
                            } else if (i == 1) {
                                holder.tvSchedule2.setText(schedules[i]);
                            } else {
                                holder.tvSchedule3.setText(schedules[i]);
                                break;
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            holder.tvDate.setText("");
        }

        if (position%7 == 0) {
            holder.tvDate.setTextColor(Color.RED);
        } else if (position%7 == 6) {
            holder.tvDate.setTextColor(Color.BLUE);
        } else {
            holder.tvDate.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    public void calculateMonth() {
        if (monthCalendar.get(Calendar.MONTH) == 0 || monthCalendar.get(Calendar.MONTH) == 1) {
            monthCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        } else {
            monthCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        }
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK);
        columnNum = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + (firstDayOfMonth - 1);
        columnNum += (7-columnNum%7);
    }

    private class ViewHolder {
        View linearItemWrapper;
        TextView tvDate;
        TextView tvSchedule1;
        TextView tvSchedule2;
        TextView tvSchedule3;
    }
}
