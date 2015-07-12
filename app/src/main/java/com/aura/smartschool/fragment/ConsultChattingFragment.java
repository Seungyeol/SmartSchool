package com.aura.smartschool.fragment;

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
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 7. 11..
 */
public class ConsultChattingFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private RecyclerView mConsultChattingList;
//    private ConsultChattingAdapter mWalkingHistoryAdapter;

    public static ConsultChattingFragment newInstance(MemberVO member) {
        ConsultChattingFragment instance = new ConsultChattingFragment();
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
    }
}
