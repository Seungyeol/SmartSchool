package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class GrowthFragment extends BaseFragment {

    private View mView;

    private MemberVO mMember;

    public GrowthFragment() {
        // Required empty public constructor
    }

    public static GrowthFragment newInstance(MemberVO member) {

        GrowthFragment instance = new GrowthFragment();

        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable("member");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_growth, null);


        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {

            }
        }
    };
}