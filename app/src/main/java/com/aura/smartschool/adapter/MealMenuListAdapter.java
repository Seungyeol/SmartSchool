package com.aura.smartschool.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.R;
import com.aura.smartschool.vo.MealVO;
import com.aura.smartschool.vo.MenuData;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


/**
 * Created by Administrator on 2015-07-18.
 */
public class MealMenuListAdapter extends BaseAdapter {

    private AQuery mAquery;
    private Context context;

    private MenuData[] menuArray;
    private Map<String, Boolean> menuPicMap;

    private ArrayList<MealVO> mealList = new ArrayList<>();
    private Calendar mealCal;
    private int schoolId;

    private int firstDayOfMonth;

    private static final String NO_MEAL_MENU = "급식이 없습니다";

    public MealMenuListAdapter(Context context, Calendar mealCal, MenuData[] menuArray, Map<String, Boolean> menuPicMap) {
        this.context = context;
        mAquery = new AQuery(context);
        schoolId = LoginManager.getInstance().getLoginUser().mSchoolVO.school_id;
        this.menuArray = menuArray;
        this.mealCal = mealCal;
        this.menuPicMap = menuPicMap;
        makeMealList();
    }

    public void setMenuArray(Calendar mealCal, MenuData[] menuArray) {
        this.menuArray = menuArray;
        this.mealCal = mealCal;
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
            holder.ivBreakfast = (ImageView) convertView.findViewById(R.id.iv_breakfast);
            holder.tvLunch = (TextView) convertView.findViewById(R.id.tv_lunch);
            holder.ivLunch = (ImageView) convertView.findViewById(R.id.iv_lunch);
            holder.tvDinner = (TextView) convertView.findViewById(R.id.tv_dinner);
            holder.ivDinner = (ImageView) convertView.findViewById(R.id.iv_dinner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvDate.setText(mealList.get(position).day + " 일 " + getDayofWeekString(mealList.get(position).day));

        holder.tvBreakfast.setText(mealList.get(position).breakfast);
        if (menuPicMap.containsKey(String.format("%d-%02d-%02d_%d", mealCal.get(Calendar.YEAR), (mealCal.get(Calendar.MONTH) + 1), mealList.get(position).day, 1))){
            holder.ivBreakfast.setVisibility(View.VISIBLE);
            mAquery.id(holder.ivBreakfast).image(Constant.HOST + Constant.API_GET_DINING_IMAGE + schoolId + "_" + String.format("%d-%02d-%02d_%d", mealCal.get(Calendar.YEAR), (mealCal.get(Calendar.MONTH) + 1), mealList.get(position).day, 1) + ".jpg", true, true, 0, AQuery.GONE);
        } else {
            holder.ivBreakfast.setVisibility(View.GONE);
        }
        holder.tvLunch.setText(mealList.get(position).lunch);
        if (menuPicMap.containsKey(String.format("%d-%02d-%02d_%d", mealCal.get(Calendar.YEAR), (mealCal.get(Calendar.MONTH) + 1), mealList.get(position).day, 2))){
            holder.ivLunch.setVisibility(View.VISIBLE);
            mAquery.id(holder.ivLunch).image(Constant.HOST + Constant.API_GET_DINING_IMAGE + schoolId + "_" + String.format("%d-%02d-%02d_%d", mealCal.get(Calendar.YEAR), (mealCal.get(Calendar.MONTH) + 1), mealList.get(position).day, 2) + ".jpg", true, true, 0, AQuery.GONE);
        } else {
            holder.ivLunch.setVisibility(View.GONE);
        }
        holder.tvDinner.setText(mealList.get(position).dinner);
        if (menuPicMap.containsKey(String.format("%d-%02d-%02d_%d", mealCal.get(Calendar.YEAR), (mealCal.get(Calendar.MONTH) + 1), mealList.get(position).day, 3))){
            holder.ivDinner.setVisibility(View.VISIBLE);
            mAquery.id(holder.ivDinner).image(Constant.HOST + Constant.API_GET_DINING_IMAGE + schoolId + "_" + String.format("%d-%02d-%02d_%d", mealCal.get(Calendar.YEAR), (mealCal.get(Calendar.MONTH) + 1), mealList.get(position).day, 3) + ".jpg", true, true, 0, AQuery.GONE);
        } else {
            holder.ivDinner.setVisibility(View.GONE);
        }

        return convertView;
    }

    private String getDayofWeekString(int day) {
        switch ((day + firstDayOfMonth -1)%7) {
            case 1:
                return "(일)";
            case 2:
                return "(월)";
            case 3:
                return "(화)";
            case 4:
                return "(수)";
            case 5:
                return "(목)";
            case 6:
                return "(금)";
            case 7:
                return "(토)";
        }
        return "";
    }

    private void makeMealList() {
        int day = 0;
        firstDayOfMonth = mealCal.get(Calendar.DAY_OF_WEEK);
        mealList.clear();
        for (MenuData data : menuArray) {
            day++;
            if (data == null) continue;
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
        ImageView ivBreakfast;
        TextView tvLunch;
        ImageView ivLunch;
        TextView tvDinner;
        ImageView ivDinner;
    }
}
