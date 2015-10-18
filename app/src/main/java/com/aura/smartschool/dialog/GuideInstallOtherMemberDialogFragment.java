package com.aura.smartschool.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;

/**
 * Created by Administrator on 2015-08-17.
 */
public class GuideInstallOtherMemberDialogFragment extends DialogFragment {

    private CheckBox mCbNeverShow;
    private View mBtnOk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_guide_install_other_member, container, false);

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_orange_white);

        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mCbNeverShow = (CheckBox) v.findViewById(R.id.cb_never_show);
        mBtnOk = v.findViewById(R.id.tv_ok);

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbNeverShow.isChecked()) {
                    PreferenceUtil.getInstance(v.getContext()).setInstallMemberEnable(false);
                }
                dismiss();
            }
        });
    }

}
