package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MeasureSummaryVO;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

public class HealthMainFragment extends Fragment {
	private View mView;
	private MemberVO mMember;
    private AQuery mAq;
    private MeasureSummaryVO mSummaryVO;

    private View rl_height, rl_growth, rl_dining, rl_pt;
    private View rl_weight, rl_muscle, rl_ranking, rl_brain;
    private View rl_bmi, rl_smoke, rl_activity, rl_magazine;
    private View rl_map, rl_consult, rl_noti, rl_challenge; //하단 4개 메뉴

    private ImageView iv_height, iv_growth;

    private TextView tv_height;
    private TextView tv_growth;
    private TextView tv_weight;
    private TextView tv_muscle;
    private TextView tv_bmi;
    private TextView tv_smoke;

    private ImageView iv_map, iv_consult, iv_noti, iv_challenge;

    private static String KEY_MEMBER = "member";

    private boolean mAnimationEnd = false;
    private float mHeight, mGrowth;

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

        rl_height = mView.findViewById(R.id.rl_height);
        rl_growth = mView.findViewById(R.id.rl_growth);
        rl_dining = mView.findViewById(R.id.rl_dining);
        rl_pt = mView.findViewById(R.id.rl_pt);

        rl_weight = mView.findViewById(R.id.rl_weight);
        rl_muscle = mView.findViewById(R.id.rl_muscle);
        rl_ranking = mView.findViewById(R.id.rl_ranking);
        rl_brain = mView.findViewById(R.id.rl_brain);

        rl_bmi = mView.findViewById(R.id.rl_bmi);
        rl_smoke = mView.findViewById(R.id.rl_smoke);
        rl_activity = mView.findViewById(R.id.rl_activity);
        rl_magazine = mView.findViewById(R.id.rl_magazine);

        rl_map = mView.findViewById(R.id.rl_map);
        rl_consult = mView.findViewById(R.id.rl_consult);
        rl_noti = mView.findViewById(R.id.rl_noti);
        rl_challenge = mView.findViewById(R.id.rl_challenge);

        iv_height = (ImageView) mView.findViewById(R.id.iv_height);
        iv_growth = (ImageView) mView.findViewById(R.id.iv_growth);

        tv_height = (TextView) mView.findViewById(R.id.tv_height);
        tv_growth = (TextView) mView.findViewById(R.id.tv_growth);
        tv_weight = (TextView) mView.findViewById(R.id.tv_weight);
        tv_muscle = (TextView) mView.findViewById(R.id.tv_muscle);
        tv_bmi = (TextView) mView.findViewById(R.id.tv_bmi);
        tv_smoke = (TextView) mView.findViewById(R.id.tv_smoke);

        iv_map = (ImageView) mView.findViewById(R.id.iv_map);
        iv_consult = (ImageView) mView.findViewById(R.id.iv_consult);
        iv_noti = (ImageView) mView.findViewById(R.id.iv_noti);
        iv_challenge = (ImageView) mView.findViewById(R.id.iv_challenge);

        //측정관련
        rl_height.setOnClickListener(mMeasureClick);
        rl_growth.setOnClickListener(mMeasureClick);
        rl_weight.setOnClickListener(mMeasureClick);
        rl_muscle.setOnClickListener(mMeasureClick);
        rl_bmi.setOnClickListener(mMeasureClick);
        rl_smoke.setOnClickListener(mMeasureClick);
        rl_pt.setOnClickListener(mMeasureClick);
        //활동량
        rl_activity.setOnClickListener(mActivityClick);

        //상단 우측 6개 메뉴
        rl_dining.setOnClickListener(mClick);
        rl_ranking.setOnClickListener(mClick);
        rl_brain.setOnClickListener(mClick);
        rl_magazine.setOnClickListener(mClick);
        //하단 4개 메뉴
        rl_map.setOnClickListener(mClick);
        rl_consult.setOnClickListener(mClick);
        rl_noti.setOnClickListener(mClick);
        rl_challenge.setOnClickListener(mClick);

