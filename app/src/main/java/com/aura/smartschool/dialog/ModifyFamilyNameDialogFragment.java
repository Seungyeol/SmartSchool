package com.aura.smartschool.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015-08-17.
 */
public class ModifyFamilyNameDialogFragment extends DialogFragment {

    private EditText etNewName;
    private View btnOk;
    private View btnCancel;

    private OnModifyListener listener;

    public interface OnModifyListener{
        void onModify(String newName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_modify_family, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_orange_white);

        etNewName = (EditText) view.findViewById(R.id.tv_new_family_name);
        btnOk = view.findViewById(R.id.btn_ok);
        btnCancel = view.findViewById(R.id.btn_cancel);

        etNewName.setText(PreferenceUtil.getInstance(view.getContext()).getHomeId());

        btnOk.setOnClickListener(clickListener);
        btnCancel.setOnClickListener(clickListener);

        return view;
    }

    public void setModifyListener(OnModifyListener listener) {
        this.listener = listener;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_ok:
                    if (listener != null) {
                        String newName = etNewName.getText().toString();
                        if(TextUtils.isEmpty(newName)) {
                            Util.showToast(v.getContext(), "가족명을 입력하세요.");
                            return;
                        }
                        if (!Pattern.matches("^[a-zA-Z0-9가-힣ㄱ-ㅎ]*$", newName)) {
                            Util.showToast(v.getContext(), "가족명은 한글, 영문, 숫자만 입력 가능합니다.");
                            return;
                        }
                        dismiss();
                        listener.onModify(newName);
                    }
                    break;
                case R.id.btn_cancel:
                    dismiss();
                    break;
            }
        }
    };


        /*

     */
}
