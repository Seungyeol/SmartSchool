package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class SmokeFragment extends BaseFragment {

    private View mView;

    private MemberVO mMember;

    private ImageView iv_smoke, iv_smoke1, iv_smoke2, iv_smoke3, iv_smoke4;
    private TextView tv_ppm, tv_cohb;

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
            iv_smoke.setBackgroundResource(R.drawable.smoking1);
            iv_smoke1.setBackgroundResource(R.drawable.point_type_1);
        } else if(ppm <= 4) {
            iv_smoke.setBackgroundResource(R.drawable.smoking2);
            iv_smoke2.setBackgroundResource(R.drawable.point_type_2);
        } else if(ppm <= 6) {
            iv_smoke.setBackgroundResource(R.drawable.smoking3);
            iv_smoke3.setBackgroundResource(R.drawable.point_type_3);
        } else {
            iv_smoke.setBackgroundResource(R.drawable.smoking4);
            iv_smoke4.setBackgroundResource(R.drawable.point_type_4);
        }
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