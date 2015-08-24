package com.aura.smartschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MealVO;
import com.aura.smartschool.vo.MenuData;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Administrator on 2015-07-18.
 */
public class MealMenuListAdapter extends BaseAdapter {

    private Context context;
    private int firstDayOfMonth;
    private MenuData[] menuArray;

    private ArrayList<MealVO> mealList = new ArrayList<>();

    private static final String NO_MEAL_MENU = "급식이 없습니다";

    public MealMenuListAdapter(Context context, Calendar monthCalendar, MenuData[] menuArray) {
        this.context = context;
        this.menuArray = menuArray;
        firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK);
        makeMealList();
    }

    public void setMenuArray(MenuData[] menuArray) {
        this.menuArray = menuArray;
        makeMealList();
    }

    public int getPosition(int day) {
        for (int i = 0; i < mealList.size(); i++) {
            if (mealList.get(i).day == day) {
                return i;
            } else if (mealList.get(i).day > day) {
                return i - 1;
            }
        }
        return 0;
    }

    @Override
    public int getCount() {
        return mealList.size();
    }

    @Override
    public MealVO getItem(int position) {
        return mealList.get(position);
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

        holder.tvDate.setText(mealList.get(position).day + " 일");

        holder.tvBreakfast.setText(mealList.get(position).breakfast);
        holder.tvLunch.setText(mealList.get(position).lunch);
        holder.tvDinner.setText(mealList.get(position).dinner);

        return convertView;
    }

    private void makeMealList() {
        int day = 0;
        mealList.clear();
        for (MenuData data : menuArray) {
            day++;
            if (!StringUtils.equals(NO_MEAL_MENU, data.breakfast)
                    || !StringUtils.equals(NO_MEAL_MENU, data.lunch)
                    || !StringUtils.equals(NO_MEAL_MENU, data.dinner)) {
                mealList.add(new MealVO(day, data.breakfast, data.lunch, data.dinner));
            }
        }
    }

    private class ViewHolder {
        TextView tvDate;
        TextView tvBreakfast;
        TextView tvLunch;
        TextView tvDinner;
    }
}
