package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2015-06-22.
 */
public class WalkingHistoryAdapter extends RecyclerView.Adapter<WalkingHistoryAdapter.WalkingHistoryViewHolder> {

    private ArrayList<WalkingHistoryVO> mWalkingHistory;
    private ArrayList<WalkingVO> mWalkingHistoryDay;
    private ArrayList<WalkingVO> mWalkingHistoryMonth;

    private class WalkingHistoryVO {
        private WalkingVO walkingVO;
        private int viewType;
        public WalkingHistoryVO(WalkingVO walkingVO, int viewType) {
            this.walkingVO = walkingVO;
            this.viewType = viewType;
        }
    }

    public WalkingHistoryAdapter(ArrayList<WalkingVO> walkingHistory) {
        this.mWalkingHistoryDay = walkingHistory;
        mWalkingHistoryMonth = new ArrayList<>();
        makeMonthHistory();
        mergeWalkingHistory();
    }

    private void mergeWalkingHistory() {
        mWalkingHistory = new ArrayList<>();
        if (mWalkingHistoryMonth.size() > 0 && mWalkingHistoryDay.size() > 0) {
            mWalkingHistory.add(new WalkingHistoryVO(mWalkingHistoryMonth.get(0), 0));
            int addedHeaderSize = 1;
            for (int i=0;i<mWalkingHistoryDay.size(); i++) {
                if (mWalkingHistoryDay.get(i).date < mWalkingHistoryMonth.get(addedHeaderSize-1).date) {
                    mWalkingHistory.add(new WalkingHistoryVO(mWalkingHistoryMonth.get(addedHeaderSize++), 0));
                }
                mWalkingHistory.add(new WalkingHistoryVO(mWalkingHistoryDay.get(i), 1));
            }
        }
    }

    private void makeMonthHistory() {
        int i = 0;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        mWalkingHistoryMonth.add(new WalkingVO(Util.getFirstDayTimeOfMonthInMillis(year, month), 0, 0, 0, 0));
        for (WalkingVO dayWalking : mWalkingHistoryDay) {
            if (dayWalking.date > mWalkingHistoryMonth.get(i).date) {
                mWalkingHistoryMonth.get(i).count += dayWalking.count;
                mWalkingHistoryMonth.get(i).calories += dayWalking.calories;
                mWalkingHistoryMonth.get(i).distance += dayWalking.distance;
                mWalkingHistoryMonth.get(i).activeTime += dayWalking.activeTime;
            } else {
                month --;
                if (month < 0) {
                    year--;
                    month = 11;
                }
                mWalkingHistoryMonth.add(new WalkingVO(Util.getFirstDayTimeOfMonthInMillis(year, month), 0, 0, 0, 0));
                i++;
            }
        }
        removeEmptyMonthHistory();
    }

    private void removeEmptyMonthHistory() {
        for (int i = mWalkingHistoryMonth.size() -1; i >=0; i--) {
            if (mWalkingHistoryMonth.get(i).count == 0) {
                mWalkingHistoryMonth.remove(0);
            }
        }
    }

    @Override
    public WalkingHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        if (viewType == 0) {
            View itemView = vi.inflate(R.layout.walking_history_month_item, viewGroup, false);
            return new WalkingHistoryMonthViewHolder(itemView);
        } else {
            View itemView = vi.inflate(R.layout.walking_history_item, viewGroup, false);
            return new WalkingHistoryDayViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(WalkingHistoryViewHolder viewHolder, final int position) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mWalkingHistory.get(position).walkingVO.date);
        if (getItemViewType(position) == 0) { //Month
            ((WalkingHistoryMonthViewHolder)viewHolder).mMonth.setText(String.format("%d년 %02d월", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1));
            ((WalkingHistoryMonthViewHolder)viewHolder).mWalkingCount.setText("총 " + mWalkingHistory.get(position).walkingVO.count + " 걸음");
        } else { //Day
            ((WalkingHistoryDayViewHolder)viewHolder).mDate.setText(String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
            ((WalkingHistoryDayViewHolder)viewHolder).mWalkingCount.setText(mWalkingHistory.get(position).walkingVO.count + " 걸음");
            ((WalkingHistoryDayViewHolder)viewHolder).mCalories.setText(mWalkingHistory.get(position).walkingVO.calories + " Kcal");
            ((WalkingHistoryDayViewHolder)viewHolder).mDistance.setText(String.format("%.2f", mWalkingHistory.get(position).walkingVO.distance) + " Km");

            int hour = mWalkingHistory.get(position).walkingVO.activeTime / 3600;
            int min = (mWalkingHistory.get(position).walkingVO.activeTime % 3600) / 60;
            int sec = mWalkingHistory.get(position).walkingVO.activeTime % 60;
            ((WalkingHistoryDayViewHolder)viewHolder).mActiveTime.setText(String.format("%02d:%02d:%02d", hour, min, sec));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mWalkingHistory.get(position).viewType;
    }

    @Override
    public int getItemCount() {
        return mWalkingHistory.size();
    }

    public class WalkingHistoryViewHolder extends RecyclerView.ViewHolder {
        public WalkingHistoryViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class WalkingHistoryDayViewHolder extends WalkingHistoryViewHolder {
        public final TextView mDate;
        public final TextView mWalkingCount;
        public final TextView mCalories;
        public final TextView mDistance;
        public final TextView mActiveTime;

        public WalkingHistoryDayViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
            mWalkingCount = (TextView) itemView.findViewById(R.id.tv_step_count);
            mCalories = (TextView) itemView.findViewById(R.id.tv_calories);
            mDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            mActiveTime = (TextView) itemView.findViewById(R.id.tv_walking_time);
        }
    }

    public class WalkingHistoryMonthViewHolder extends WalkingHistoryViewHolder {
        public final TextView mMonth;
        public final TextView mWalkingCount;

        public WalkingHistoryMonthViewHolder(View itemView) {
            super(itemView);
            mMonth = (TextView) itemView.findViewById(R.id.tv_month);
            mWalkingCount = (TextView) itemView.findViewById(R.id.tv_step_count);
        }
    }
}
