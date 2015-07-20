package com.aura.smartschool.fragment.schoolNoticeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.adapter.SchoolScheduleCalendarAdapter;
import com.aura.smartschool.adapter.SchoolScheduleListAdapter;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.vo.MemberVO;

import java.util.Calendar;

/**
 * Created by Administrator on 2015-07-18.
 */
public class SchoolScheduleFragment extends BaseFragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private TextView tvMonth;
    private View ivBtnPre;
    private View ivBtnNext;
    private View ivBtnChangeLayout;

    private View calendarContainer;
    private GridView gvCalendar;
    private SchoolScheduleCalendarAdapter calendarAdapter;

    private ListView mSchoolScheduleListView;
    private SchoolScheduleListAdapter mScheduleListAdapter;

    public static SchoolScheduleFragment newInstance(MemberVO member) {
        SchoolScheduleFragment instance = new SchoolScheduleFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_school_schedule_calendar, null);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        Calendar cal = Calendar.getInstance();
        tvMonth = (TextView) view.findViewById(R.id.tv_month);
        tvMonth.setText(String.format(getActivity().getResources().getText(R.string.month_schedule).toString(), (cal.get(Calendar.MONTH) + 1)));

        ivBtnChangeLayout = view.findViewById(R.id.iv_btn_change_layout);
        ivBtnChangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarContainer.getVisibility() == View.VISIBLE) {
                    calendarContainer.setVisibility(View.GONE);
                    mSchoolScheduleListView.setVisibility(View.VISIBLE);
                } else {
                    calendarContainer.setVisibility(View.VISIBLE);
                    mSchoolScheduleListView.setVisibility(View.GONE);
                }
            }
        });

        initCalendarView(view, cal);
        initListView(view, cal);
    }

    private void initListView(View view, Calendar cal) {
        mSchoolScheduleListView = (ListView) view.findViewById(R.id.list_school_schedule_list);
        mScheduleListAdapter = new SchoolScheduleListAdapter(getActivity(), cal);
        mSchoolScheduleListView.setAdapter(mScheduleListAdapter);
    }

    private void initCalendarView(View view, Calendar cal) {
        calendarContainer = view.findViewById(R.id.ll_calendar_container);
        gvCalendar = (GridView) view.findViewById(R.id.gv_calendar);
        calendarAdapter = new SchoolScheduleCalendarAdapter(getActivity(), cal);
        gvCalendar.setAdapter(calendarAdapter);
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
