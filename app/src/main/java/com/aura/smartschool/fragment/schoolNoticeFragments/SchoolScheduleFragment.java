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
import com.aura.smartschool.vo.SchoolNotiVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015-07-18.
 */
public class SchoolScheduleFragment extends BaseFragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private Calendar selMonth;

    private TextView tvMonth;
    private View ivBtnPre;
    private View ivBtnNext;
    private View ivBtnChangeLayout;

    private View calendarContainer;

    private GridView gvCalendar;
    private ListView mSchoolScheduleListView;

    private SchoolScheduleCalendarAdapter mCalendarAdapter;
    private SchoolScheduleListAdapter mScheduleListAdapter;

    private ArrayList<SchoolNotiVO> mScheduleList = new ArrayList<>();
    private Map<String, ArrayList<SchoolNotiVO>> mScheduleMap = new HashMap<>();

    public static SchoolScheduleFragment newInstance(MemberVO member, Calendar selCalendar) {
        SchoolScheduleFragment instance = new SchoolScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        args.putSerializable("selMonth", selCalendar);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
        this.selMonth = (Calendar) args.getSerializable("selMonth");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_school_schedule_calendar, null);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.actionbar_back, mMember.name);
    }

    public void setScheduleList(ArrayList<SchoolNotiVO> scheduleList) {
        this.mScheduleList = scheduleList;
        makeScheduleMap();
        if (mCalendarAdapter != null) {
            mCalendarAdapter.setScheduleMap(mScheduleMap);
            mCalendarAdapter.notifyDataSetChanged();
        }
        if (mScheduleListAdapter != null) {
            mScheduleListAdapter.setScheduleMap(mScheduleMap);
            mScheduleListAdapter.notifyDataSetChanged();
        }
    }

    private void initViews(View view) {
        tvMonth = (TextView) view.findViewById(R.id.tv_month);
        tvMonth.setText(String.format(getActivity().getResources().getText(R.string.month_schedule).toString(), (selMonth.get(Calendar.MONTH) + 1)));

        ivBtnPre = view.findViewById(R.id.iv_btn_pre);
        ivBtnNext = view.findViewById(R.id.iv_btn_next);
        ivBtnChangeLayout = view.findViewById(R.id.iv_btn_change_layout);

        ivBtnPre.setOnClickListener(clickListener);
        ivBtnNext.setOnClickListener(clickListener);
        ivBtnChangeLayout.setOnClickListener(clickListener);

        initCalendarView(view, selMonth);
        initListView(view, selMonth);
    }

    private void initListView(View view, Calendar cal) {
        mSchoolScheduleListView = (ListView) view.findViewById(R.id.list_school_schedule_list);
        mScheduleListAdapter = new SchoolScheduleListAdapter(getActivity(), cal, mScheduleMap);
        mSchoolScheduleListView.setAdapter(mScheduleListAdapter);
    }

    private void initCalendarView(View view, Calendar cal) {
        calendarContainer = view.findViewById(R.id.ll_calendar_container);
        gvCalendar = (GridView) view.findViewById(R.id.gv_calendar);
        mCalendarAdapter = new SchoolScheduleCalendarAdapter(getActivity(), cal, mScheduleMap);
        gvCalendar.setAdapter(mCalendarAdapter);
    }

    private void makeScheduleMap() {
        for(SchoolNotiVO notiVO : mScheduleList) {
            ArrayList<SchoolNotiVO> scheduleList = mScheduleMap.get(notiVO.notiDate);
            if (scheduleList == null) {
                scheduleList = new ArrayList<>();
            }
            scheduleList.add(notiVO);
            mScheduleMap.put(notiVO.notiDate, scheduleList);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_btn_pre:
                    selMonth.set(Calendar.MONTH, selMonth.get(Calendar.MONTH) - 1);
                    chageMonth();
                    break;
                case R.id.iv_btn_next:
                    selMonth.set(Calendar.MONTH, selMonth.get(Calendar.MONTH) + 1);
                    chageMonth();
                    break;
                case R.id.iv_btn_change_layout:
                    if (calendarContainer.getVisibility() == View.VISIBLE) {
                        calendarContainer.setVisibility(View.GONE);
                        mSchoolScheduleListView.setVisibility(View.VISIBLE);
                    } else {
                        calendarContainer.setVisibility(View.VISIBLE);
                        mSchoolScheduleListView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void chageMonth() {
        tvMonth.setText(String.format(getActivity().getResources().getText(R.string.month_schedule).toString(), (selMonth.get(Calendar.MONTH) + 1)));
        mCalendarAdapter.notifyDataSetChanged();
        mScheduleListAdapter.notifyDataSetChanged();
        ivBtnPre.setVisibility(View.VISIBLE);
        ivBtnNext.setVisibility(View.VISIBLE);
        if (selMonth.get(Calendar.MONTH) == 0) {
            ivBtnPre.setVisibility(View.GONE);
        }
        if (selMonth.get(Calendar.MONTH) == 11) {
            ivBtnNext.setVisibility(View.GONE);
        }
    }
}
