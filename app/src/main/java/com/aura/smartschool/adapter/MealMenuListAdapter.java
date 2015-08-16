package com.aura.smartschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MenuData;

import java.util.Calendar;


/**
 * Created by Administrator on 2015-07-18.
 */
public class MealMenuListAdapter extends BaseAdapter {

    private Context context;
    private int firstDayOfMonth;
    private MenuData[] menuArray;

    public MealMenuListAdapter(Context context, Calendar monthCalendar, MenuData[] menuArray) {
        this.context = context;
        this.menuArray = menuArray;
        firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setMenuArray(MenuData[] menuArray) {
        this.menuArray = menuArray;
    }

    @Override
    public int getCount() {
        return menuArray.length;
    }

    @Override
    public MenuData getItem(int position) {
        return menuArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_meal_list, null);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvBreakfast = (TextView) convertView.findViewById(R.id.tv_breakfast);
            holder.tvLunch = (TextView) convertView.findViewById(R.id.tv_lunch);
            holder.tvDinner = (TextView) convertView.findViewById(R.id.tv_dinner);
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

        holder.tvBreakfast.setText(menuArray[position].breakfast);
        holder.tvLunch.setText(menuArray[position].lunch);
        holder.tvDinner.setText(menuArray[position].dinner);

        return convertView;
    }

    private class ViewHolder {
        TextView tvDate;
        TextView tvBreakfast;
        TextView tvLunch;
        TextView tvDinner;
    }
}
