package com.aura.smartschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.smartschool.R;

import java.util.Calendar;

/**
 * Created by Administrator on 2015-07-18.
 */
public class SchoolScheduleListAdapter extends BaseAdapter {

    private Context context;
    private Calendar month;

    private int firstDayOfMonth;

    public SchoolScheduleListAdapter(Context context, Calendar month) {
        this.context = context;
        this.month = month;

        month.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth = month.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public int getCount() {
        return month.getActualMaximum(Calendar.DAY_OF_MONTH);
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

        if (position == 10) {
            for (int i =0; i<2; i++) {
                addScheduleOnDate(holder, "Test 일정 " + i);
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
