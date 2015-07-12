package com.aura.smartschool.fragment.walkingfragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.aura.smartschool.R;
import com.aura.smartschool.fragment.BaseFragment;
import com.aura.smartschool.utils.StepSharePrefrenceUtil;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-07-07.
 */
public class WalkingSettingFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private EditText mEtSteps;
    private CheckBox mCbSteps;

    private Runnable mTask;
    private Handler mHandler = new Handler();

    public static WalkingSettingFragment newInstance(MemberVO member) {
        WalkingSettingFragment instance = new WalkingSettingFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_walking_setting, null);
        mEtSteps = (EditText) view.findViewById(R.id.et_setting_steps);
        mCbSteps = (CheckBox) view.findViewById(R.id.cb_setting_steps);

        mCbSteps.setChecked(StepSharePrefrenceUtil.getTargetOnOff(getActivity()));
        mEtSteps.setEnabled(mCbSteps.isChecked());
        mEtSteps.setText(String.valueOf(StepSharePrefrenceUtil.getTargetSteps(getActivity())));

        mCbSteps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtSteps.setEnabled(isChecked);
                StepSharePrefrenceUtil.saveTargetOnOff(getActivity(), isChecked);
            }
        });

        mEtSteps.addTextChangedListener(mWatcher);
        return view;
    }

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            final int targetSteps = Integer.parseInt(s.toString());
            if (mTask != null) {
                mHandler.removeCallbacks(mTask);
            }

            if (!s.toString().isEmpty()) {
                mTask = new Runnable() {
                    @Override
                    public void run() {
                        saveTargetSteps(targetSteps);
                    }

                };

                mHandler.postDelayed(mTask, 500);
            }
        }
    };

    private void saveTargetSteps(int targetSteps) {
        if (targetSteps != StepSharePrefrenceUtil.getTargetSteps(getActivity())
                && targetSteps != 0) {
            StepSharePrefrenceUtil.saveTargetSteps(getActivity(), targetSteps);
            StepSharePrefrenceUtil.saveTodayAchieved(getActivity(), false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTask != null) {
            mHandler.removeCallbacks(mTask);
        }
        mEtSteps.removeTextChangedListener(mWatcher);
    }
}
