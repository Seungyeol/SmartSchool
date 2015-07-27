package com.aura.smartschool.fragment;

import android.os.Bundle;
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

public class SmokeFragment extends BaseFragment {

    private View mView;

    private MemberVO mMember;

    private ImageView iv_smoke, iv_smoke1, iv_smoke2, iv_smoke3, iv_smoke4;
    private TextView tv_ppm, tv_cohb;
    private TextView tv_help_left, tv_help_right;

    private PopupWindow mPopup;

    public SmokeFragment() {
        // Required empty public constructor
    }

    public static SmokeFragment newInstance(MemberVO member) {

        SmokeFragment instance = new SmokeFragment();

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
        mView = View.inflate(getActivity(), R.layout.fragment_smoke, null);

        iv_smoke = (ImageView) mView.findViewById(R.id.iv_smoke);
        iv_smoke1 = (ImageView) mView.findViewById(R.id.iv_smoke1);
        iv_smoke2 = (ImageView) mView.findViewById(R.id.iv_smoke2);
        iv_smoke3 = (ImageView) mView.findViewById(R.id.iv_smoke3);
        iv_smoke4 = (ImageView) mView.findViewById(R.id.iv_smoke4);
        tv_ppm = (TextView) mView.findViewById(R.id.tv_ppm);
        tv_cohb = (TextView) mView.findViewById(R.id.tv_cohb);
        tv_help_left = (TextView) mView.findViewById(R.id.tv_help_left);
        tv_help_right = (TextView) mView.findViewById(R.id.tv_help_right);

        float ppm = 0;
        float cohb = 0;
        try {
            ppm = Float.parseFloat(mMember.mMeasureSummaryVO.ppm);
            cohb = Float.parseFloat(mMember.mMeasureSummaryVO.cohd);
        } catch (NumberFormatException e) {

        }

        tv_ppm.setText(String.format("%.1f", ppm));
        tv_cohb.setText(String.format("%.1f", cohb));

        if(ppm <= 2) {
            iv_smoke.setImageResource(R.drawable.smoking1);
            iv_smoke1.setBackgroundResource(R.drawable.point_type_1);
        } else if(ppm <= 4) {
            iv_smoke.setImageResource(R.drawable.smoking2);
            iv_smoke2.setBackgroundResource(R.drawable.point_type_2);
        } else if(ppm <= 6) {
            iv_smoke.setImageResource(R.drawable.smoking3);
            iv_smoke3.setBackgroundResource(R.drawable.point_type_3);
        } else {
            iv_smoke.setImageResource(R.drawable.smoking4);
            iv_smoke4.setBackgroundResource(R.drawable.point_type_4);
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
                    tv_title.setText("ppm이란?");
                    tv_content.setText("parts per million 의 줄임말. 신선한 공기의 양에 대한 유해가스의 비율을 측정할 때 사용되는 부피 단위입니다.");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    //mPopup.showAsDropDown(tv_lastmonth);
                    break;

                case R.id.tv_help_right:
                    tv_title.setText("COHb(%)이란?");
                    tv_content.setText("Carboxy Hemoglobin의 줄임말. 혈류속의 일산화탄소 농도를 의미합니다.");

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    break;
            }
        }
    };
}