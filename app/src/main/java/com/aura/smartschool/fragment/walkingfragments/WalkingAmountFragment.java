package com.aura.smartschool.fragment.walkingfragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.service.StepCounterService;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-06-28.
 */
public class WalkingAmountFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private static float ACTIVITY_COEFFICIENT = 1.55F;

    private TextView mTvWalkingCount;
    private TextView mTvCalories;
    private TextView mTvWalkingTime;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            ((StepCounterService.StepCounterBinder) binder).setStepCounterHandler(mStepCounterHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    //Message hander
    private final Handler mStepCounterHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
//            int steps = message.arg1;
//            int bigSteps = message.arg2;

            Bundle data = message.getData();
            int steps = data.getInt("steps");

            mTvWalkingCount.setText(String.valueOf(steps));


            //Change mode walking or running
//            if(bigSteps >= (int)(steps/2)){
//                ACTIVITY_COEFFICIENT = 1.725f;
//            }else{
//                ACTIVITY_COEFFICIENT = 1.55f;
//            }
        }
    };

    public static WalkingAmountFragment newInstance(MemberVO member) {

        WalkingAmountFragment instance = new WalkingAmountFragment();

        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_walking_count, null);
        mTvWalkingCount = (TextView) view.findViewById(R.id.tv_walking_count);
        mTvCalories = (TextView) view.findViewById(R.id.tv_calories);
        mTvWalkingTime = (TextView) view.findViewById(R.id.tv_walking_time);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);

        Intent serviceIntent = new Intent(getActivity(), StepCounterService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /*
                        if(gender == 0){
                    calories += (int)(((((655+(9.6*weight)+(1.8*height)-(4.7*age))*ACTIVITY_COEFFICIENT)/24)/3600)
                    					*(second));
                    }//men
                    else{
                    calories += (int)(((((66+(13.7*weight)+(5*height)-(6.8*age))*ACTIVITY_COEFFICIENT)/24)/3600)
        								*(second));
                    }
     */
}


