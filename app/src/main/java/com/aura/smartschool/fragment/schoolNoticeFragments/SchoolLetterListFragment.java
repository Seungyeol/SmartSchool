package com.aura.smartschool.fragment.schoolNoticeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;
import com.aura.smartschool.adapter.SchoolNoticeAdapter;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolNotiVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-16.
 */
public class SchoolLetterListFragment extends BaseFragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private RecyclerView mSchoolLetterListView;
    private SchoolNoticeAdapter mLetterAdapter;

    private ArrayList<SchoolNotiVO> mLetterList = new ArrayList<>();

    public static SchoolLetterListFragment newInstance(MemberVO member) {
        SchoolLetterListFragment instance = new SchoolLetterListFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_school_notice, null);

        mSchoolLetterListView = (RecyclerView) view.findViewById(R.id.list_school_notice);
        mSchoolLetterListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLetterAdapter = new SchoolNoticeAdapter(mMember, mLetterList);
        mSchoolLetterListView.setAdapter(mLetterAdapter);

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

    public void setLetterList(ArrayList<SchoolNotiVO> letterList) {
        this.mLetterList = letterList;
        if (mLetterAdapter != null) {
            mLetterAdapter.setNotiList(mLetterList);
            mLetterAdapter.notifyDataSetChanged();
        }
    }
}
