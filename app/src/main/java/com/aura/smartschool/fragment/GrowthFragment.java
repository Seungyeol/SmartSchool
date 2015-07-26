package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class GrowthFragment extends BaseFragment {

    private View mView;

    private MemberVO mMember;

    private ImageView ivCoach;
    private TextView tvGrowthScore;
    private ImageView ivWeight1, ivWeight2, ivWeight3, ivWeight4, ivWeight5, ivWeight6;

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
        mView = View.inflate(getActivity(), R.layout.fragment_growth_grade, null);

        ivCoach = (ImageView) mView.findViewById(R.id.iv_coach);
        tvGrowthScore = (TextView) mView.findViewById(R.id.tv_growth_score);
        tvGrowthScore.setText(mMember.mMeasureSummaryVO.growthGrade + "점");

        ivWeight1 = (ImageView) mView.findViewById(R.id.iv_weight1);
        ivWeight2 = (ImageView) mView.findViewById(R.id.iv_weight2);
        ivWeight3 = (ImageView) mView.findViewById(R.id.iv_weight3);
        ivWeight4 = (ImageView) mView.findViewById(R.id.iv_weight4);
        ivWeight5 = (ImageView) mView.findViewById(R.id.iv_weight5);
        ivWeight6 = (ImageView) mView.findViewById(R.id.iv_weight6);

        setGradeType();

        ivCoach.setOnClickListener(mClick);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    //저체중, 정상, 과체중, 비만, 중도비만, 고도비만
    private void setGradeType() {
        if (mMember.mMeasureSummaryVO.bmiStatus.contains("저체중")) {
            ivWeight1.setBackgroundResource(R.drawable.point_type_1);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.contains("정상")) {
            ivWeight2.setBackgroundResource(R.drawable.point_type_1);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.contains("과체중")) {
            ivWeight3.setBackgroundResource(R.drawable.point_type_1);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.contains("경도비만")) {
            ivWeight4.setBackgroundResource(R.drawable.point_type_1);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.contains("중도비만")) {
            ivWeight5.setBackgroundResource(R.drawable.point_type_1);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.contains("고도비만")) {
            ivWeight6.setBackgroundResource(R.drawable.point_type_1);
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.iv_coach:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, GrowthGradeDescriptionFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;
            }
        }
    };
}