package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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

public class BmiFragment extends Fragment {

    private View mView;
    private TextView tv_bmi, tv_fat;
    private TextView tv_bmi_title, tv_fat_title;
    private View tv_help_left, tv_help_right;
    private ImageView iv_man;
    private TextView tv_man;
    private TextView tv_babel;
    private PopupWindow mPopup;

    private MemberVO mMember;

    public BmiFragment() {
        // Required empty public constructor
    }

    public static BmiFragment newInstance(MemberVO member) {

        BmiFragment instance = new BmiFragment();

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
        mView = View.inflate(getActivity(), R.layout.fragment_bmi, null);

        tv_bmi = (TextView) mView.findViewById(R.id.tv_bmi);
        tv_fat = (TextView) mView.findViewById(R.id.tv_muscle);
        tv_bmi_title = (TextView) mView.findViewById(R.id.tv_bmi_title);
        tv_fat_title = (TextView) mView.findViewById(R.id.tv_fat_title);
        tv_help_left = mView.findViewById(R.id.tv_help_left);
        tv_help_right = mView.findViewById(R.id.tv_help_right);
        iv_man = (ImageView) mView.findViewById(R.id.iv_man);
        tv_man = (TextView) mView.findViewById(R.id.tv_man);
        tv_babel = (TextView) mView.findViewById(R.id.tv_babel);

        tv_bmi_title.setText(Html.fromHtml("<u>BMI</u>"));
        tv_fat_title.setText(Html.fromHtml("<u>체지방률</u>"));

        tv_bmi.setText(String.format("%s", mMember.mMeasureSummaryVO.bmi));
        tv_fat.setText(String.valueOf(mMember.mMeasureSummaryVO.fat) + "%");

        if(mMember.mMeasureSummaryVO.bmiStatus.equals("저체중 A")) {
            iv_man.setImageResource(R.drawable.point_man_1);
            tv_man.setText("저체중\n위험");
        }else  if(mMember.mMeasureSummaryVO.bmiStatus.equals("저체중 B")) {
            iv_man.setImageResource(R.drawable.point_man_1);
            tv_man.setText("저체중\n경고");
        }else  if(mMember.mMeasureSummaryVO.bmiStatus.equals("저체중 C")) {
            iv_man.setImageResource(R.drawable.point_man_1);
            tv_man.setText("저체중\n노력");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("정상 A")) {
            iv_man.setImageResource(R.drawable.point_man_2);
            tv_man.setText("정상\n보통");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("정상 B")) {
            iv_man.setImageResource(R.drawable.point_man_2);
            tv_man.setText("정상\n관리");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("과체중 A")) {
            iv_man.setImageResource(R.drawable.point_man_3);
            tv_man.setText("과체중\n경고");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("과체중 B")) {
            iv_man.setImageResource(R.drawable.point_man_3);
            tv_man.setText("과체중\n위험");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("비만 A")) {
            iv_man.setImageResource(R.drawable.point_man_4);
            tv_man.setText("비만\n관리");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("비만 B")) {
            iv_man.setImageResource(R.drawable.point_man_4);
            tv_man.setText("비만\n경고");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("비만 C")) {
            iv_man.setImageResource(R.drawable.point_man_4);
            tv_man.setText("비만\n위험");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("중도비만 A")) {
            iv_man.setImageResource(R.drawable.point_man_5);
            tv_man.setText("중도\n비만\n관리");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("중도비만 B")) {
            iv_man.setImageResource(R.drawable.point_man_5);
            tv_man.setText("중도\n비만\n경고");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("중도비만 C")) {
            iv_man.setImageResource(R.drawable.point_man_5);
            tv_man.setText("중도\n비만\n위험");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("고도비만 A")) {
            iv_man.setImageResource(R.drawable.point_man_6);
            tv_man.setText("고도\n비만\n관리");
        } else if(mMember.mMeasureSummaryVO.bmiStatus.startsWith("고도비만 B")) {
            iv_man.setImageResource(R.drawable.point_man_6);
            tv_man.setText("고도\n비만\n경고");
        } else { //고도 비만
            iv_man.setImageResource(R.drawable.point_man_6);
            tv_man.setText("고도\n비만\n위험");
        }

        iv_man.post(new Runnable() {
            @Override
            public void run() {
                ((View) iv_man.getParent()).getLayoutParams().width = ((View) iv_man.getParent()).getMeasuredHeight();
                ((View) iv_man.getParent()).requestLayout();
            }
        });

        tv_help_left.setOnClickListener(mClick);
        tv_help_right.setOnClickListener(mClick);
        tv_babel.setOnClickListener(mClick);

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
                    tv_title.setText("BMI란?");
                    tv_content.setText("체중(kg)을 키의 제곱으로 나눈 값을 통해 지방의 양을 추정하는 비만 측정법이다. 수치가 클수록 체격이 커진다.");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    //mPopup.showAsDropDown(tv_lastmonth);
                    break;

                case R.id.tv_help_right:
                    tv_title.setText("체지방률이란?");
                    tv_content.setText("체중에서 체지방이 차지하는 비율. 수치가 작을수록 근육형체형이다.");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    break;

                case R.id.tv_babel:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, VideoFragment.newInstance(mMember, 4)).addToBackStack(null).commit();
                    break;
            }
        }
    };
}