package com.aura.smartschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aura.smartschool.dialog.GuardianInputDialogFragment;
import com.aura.smartschool.service.SOSIconService;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2015-10-17.
 */
public class SOSSettingActivity extends FragmentActivity {
    private View btnLogo;

    private TextView mTvGuardianA;
    private TextView mTvGuardianB;
    private TextView mTvGuardianC;

    private View mBtnRemoveA;
    private View mBtnRemoveB;
    private View mBtnRemoveC;

    private EditText mEtSOSMessage;

    private View mBtnCancel;
    private View mBtnConfirm;

    private String[] mGuardianNames = new String[3];
    private String[] mGuardianPhoneNumbers = new String[3];

    private boolean isModified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_setting);

        btnLogo = findViewById(R.id.logo);
        btnLogo.setOnClickListener(mBtnClick);

        initGuardianData();
        initView();
    }

    private void initGuardianData() {
        for (int i = 0; i < mGuardianNames.length; i++) {
            String[] info = PreferenceUtil.getInstance(SOSSettingActivity.this).getGuardianInfo(i);
            mGuardianNames[i] = info[0];
            mGuardianPhoneNumbers[i] = info[1];
        }
    }

    private void initView() {
        mTvGuardianA = (TextView) findViewById(R.id.tv_guardian_tel_a);
        mTvGuardianB = (TextView) findViewById(R.id.tv_guardian_tel_b);
        mTvGuardianC = (TextView) findViewById(R.id.tv_guardian_tel_c);

        mBtnRemoveA = findViewById(R.id.btn_remove_a);
        mBtnRemoveB = findViewById(R.id.btn_remove_b);
        mBtnRemoveC = findViewById(R.id.btn_remove_c);

        mBtnRemoveA.setOnClickListener(mBtnClick);
        mBtnRemoveB.setOnClickListener(mBtnClick);
        mBtnRemoveC.setOnClickListener(mBtnClick);

        setGuardianInfoViews(0, mTvGuardianA, mBtnRemoveA);
        setGuardianInfoViews(1, mTvGuardianB, mBtnRemoveB);
        setGuardianInfoViews(2, mTvGuardianC, mBtnRemoveC);

        mTvGuardianA.setOnClickListener(mGuardianTextClick);
        mTvGuardianB.setOnClickListener(mGuardianTextClick);
        mTvGuardianC.setOnClickListener(mGuardianTextClick);

        mEtSOSMessage = (EditText) findViewById(R.id.et_sos_message);
        mEtSOSMessage.setText(PreferenceUtil.getInstance(SOSSettingActivity.this).getSOSMessage());
        mEtSOSMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isModified = true;
            }
        });

        mBtnCancel = findViewById(R.id.tv_cancel);
        mBtnConfirm = findViewById(R.id.tv_confirm);

        mBtnCancel.setOnClickListener(mBtnClick);
        mBtnConfirm.setOnClickListener(mBtnClick);

    }

    @Override
    public void onBackPressed() {
        checkEditingBeforeFinish();
    }

    private void setGuardianInfoViews(int idx, TextView tv, View btnView) {
        if (!StringUtils.isEmpty(mGuardianNames[idx]) &&
                !StringUtils.isEmpty(mGuardianPhoneNumbers[idx])) {
            tv.setText(mGuardianNames[idx] + " - " + mGuardianPhoneNumbers[idx]);
            btnView.setVisibility(View.VISIBLE);
        } else {
            tv.setText("");
            btnView.setVisibility(View.GONE);
        }
    }

    View.OnClickListener mGuardianTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            GuardianInputDialogFragment mGuardianDialog = new GuardianInputDialogFragment();
            mGuardianDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            mGuardianDialog.setOnGuardianChangedListener(mGuardianGhangedListener);
            int idx = -1;
            switch (id) {
                case R.id.tv_guardian_tel_a:
                    idx = 0;
                    break;
                case R.id.tv_guardian_tel_b:
                    idx = 1;
                    break;
                case R.id.tv_guardian_tel_c:
                    idx = 2;
                    break;
            }
            if (idx != -1) {
                mGuardianDialog.setGuardianInfo(idx, mGuardianNames[idx], mGuardianPhoneNumbers[idx]);
                mGuardianDialog.show(getSupportFragmentManager(), "guardianDialog");
            }
        }
    };

    GuardianInputDialogFragment.OnGuardianChangedListener mGuardianGhangedListener = new GuardianInputDialogFragment.OnGuardianChangedListener() {
        @Override
        public void onGuardianChanged(int idx, String name, String phoneNum) {
            isModified = true;

            mGuardianNames[idx] = name;
            mGuardianPhoneNumbers[idx] = phoneNum;
            if (idx == 0) {
                setGuardianInfoViews(0, mTvGuardianA, mBtnRemoveA);
            } else if (idx == 1) {
                setGuardianInfoViews(1, mTvGuardianB, mBtnRemoveB);
            } else if (idx == 2) {
                setGuardianInfoViews(2, mTvGuardianC, mBtnRemoveC);
            }
        }
    };

    View.OnClickListener mBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_remove_a:
                    showRemoveConfirmDialog(0);
                    break;
                case R.id.btn_remove_b:
                    showRemoveConfirmDialog(1);
                    break;
                case R.id.btn_remove_c:
                    showRemoveConfirmDialog(2);
                    break;
                case R.id.tv_cancel:
                case R.id.logo:
                    checkEditingBeforeFinish();
                    break;
                case R.id.tv_confirm:
                    if (saveSOSInfo()) {
                        Util.showToast(SOSSettingActivity.this, "SOS 정보가 저장되었습니다.");

                        Intent sosIntent = new Intent(SOSSettingActivity.this, SOSIconService.class);
                        startService(sosIntent);

                        SOSSettingActivity.this.finish();
                    }
                    break;
            }
        }

        private void showRemoveConfirmDialog(final int idx) {
            Util.showConfirmDialog(SOSSettingActivity.this, "삭제하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isModified = true;
                    mGuardianNames[idx] = "";
                    mGuardianPhoneNumbers[idx] = "";
                    if (idx == 0) {
                        setGuardianInfoViews(0, mTvGuardianA, mBtnRemoveA);
                    } else if (idx == 1) {
                        setGuardianInfoViews(1, mTvGuardianB, mBtnRemoveB);
                    } else if (idx == 2) {
                        setGuardianInfoViews(2, mTvGuardianC, mBtnRemoveC);
                    }
                    dialog.dismiss();
                }
            });
        }
    };

    private void checkEditingBeforeFinish() {
        if (isModified) {
            Util.showConfirmDialog(SOSSettingActivity.this, "취소하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SOSSettingActivity.this.finish();
                }
            });
        } else {
            if (isGuardianNumberEmpty()) {
                Util.showToast(SOSSettingActivity.this, "SOS 서비스가 실행되지 않습니다.");
                PreferenceUtil.getInstance(SOSSettingActivity.this).setSOSEnabled(false);
            }
            SOSSettingActivity.this.finish();
        }
    }

    private boolean isGuardianNumberEmpty() {
        for (int i = 0; i < mGuardianPhoneNumbers.length; i++) {
            if (!StringUtils.isEmpty(mGuardianPhoneNumbers[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean saveSOSInfo() {
        if (!saveGuardians()) {
            Util.showToast(SOSSettingActivity.this, "최소 한명 이상의 보호자 연락처를 입력해주세요.");
            return false;
        };
        if (!saveSOSMessage()) {
            Util.showToast(SOSSettingActivity.this, "SOS 메시지를 입력해주세요.");
            return false;
        };
        return true;
    }

    private boolean saveSOSMessage() {
        if (StringUtils.isBlank(mEtSOSMessage.getText())) {
            return false;
        } else {
            PreferenceUtil.getInstance(SOSSettingActivity.this).putSOSMessage(mEtSOSMessage.getText().toString().trim());
            return true;
        }
    }

    private boolean saveGuardians() {
        if (isGuardianNumberEmpty()) {
            return false;
        }
        for (int i = 0; i < mGuardianNames.length; i++) {
            PreferenceUtil.getInstance(SOSSettingActivity.this).putGuardianInfo(i, mGuardianNames[i], mGuardianPhoneNumbers[i]);
        }

        return true;
    }
}
