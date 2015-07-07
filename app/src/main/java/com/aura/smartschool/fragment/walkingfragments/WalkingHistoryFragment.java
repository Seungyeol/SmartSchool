package com.aura.smartschool.fragment.walkingfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;
import com.aura.smartschool.adapter.WalkingHistoryAdapter;
import com.aura.smartschool.database.DBStepCounter;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-07.
 */
public class WalkingHistoryFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private RecyclerView mWalkingHistory;
    private WalkingHistoryAdapter mWalkingHistoryAdapter;

    public static WalkingHistoryFragment newInstance(MemberVO member) {
        WalkingHistoryFragment instance = new WalkingHistoryFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_walking_history, null);
        mWalkingHistory = (RecyclerView) view.findViewById(R.id.list_walking_history);
        mWalkingHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<WalkingVO> list = DBStepCounter.getInstance(getActivity()).getAllSteps();
        mWalkingHistoryAdapter = new WalkingHistoryAdapter(DBStepCounter.getInstance(getActivity()).getAllSteps());
        mWalkingHistory.setAdapter(mWalkingHistoryAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
    }

}
