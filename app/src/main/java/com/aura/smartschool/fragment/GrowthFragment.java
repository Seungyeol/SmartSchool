package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class GrowthFragment extends Fragment {

    private View mView;

    private MemberVO mMember;

    private RelativeLayout rl_coach;
    private TextView tvGrowthScore, tv_growth_content;
    private TextView tv_1, tv_2, tv_3; //근육조절, 체중조절, 체지방조절
    private TextView tv_babel;
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

        rl_coach = (RelativeLayout) mView.findViewById(R.id.rl_coach);
        tvGrowthScore = (TextView) mView.findViewById(R.id.tv_growth_score);
        tvGrowthScore.setText(String.format("%d점", (int)(mMember.mMeasureSummaryVO.growthGrade)));

        tv_growth_content = (TextView) mView.findViewById(R.id.tv_growth_content);
        String content = String.format("%s 학생의 키는 %.1fcm 이고 몸무게는 %skg 입니다. BMI는 %s로 %s이며 체지방은 %s 입니다. 건강을 위해서 아래사항을 참고하세요.",
                mMember.name, mMember.mMeasureSummaryVO.height, mMember.mMeasureSummaryVO.weight,
                mMember.mMeasureSummaryVO.bmi, GrowthGradeDescriptionFragment.GROWTH_GRADE.findDescription(mMember.mMeasureSummaryVO.bmiStatus).title, mMember.mMeasureSummaryVO.fat + "%");
        tv_growth_content.setText(content);

        tv_1 = (TextView) mView.findViewById(R.id.tv_1);
        tv_2 = (TextView) mView.findViewById(R.id.tv_2);
        tv_3 = (TextView) mView.findViewById(R.id.tv_3);
        tv_babel = (TextView) mView.findViewById(R.id.tv_babel);

        tv_1.setText(mMember.mMeasureSummaryVO.muscle_control + "kg");
        tv_2.setText(mMember.mMeasureSummaryVO.weight_control + "kg");
        tv_3.setText(mMember.mMeasureSummaryVO.fat_control + "kg");

        ivWeight1 = (ImageView) mView.findViewById(R.id.iv_weight1);
        ivWeight2 = (ImageView) mView.findViewById(R.id.iv_weight2);
        ivWeight3 = (ImageView) mView.findViewById(R.id.iv_weight3);
        ivWeight4 = (ImageView) mView.findViewById(R.id.iv_weight4);
        ivWeight5 = (ImageView) mView.findViewById(R.id.iv_weight5);
        ivWeight6 = (ImageView) mView.findViewById(R.id.iv_weight6);

        setGradeType();

        rl_coach.setOnClickListener(mClick);
        tv_babel.setOnClickListener(mClick);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    //저체중, 정상, 과체중, 비만, 중도비만, 고도비만
    private void setGradeType() {
        if (mMember.mMeasureSummaryVO.bmiStatus.startsWith("저체중")) {
            ivWeight1.setBackgroundResource(R.drawable.point_type_1);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.startsWith("정상")) {
            ivWeight2.setBackgroundResource(R.drawable.point_type_2);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.startsWith("과체중")) {
            ivWeight3.setBackgroundResource(R.drawable.point_type_3);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.startsWith("비만")) {
            ivWeight4.setBackgroundResource(R.drawable.point_type_4);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.startsWith("중도비만")) {
            ivWeight5.setBackgroundResource(R.drawable.point_type_5);
        } else if (mMember.mMeasureSummaryVO.bmiStatus.startsWith("고도비만")) {
            ivWeight6.setBackgroundResource(R.drawable.point_type_6);
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.rl_coach:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, GrowthGradeDescriptionFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;

                case R.id.tv_babel:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, VideoFragment.newInstance(mMember, 1)).addToBackStack(null).commit();
                    break;
            }
        }
    };
}