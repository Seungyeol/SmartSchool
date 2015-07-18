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
public class SchoolMonthCalendarScheduleAdapter extends BaseAdapter {

    private Context context;
    private Calendar month;

    private int columnNum;
    private int firstDayOfMonth;

    public SchoolMonthCalendarScheduleAdapter(Context context, Calendar month) {
        this.context = context;
        this.month = month;
        calculateMonth();
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
            convertView = View.inflate(context, R.layout.item_month_calendar, null);
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

        int date = position - firstDayOfMonth + 2;
        if (date > 0 && date <= month.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            holder.tvDate.setText(String.valueOf(date));
        } else {
            holder.tvDate.setText("");
        }

        if (date == 10) {
            holder.tvSchedule1.setText("test 일정");
        }


        if (position%7 == 0) {
            holder.tvDate.setTextColor(Color.RED);
        } else if (position%7 == 6) {
            holder.tvDate.setTextColor(Color.BLUE);
        }

        return convertView;
    }

    private void calculateMonth() {
        month.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth = month.get(Calendar.DAY_OF_WEEK);
        columnNum = month.getActualMaximum(Calendar.DAY_OF_MONTH) + (firstDayOfMonth - 1);
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
