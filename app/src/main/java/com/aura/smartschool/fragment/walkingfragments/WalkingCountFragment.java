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
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.service.StepCounterService;
import com.aura.smartschool.utils.StepSharePrefrenceUtil;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-06-28.
 */
public class WalkingCountFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private TextView mTvWalkingCount, mTvCalories, mTvDistance, mTvWalkingTime;
    private TextView mTvTargetSelection, mTvTargetMeasure;
    private EditText mEtTargetNum;
    private Switch mSwTarget;

    private IBinder mBinder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mBinder = binder;
            ((StepCounterService.StepCounterBinder) binder).setSensorDelay(StepCounterService.SENSOR_DELAY_TYPE.WALKING_FRAGMENT_SHOW, mStepCounterHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBinder = null;
        }
    };

    //Message hander
    private final Handler mStepCounterHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            int steps = data.getInt("steps");
            int calories = data.getInt("calories");
            double distance = data.getDouble("distance");
            int totalWalkingTime = data.getInt("activeTime");

            mTvWalkingCount.setText(String.valueOf(steps));

            int hours = totalWalkingTime / 3600;
            int minutes = (totalWalkingTime % 3600) / 60;
            int seconds = totalWalkingTime % 60;

            mTvCalories.setText(calories + " kcal");
            mTvDistance.setText(String.format("%.2f Km", distance));
            mTvWalkingTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }
    };

    public static WalkingCountFragment newInstance(MemberVO member) {
        WalkingCountFragment instance = new WalkingCountFragment();
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
        mTvDistance = (TextView) view.findViewById(R.id.tv_distance);
        mTvWalkingTime = (TextView) view.findViewById(R.id.tv_walking_time);

        mTvTargetSelection = (TextView) view.findViewById(R.id.tv_target_selection);
        mTvTargetMeasure = (TextView) view.findViewById(R.id.tv_target_measure);
        mEtTargetNum = (EditText) view.findViewById(R.id.et_target_num);

        mTvTargetSelection.setOnClickListener(mClickListener);

        mSwTarget = (Switch) view.findViewById(R.id.sw_target);
        mSwTarget.setChecked(StepSharePrefrenceUtil.getNoticeOnOff(getActivity()));
        mSwTarget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StepSharePrefrenceUtil.saveNoticeOnOff(getActivity(), isChecked);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.actionbar_back, mMember.name);

        Intent serviceIntent = new Intent(getActivity(), StepCounterService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mBinder != null) {
            ((StepCounterService.StepCounterBinder) mBinder).setSensorDelay(StepCounterService.SENSOR_DELAY_TYPE.WALKING_FRAGMENT_DISAPPEAR, null);
        }
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinder = null;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_target_selection:
                    TargetSelectionDialogFragment dialog = new TargetSelectionDialogFragment();
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    dialog.show(getActivity().getSupportFragmentManager(), "selectionDialog");
                    break;
            }
        }
    };

    private class TargetSelectionDialogFragment extends DialogFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_activity_target_selection, container, false);

            return v;
        }
    }
}


