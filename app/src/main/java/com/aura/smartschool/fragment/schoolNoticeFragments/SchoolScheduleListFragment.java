package com.aura.smartschool.fragment.schoolNoticeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aura.smartschool.R;
import com.aura.smartschool.adapter.SchoolScheduleListAdapter;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.vo.MemberVO;

import java.util.Calendar;

/**
 * Created by Administrator on 2015-07-16.
 */
public class SchoolScheduleListFragment extends BaseFragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private ListView mSchoolScheduleListView;
    private SchoolScheduleListAdapter mScheduleListAdapter;

    public static SchoolScheduleListFragment newInstance(MemberVO member) {
        SchoolScheduleListFragment instance = new SchoolScheduleListFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_school_schedule_list, null);

        mSchoolScheduleListView = (ListView) view.findViewById(R.id.list_school_schedule_list);

        mScheduleListAdapter = new SchoolScheduleListAdapter(getActivity(), Calendar.getInstance());
        mSchoolScheduleListView.setAdapter(mScheduleListAdapter);

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
