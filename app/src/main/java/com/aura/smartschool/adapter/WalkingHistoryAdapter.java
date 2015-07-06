package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.SchoolVO;
import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2015-06-22.
 */
public class WalkingHistoryAdapter extends RecyclerView.Adapter<WalkingHistoryAdapter.WalkingHistoryViewHolder> {

    private ArrayList<WalkingVO> mWalkingHistory;
    private ArrayList<WalkingVO> mWalkingHistoryMonth;

    public WalkingHistoryAdapter(ArrayList<WalkingVO> walkingHistory) {
        this.mWalkingHistory = walkingHistory;
        mWalkingHistoryMonth = new ArrayList<>();
        makeMonthHistory();
    }

    private void makeMonthHistory() {
        int i = 0;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        mWalkingHistoryMonth.add(new WalkingVO(Util.getFirstDayTimeOfMonthInMillis(year, month), 0));
        for(WalkingVO dayWalking : mWalkingHistory) {
            if (dayWalking.date > mWalkingHistoryMonth.get(i).date) {
                mWalkingHistoryMonth.get(i).count = dayWalking.count;
            } else {
                month --;
                if (month < 0) {
                    year--;
                    month = 11;
                }
                mWalkingHistoryMonth.add(new WalkingVO(Util.getFirstDayTimeOfMonthInMillis(year, month), 0));
                i++;
            }
        }
    }

    @Override
    public WalkingHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View itemView = vi.inflate(R.layout.walking_history_item, viewGroup, false);
        return new WalkingHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WalkingHistoryViewHolder viewHolder, final int position) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mWalkingHistory.get(position).date);
        viewHolder.mDate.setText(String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
        viewHolder.mWalkingCount.setText(mWalkingHistory.get(position).count + " 걸음");
    }

    @Override
    public int getItemCount() {
        return mWalkingHistory.size();
    }

    public static class WalkingHistoryViewHolder extends RecyclerView.ViewHolder {
        public final TextView mDate;
        public final TextView mWalkingCount;
        public final TextView mCalories;
        public final TextView mDistance;
        public final TextView mTime;

        public WalkingHistoryViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
            mWalkingCount = (TextView) itemView.findViewById(R.id.tv_step_count);
            mCalories = (TextView) itemView.findViewById(R.id.tv_calories);
            mDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            mTime = (TextView) itemView.findViewById(R.id.tv_walking_time);
        }
    }
}
