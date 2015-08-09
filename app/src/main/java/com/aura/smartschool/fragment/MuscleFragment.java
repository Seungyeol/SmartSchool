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
    private TextView tv_bmi, tv_fat;
    private TextView tv_help_left, tv_help_right;
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

        tv_bmi = (TextView) mView.findViewById(R.id.tv_bmi);
        tv_fat = (TextView) mView.findViewById(R.id.tv_fat);
        tv_help_left = (TextView) mView.findViewById(R.id.tv_help_left);
        tv_help_right = (TextView) mView.findViewById(R.id.tv_help_right);
        iv_bmi = (ImageView) mView.findViewById(R.id.iv_bmi);

        tv_bmi.setText(String.format("%s", mMember.mMeasureSummaryVO.skeletal));
        tv_fat.setText(String.valueOf(mMember.mMeasureSummaryVO.muscle_percent) + "%");

        if("표준".equals(mMember.mMeasureSummaryVO.weightStatus)) {
            iv_bmi.setImageResource(R.drawable.type_normal);
        } else if("표준 이상".equals(mMember.mMeasureSummaryVO.weightStatus)) {
            iv_bmi.setImageResource(R.drawable.type_over_1);
        }  else {
            iv_bmi.setImageResource(R.drawable.type_under);
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
                    tv_title.setText("근육량이란?");
                    tv_content.setText("우리 몸 전체에 있는 근육량을 의미합니다.");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    break;
            }
        }
    };
}