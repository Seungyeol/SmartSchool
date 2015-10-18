package com.aura.smartschool.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;

/**
 * Created by Administrator on 2015-08-17.
 */
public class GuideAddMemberDialogFragment extends DialogFragment {

    private View mBtnAddMember;

    private View.OnClickListener mClick;

    public void setAddMemberClickListner(View.OnClickListener l) {
        mClick = l;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_guide_add_family_member, container, false);

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_orange_white);

        mBtnAddMember = view.findViewById(R.id.tvAddMember);
        if (mClick != null) {
            mBtnAddMember.setOnClickListener(mClick);
        }

        return view;
    }

}
