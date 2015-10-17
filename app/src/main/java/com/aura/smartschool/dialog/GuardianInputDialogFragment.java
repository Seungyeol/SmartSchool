package com.aura.smartschool.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2015-08-17.
 */
public class GuardianInputDialogFragment extends DialogFragment {

    private EditText mEtGuardianName;
    private EditText mEtGuardianPhoneNum;

    private View mBtnCancel;
    private View mBtnOk;

    private int mIdx;
    private String mName;
    private String mPhoneNum;

    private OnGuardianChangedListener mListener;

    public interface OnGuardianChangedListener {
        void onGuardianChanged(int idx, String name, String phoneNum);
    }

    public void setGuardianInfo(int idx, String name, String phoneNum) {
        mIdx = idx;
        mName = name;
        mPhoneNum = phoneNum;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_input_guardians, container, false);

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_orange_white);

        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mEtGuardianName = (EditText) v.findViewById(R.id.et_guardian_name);
        mEtGuardianPhoneNum = (EditText) v.findViewById(R.id.et_guardian_phone_num);

        mBtnCancel = v.findViewById(R.id.tv_cancel);
        mBtnOk = v.findViewById(R.id.tv_ok);

        mBtnCancel.setOnClickListener(mClick);
        mBtnOk.setOnClickListener(mClick);

        if (!StringUtils.isEmpty(mName)) {
            mEtGuardianName.setText(mName);
        }
        if (!StringUtils.isEmpty(mPhoneNum)) {
            mEtGuardianPhoneNum.setText(mPhoneNum);
        }
    }

    public void setOnGuardianChangedListener(OnGuardianChangedListener l) {
        mListener = l;
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBtnCancel == v) {
                dismiss();
            } else if (mBtnOk == v) {
                if (StringUtils.isBlank(mEtGuardianName.getText())) {
                    Util.showToast(v.getContext(), "이름을 입력해주세요.");
                    return;
                }
                if (StringUtils.isBlank(mEtGuardianPhoneNum.getText())) {
                    Util.showToast(v.getContext(), "전화번호를 입력해주세요.");
                    return;
                }
                if (mListener != null) {
                    mListener.onGuardianChanged(mIdx,
                            mEtGuardianName.getText().toString().trim(),
                            mEtGuardianPhoneNum.getText().toString().trim());
                }
                dismiss();
            }
        }
    };
}
