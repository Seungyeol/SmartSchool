package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

public class HealthMainFragment extends BaseFragment implements View.OnClickListener {
	private View mView;
	private MemberVO mMember;
    private AQuery mAq;

    private RelativeLayout rl_height, rl_weight, rl_bmi, rl_smoke, rl_fat, rl_ranking, rl_growth, rl_dining, rl_activity, rl_pt;
    private ImageView iv_pin, iv_email;
    private RelativeLayout rl_map, rl_school_info;

    private static String KEY_MEMBER = "member";

    public static HealthMainFragment newInstance(MemberVO member) {

        HealthMainFragment instance = new HealthMainFragment();

        Bundle args = new Bundle();
        args.putSerializable(KEY_MEMBER, member);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_main, null);
        mAq = new AQuery(mView);

        rl_height = (RelativeLayout) mView.findViewById(R.id.rl_height);
        rl_weight = (RelativeLayout) mView.findViewById(R.id.rl_weight);
        rl_bmi = (RelativeLayout) mView.findViewById(R.id.rl_bmi);
        rl_smoke = (RelativeLayout) mView.findViewById(R.id.rl_smoke);
        rl_fat = (RelativeLayout) mView.findViewById(R.id.rl_fat);
        rl_ranking = (RelativeLayout) mView.findViewById(R.id.rl_ranking);
        rl_growth = (RelativeLayout) mView.findViewById(R.id.rl_growth);
        rl_dining = (RelativeLayout) mView.findViewById(R.id.rl_dining);
        rl_activity = (RelativeLayout) mView.findViewById(R.id.rl_activity);
        rl_pt = (RelativeLayout) mView.findViewById(R.id.rl_pt);
        iv_pin = (ImageView) mView.findViewById(R.id.iv_pin);
        iv_email = (ImageView) mView.findViewById(R.id.iv_email);


        rl_activity.setOnClickListener(this);
        animateHealthMenu();

        rl_map = (RelativeLayout) mView.findViewById(R.id.rl_map);
        rl_school_info = (RelativeLayout) mView.findViewById(R.id.rl_school_info);
        rl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMember.lat != 0) {
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, LocationFragment.newInstance(mMember)).addToBackStack(null).commit();
                } else {
                    Util.showToast(getActivity(), "위치정보가 없습니다");
                }
            }
        });

        return mView;
	}

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
    }

    private void animateHealthMenu() {
        Animation aniLeftToRight = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
        rl_height.startAnimation(aniLeftToRight);
        rl_ranking.startAnimation(aniLeftToRight);
        rl_smoke.startAnimation(aniLeftToRight);
        Animation aniTopToBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_bottom);
        rl_weight.startAnimation(aniTopToBottom);
        rl_bmi.startAnimation(aniTopToBottom);
        Animation aniRightToLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
        rl_fat.startAnimation(aniRightToLeft);
        rl_dining.startAnimation(aniRightToLeft);
        rl_pt.startAnimation(aniRightToLeft);
        Animation aniBottomToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_top);
        rl_activity.startAnimation(aniBottomToTop);
        rl_growth.startAnimation(aniBottomToTop);

        Animation aniTranslate = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bounce);
        iv_pin.startAnimation(aniTranslate);

        Animation aniZoomIn = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_zoom_in);
        iv_email.startAnimation(aniZoomIn);

        /*aniZoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //애니메이션 종료후 위치 정보 가져오기
                Log.d("LDK", "animation end ");
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.rl_activity:
                Log.d("test", "onClick >> rl_activity");
                getFragmentManager().beginTransaction().replace(R.id.content_frame, WalkingPagerFragment.newInstance(mMember)).addToBackStack(null).commit();
                break;
        }
    }
}
