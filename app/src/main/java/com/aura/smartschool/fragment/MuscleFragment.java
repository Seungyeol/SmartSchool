package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class MuscleFragment extends Fragment {

    private View mView;
    private TextView tv_skeletal, tv_muscle;
    private TextView tv_help_left, tv_help_right;
    private TextView tv_man_status;
    private ImageView iv_bmi;
    private PopupWindow mPopup;

    private MemberVO mMember;

    public MuscleFragment() {
        // Required empty public constructor
    }

    public static MuscleFragment newInstance(MemberVO member) {

        MuscleFragment instance = new MuscleFragment();

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
        mView = View.inflate(getActivity(), R.layout.fragment_muscle, null);

        tv_skeletal = (TextView) mView.findViewById(R.id.tv_skeletal);
        tv_muscle = (TextView) mView.findViewById(R.id.tv_muscle);
        tv_help_left = (TextView) mView.findViewById(R.id.tv_help_left);
        tv_help_right = (TextView) mView.findViewById(R.id.tv_help_right);
        tv_man_status = (TextView) mView.findViewById(R.id.tv_man_status);
        iv_bmi = (ImageView) mView.findViewById(R.id.iv_bmi);

        int muscle_control = 0;
        try {
            muscle_control = Integer.parseInt(mMember.mMeasureSummaryVO.muscle_control);
        } catch (Exception e) {
        }

        tv_skeletal.setText(String.format("%s", mMember.mMeasureSummaryVO.skeletal) + "kg");
        tv_muscle.setText( (muscle_control >=0 ? "+" : "-") +  String.valueOf(mMember.mMeasureSummaryVO.muscle_control) + "kg");

        if(muscle_control >= 3) {
            iv_bmi.setImageResource(R.drawable.point_man_3);
            tv_man_status.setText("부족");
        } else if(muscle_control >= 0 && muscle_control < 3) {
            iv_bmi.setImageResource(R.drawable.point_man_2);
            tv_man_status.setText("정상");
        }  else {
            iv_bmi.setImageResource(R.drawable.point_man_1);
            tv_man_status.setText("많음");
        }

        tv_help_left.setOnClickListener(mClick);
        tv_help_right.setOnClickListener(mClick);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mPopup != null && mPopup.isShowing()) {
                mPopup.dismiss();
                return;
            }

            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, dm);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, dm);
            View view = View.inflate(getActivity(), R.layout.popup_help, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);

            switch(v.getId()) {
                case R.id.tv_help_left:
                    tv_title.setText("골격근량이란?");
                    tv_content.setText("뼈에 붙어 직접적으로 힘을 낼수있는 근육을 말합니다.");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    //mPopup.showAsDropDown(tv_lastmonth);
                    break;

                case R.id.tv_help_right:
                    tv_title.setText("근육조절?");
                    tv_content.setText("표준이하: 근육조절 +3kg 이상");
                    tv_content.append("\n");
                    tv_content.append("표준: 근육조절 +0kg 이상 ~ 3Kg 미만");
                    tv_content.append("\n");
                    tv_content.append("표준이상: 근육조절 +0kg 미만");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    break;
            }
        }
    };
}