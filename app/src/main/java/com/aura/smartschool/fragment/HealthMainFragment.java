package com.aura.smartschool.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HealthMainFragment extends BaseFragment {
	private View mView;
	private MemberVO mMember;
    private AQuery mAq;

    private RelativeLayout rl_height, rl_weight, rl_bmi, rl_smoke, rl_fat, rl_ranking, rl_growth, rl_dining, rl_activity, rl_pt;
    private ImageView iv_pin, iv_email;
    private TextView tv_current_location;
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
        tv_current_location = (TextView) mView.findViewById(R.id.tv_current_location);

        animateHealthMenu();

        rl_map = (RelativeLayout) mView.findViewById(R.id.rl_map);
        rl_school_info = (RelativeLayout) mView.findViewById(R.id.rl_school_info);
        rl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, LocationFragment.newInstance(mMember)).addToBackStack(null).commit();
            }
        });

        return mView;
	}

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
        getCurrentLocation();
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

    private void getCurrentLocation() {
        try {
            String url = Constant.HOST + Constant.API_GET_LASTLOCATION;

            JSONObject json = new JSONObject();
            json.put("member_id", mMember.member_id);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        if (status.getCode() != 200) {
                            return;
                        }
                        Log.d("LDK", "result:" + object.toString(1));

                        if ("0".equals(object.getString("result"))) {
                            JSONObject data = object.getJSONObject("data");
                            if(data != null) {
                                mMember.lat = Double.parseDouble(data.getString("lat"));
                                mMember.lng = Double.parseDouble(data.getString("lng"));
                            }
                            //애니메이션 끝나고 UI 작업을 해야 애니메이션이 안끈김
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    displayLocation();
                                }
                            }, 1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayLocation() {
        Log.d("LDK", "location: " + mMember.lat + "," + mMember.lng);
        List<Address> addresses = null;
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(mMember.lat, mMember.lng, 1);
        } catch (IOException ioException) {
            Log.d("LDK", ioException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.d("LDK", illegalArgumentException.getMessage());
        } catch (NullPointerException e) {
        }

        if(addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0);
            if(address.startsWith("한국")) {
                address = address.substring(3);
            }
            Log.d("LDK", "address:" + address);
            tv_current_location.setText(address);
        }
    }

}
