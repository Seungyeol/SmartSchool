package com.aura.smartschool.fragment.schoolNoticeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.adapter.SchoolMonthCalendarScheduleAdapter;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.vo.MemberVO;

import java.util.Calendar;

/**
 * Created by Administrator on 2015-07-18.
 */
public class SchoolMonthCalScheduleFragment extends BaseFragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private TextView tvMonth;
    private GridView gvCalendar;
    private SchoolMonthCalendarScheduleAdapter calendarAdapter;

    public static SchoolMonthCalScheduleFragment newInstance(MemberVO member) {
        SchoolMonthCalScheduleFragment instance = new SchoolMonthCalScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_school_cal_schedule, null);

        Calendar cal = Calendar.getInstance();
        tvMonth = (TextView) view.findViewById(R.id.tv_month);

        tvMonth.setText(String.format(getActivity().getResources().getText(R.string.month_schedule).toString(), (cal.get(Calendar.MONTH) + 1)));

        gvCalendar = (GridView) view.findViewById(R.id.gv_calendar);
        calendarAdapter = new SchoolMonthCalendarScheduleAdapter(getActivity(), cal);
        gvCalendar.setAdapter(calendarAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.actionbar_back, mMember.name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
