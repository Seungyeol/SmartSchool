package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

public class HealthMainFragment extends BaseFragment {
	private View mView;
	private MemberVO mMember;
    private AQuery mAq;

    private RelativeLayout rl_height, rl_growth, rl_dining, rl_pt;
    private RelativeLayout rl_weight, rl_fat, rl_smoke, rl_mental;
    private RelativeLayout rl_bmi, rl_ranking, rl_activity;
    private RelativeLayout rl_map, rl_consult, rl_noti, rl_challenge;

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
        rl_growth = (RelativeLayout) mView.findViewById(R.id.rl_growth);
        rl_dining = (RelativeLayout) mView.findViewById(R.id.rl_dining);
        rl_pt = (RelativeLayout) mView.findViewById(R.id.rl_pt);

        rl_weight = (RelativeLayout) mView.findViewById(R.id.rl_weight);
        rl_fat = (RelativeLayout) mView.findViewById(R.id.rl_fat);
        rl_smoke = (RelativeLayout) mView.findViewById(R.id.rl_smoke);
        rl_mental = (RelativeLayout) mView.findViewById(R.id.rl_mental);

        rl_bmi = (RelativeLayout) mView.findViewById(R.id.rl_bmi);
        rl_ranking = (RelativeLayout) mView.findViewById(R.id.rl_ranking);
        rl_activity = (RelativeLayout) mView.findViewById(R.id.rl_activity);

        rl_map = (RelativeLayout) mView.findViewById(R.id.rl_map);
        rl_consult = (RelativeLayout) mView.findViewById(R.id.rl_consult);
        rl_noti = (RelativeLayout) mView.findViewById(R.id.rl_noti);
        rl_challenge = (RelativeLayout) mView.findViewById(R.id.rl_challenge);

        rl_activity.setOnClickListener(mClick);
        rl_map.setOnClickListener(mClick);

        animateHealthMenu();

        return mView;
	}

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
    }

    private void animateHealthMenu() {
        Animation aniLeftToRight = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
        Animation aniTopToBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_bottom);
        Animation aniRightToLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
        Animation aniBottomToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_top);

        rl_height.startAnimation(aniLeftToRight);
        rl_growth.startAnimation(aniTopToBottom);
        rl_dining.startAnimation(aniRightToLeft);
        rl_pt.startAnimation(aniBottomToTop);
        rl_weight.startAnimation(aniRightToLeft);
        rl_fat.startAnimation(aniTopToBottom);
        rl_smoke.startAnimation(aniRightToLeft);
        rl_mental.startAnimation(aniBottomToTop);
        rl_bmi.startAnimation(aniBottomToTop);
        rl_ranking.startAnimation(aniRightToLeft);
        rl_activity.startAnimation(aniLeftToRight);


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

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_activity:
                    Log.d("test", "onClick >> rl_activity");
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, WalkingPagerFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;

                case R.id.rl_map:
                    if(mMember.lat != 0) {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, LocationFragment.newInstance(mMember)).addToBackStack(null).commit();
                    } else {
                        Util.showToast(getActivity(), "위치정보가 없습니다");
                    }
                    break;
            }
        }
    };
}
