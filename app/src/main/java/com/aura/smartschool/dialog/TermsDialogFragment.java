package com.aura.smartschool.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aura.smartschool.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015-08-17.
 */
public class TermsDialogFragment extends DialogFragment {

    private CheckBox cbTerms;
    private CheckBox cbPrivacy;
    private CheckBox cbLocation;
    private CheckBox cbAll;

    private TextView tvTerms;
    private TextView tvPrivacy;
    private TextView tvLocation;

    private View btnOk;

    private View.OnClickListener btnOkListener;
    private boolean isVisible = true;

    public TermsDialogFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_terms, container, false);

        cbTerms = (CheckBox) view.findViewById(R.id.cb_agree_terms);
        cbPrivacy = (CheckBox) view.findViewById(R.id.cb_agree_privacy);
        cbLocation = (CheckBox) view.findViewById(R.id.cb_agree_location);
        cbAll = (CheckBox) view.findViewById(R.id.cb_agree_all);

        tvTerms = (TextView) view.findViewById(R.id.tv_terms);
        tvPrivacy = (TextView) view.findViewById(R.id.tv_privacy);
        tvLocation = (TextView) view.findViewById(R.id.tv_location);

        btnOk = view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(btnOkListener);

        view.findViewById(R.id.tv_agree_term).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.tv_agree_privacy).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.tv_agree_location).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.tv_agree_all).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        cbTerms.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        cbPrivacy.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        cbLocation.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        cbAll.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        btnOk.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        cbTerms.setOnCheckedChangeListener(mCheckedChangedListener);
        cbPrivacy.setOnCheckedChangeListener(mCheckedChangedListener);
        cbLocation.setOnCheckedChangeListener(mCheckedChangedListener);
        cbAll.setOnCheckedChangeListener(mCheckedChangedListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRawTerms(tvTerms, R.raw.terms);
        loadRawTerms(tvPrivacy, R.raw.privacy);
        loadRawTerms(tvLocation, R.raw.location);
    }

    public void setBtnOkListener(View.OnClickListener listener) {
        this.btnOkListener = listener;
    }

    public void setButtonVisible(boolean btnVisible) {
        this.isVisible = btnVisible;
    }

    private void loadRawTerms (final TextView tv, final int rawId){
        getView().post(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = getResources().openRawResource(rawId);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int i;
                try {
                    i = inputStream.read();
                    while (i != -1)
                    {
                        byteArrayOutputStream.write(i);
                        i = inputStream.read();
                    }
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                tv.setText(byteArrayOutputStream.toString());
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.cb_agree_all) {
                if (isChecked) {
                    cbTerms.setChecked(isChecked);
                    cbPrivacy.setChecked(isChecked);
                    cbLocation.setChecked(isChecked);
                }
                btnOk.setEnabled(isChecked);
            } else {
                cbAll.setChecked(cbTerms.isChecked() && cbPrivacy.isChecked() && cbLocation.isChecked());
            }
        }
    };
}