        return mView;
	}

    @Override
    public void onResume() {
        super.onResume();
//        if (LoginManager.getInstance().getLoginUser().is_parent == 1) {
            ((MainActivity)getActivity()).setHeaderView(R.drawable.actionbar_back, mMember.name);
//        } else {
//            ((MainActivity)getActivity()).setHeaderView(R.drawable.home, mMember.name);
//        }

        if (!mAnimationEnd) {
            animateHealthMenu();
            getMeasureSummary();
        } else {
            displayData();
        }
    }

    private void animateHealthMenu() {
        Animation aniLeftToRight = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
        Animation aniTopToBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_bottom);
        Animation aniRightToLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
        Animation aniBottomToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_top);
        Animation aniTranslateAndBounce = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bounce);

        rl_height.startAnimation(aniLeftToRight);
        rl_growth.startAnimation(aniTopToBottom);
        rl_dining.startAnimation(aniRightToLeft);
        rl_pt.startAnimation(aniBottomToTop);
        rl_weight.startAnimation(aniRightToLeft);
        rl_muscle.startAnimation(aniTopToBottom);
        rl_smoke.startAnimation(aniRightToLeft);
        rl_brain.startAnimation(aniBottomToTop);
        rl_bmi.startAnimation(aniBottomToTop);
        //rl_ranking.startAnimation(aniRightToLeft);
        rl_activity.startAnimation(aniLeftToRight);
        iv_map.startAnimation(aniTranslateAndBounce);
        iv_consult.startAnimation(aniTranslateAndBounce);
        iv_noti.startAnimation(aniTranslateAndBounce);
        iv_challenge.startAnimation(aniTranslateAndBounce);

        aniLeftToRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("LDK", "onAnimationEnd");
                mAnimationEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void getMeasureSummary() {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.HOST + Constant.API_GET_MEASURESUMMARY;

            JSONObject json = new JSONObject();
            json.put("member_id", mMember.member_id);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();

                    try {
                        if (status.getCode() != 200) {
                            return;
                        }
                        Log.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            JSONObject json = object.getJSONObject("data");

                            mSummaryVO = new MeasureSummaryVO();
                            mSummaryVO.measure_date = json.getString("measure_date");
                            mSummaryVO.height = Float.parseFloat(json.getString("height"));
                            mSummaryVO.heightStatus = json.getString("heightStatus");
                            mSummaryVO.heightGradeId = json.getString("heightGradeId");
                            mSummaryVO.weight = json.getString("weight");
                            mSummaryVO.weightStatus = json.getString("weightStatus");
                            mSummaryVO.weightGradeId = json.getString("weightGradeId");
                            mSummaryVO.fat = json.getString("fat");

                            float muscle = Float.parseFloat(json.getString("muscle"));
                            float weight = Float.parseFloat(json.getString("weight"));
                            mSummaryVO.muscle_percent = (int) (muscle /weight * 100);

                            mSummaryVO.waist = json.getString("waist");
                            mSummaryVO.skeletal = json.getString("skeletal");//골격근량
                            mSummaryVO.weight_control = json.getString("weight_control");
                            mSummaryVO.fat_control = json.getString("fat_control");
                            mSummaryVO.muscle_control = json.getString("muscle_control");
                            mSummaryVO.bmi = json.getString("bmi");
                            mSummaryVO.bmiStatus = json.getString("bmiStatus");
                            mSummaryVO.bmiGradeId = json.getString("bmiGradeId");
                            mSummaryVO.ppm = json.getString("ppm");
                            mSummaryVO.cohd = json.getString("cohd");
                            mSummaryVO.smokeStatus = json.getString("smokeStatus");
                            mSummaryVO.growthGrade = Float.parseFloat(json.getString("growthGrade"));

                            mMember.mMeasureSummaryVO = mSummaryVO;

                            PreferenceUtil.getInstance(getActivity()).setWeight(Double.parseDouble(mSummaryVO.weight));
                            PreferenceUtil.getInstance(getActivity()).setHeight(mSummaryVO.height);

                            mHandler.sendEmptyMessage(MainActivity.MSG_CHECK_ANIMATION);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {

                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        if (mSummaryVO == null) return;

        iv_height.setPivotX(0.5f);
        iv_height.setPivotY(100f);
        iv_height.setScaleY(mHeight);
        iv_growth.setPivotX(0.0f);
        iv_growth.setPivotY(0.0f);
        iv_growth.setScaleX(mGrowth);

        //height animation
        mHandler.sendEmptyMessage(MainActivity.MSG_INCREASE_NUMBER);

        tv_weight.setText(String.format("%s", mSummaryVO.weight) + "kg");
        //tv_weight_status.setText(mSummaryVO.weightStatus);
        tv_bmi.setText(mSummaryVO.bmi);
        //tv_bmi_status.setText(mSummaryVO.bmiStatus);
        if(!mSummaryVO.smokeStatus.equals("null")) {
            tv_smoke.setText(mSummaryVO.smokeStatus);
        }
        //tv_smoke_cohb.setText(String.format("%s COHb", mSummaryVO.cohd));
        //tv_smoke_ppm.setText(String.format("%s ppm", mSummaryVO.ppm));
        tv_muscle.setText(String.valueOf(mSummaryVO.muscle_percent) + "%");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MainActivity.MSG_CHECK_ANIMATION:
                    if(mAnimationEnd) {
                        displayData();
                    } else {
                        mHandler.sendEmptyMessageDelayed(0, 100);
                    }
                    break;
                case MainActivity.MSG_INCREASE_NUMBER:
                    //1초 애니메이션, 20ms 간격 총 50번
                    if(mHeight < mSummaryVO.height) {
                        mHeight += mSummaryVO.height * 0.02;
                        mGrowth += mSummaryVO.growthGrade * 0.02;
                        mHandler.sendEmptyMessageDelayed(MainActivity.MSG_INCREASE_NUMBER, 20);
                    } else {
                        mHeight = mSummaryVO.height;
                        mGrowth = mSummaryVO.growthGrade;
                    }
                    tv_height.setText(String.format("%.1f", mHeight));
                    iv_height.setScaleY(mHeight / mSummaryVO.height);
                    tv_growth.setText(String.format("%.0f", mGrowth));
                    iv_growth.setScaleX(mGrowth / mSummaryVO.growthGrade);
                    break;
            }
        }
    };

    View.OnClickListener mMeasureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mSummaryVO == null) {
                if(mMember.isVIPUser()) {
                    Util.showAlertDialog(getActivity(), getString(R.string.popup_alert_vip_nodata));
                } else {
                    Util.showAlertDialog(getActivity(), getString(R.string.popup_alert_nodata));
                }
                return;
            }
            switch (v.getId()) {
                case R.id.rl_height:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, HeightFragment.newInstance(mMember, 1)).addToBackStack(null).commit();
                    break;

                case R.id.rl_weight:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, HeightFragment.newInstance(mMember, 2)).addToBackStack(null).commit();
                    break;

                case R.id.rl_smoke:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, SmokeFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;

                case R.id.rl_bmi:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, BmiFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;

                case R.id.rl_muscle:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, MuscleFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;

                case R.id.rl_growth:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, GrowthFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;

                case R.id.rl_pt:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, VideoFragment.newInstance(mMember, 1)).addToBackStack(null).commit();
                    break;

            }
        }
    };

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_dining:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, MealListFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;
                case R.id.rl_ranking:
                    Util.showAlertDialog(getActivity(), "업데이트 예정");
                    break;
                case R.id.rl_brain:
                    Util.showAlertDialog(getActivity(), "업데이트 예정");
                    break;

                case R.id.rl_magazine:
                    Util.showAlertDialog(getActivity(), "업데이트 예정");
                    break;
                case R.id.rl_map:
                    if(mMember.lat != 0) {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, LocationFragment.newInstance(mMember)).addToBackStack(null).commit();
                    } else {
                        Util.showToast(getActivity(), "위치정보가 없습니다");
                    }
                    break;
                case R.id.rl_consult:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, ConsultMainFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;
                case R.id.rl_noti:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, SchoolNoticePagerFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;
                case R.id.rl_challenge:
                    Util.showAlertDialog(getActivity(), "업데이트 예정");
                    break;

            }
        }
    };

    View.OnClickListener mActivityClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mMember.isVIPUser()) {
                Util.showAlertDialog(getActivity(), getString(R.string.popup_alert_nodata));
                return;
            }

            switch(v.getId()) {
                case R.id.rl_activity:
                    Log.d("test", "onClick >> rl_activity");
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, WalkingPagerFragment.newInstance(mMember)).addToBackStack(null).commit();
                    break;
            }
        }
    };
}