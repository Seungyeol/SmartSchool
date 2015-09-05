package com.aura.smartschool.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aura.smartschool.R;

/**
 * Created by Administrator on 2015-08-17.
 */
public class AppraisalDialogFragment extends DialogFragment {

    private View vStar1;
    private View vStar2;
    private View vStar3;
    private View vStar4;
    private View vStar5;
    private TextView tvAppraisal;
    private View btnSendAppraisal;

    private int appraisalNum = 1;

    private OnAppraisalSelectedListener listener;

    public interface OnAppraisalSelectedListener{
        void onApprisalSelected(int num);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_appraisal, container, false);

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_orange_white);

        vStar1 = view.findViewById(R.id.v_star1);
        vStar2 = view.findViewById(R.id.v_star2);
        vStar3 = view.findViewById(R.id.v_star3);
        vStar4 = view.findViewById(R.id.v_star4);
        vStar5 = view.findViewById(R.id.v_star5);
        tvAppraisal = (TextView) view.findViewById(R.id.tv_appraisal_text);
        btnSendAppraisal = view.findViewById(R.id.btn_send_appraisal);

        vStar1.setOnClickListener(clickListener);
        vStar2.setOnClickListener(clickListener);
        vStar3.setOnClickListener(clickListener);
        vStar4.setOnClickListener(clickListener);
        vStar5.setOnClickListener(clickListener);
        btnSendAppraisal.setOnClickListener(clickListener);

        setStartSelected(3);

        return view;
    }

    public void setAppraisalSelectedListener(OnAppraisalSelectedListener listener) {
        this.listener = listener;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.v_star1:
                    tvAppraisal.setText("- 완전 불만족해요 -");
                    setStartSelected(1);
                    break;
                case R.id.v_star2:
                    tvAppraisal.setText("- 불만족해요 -");
                    setStartSelected(2);
                    break;
                case R.id.v_star3:
                    tvAppraisal.setText("- 보통이예요 -");
                    setStartSelected(3);
                    break;
                case R.id.v_star4:
                    tvAppraisal.setText("- 만족했어요 -");
                    setStartSelected(4);
                    break;
                case R.id.v_star5:
                    tvAppraisal.setText("- 완전 만족했어요 -");
                    setStartSelected(5);
                    break;
                case R.id.btn_send_appraisal:
                    dismiss();
                    if (listener != null) {
                        listener.onApprisalSelected(appraisalNum);
                    }
                    break;
            }
        }
    };

    private void setStartSelected(int appraisalNum) {
        this.appraisalNum = appraisalNum;
        vStar5.setSelected(false);
        vStar4.setSelected(false);
        vStar3.setSelected(false);
        vStar2.setSelected(false);
        vStar1.setSelected(false);
        switch (appraisalNum) {
            case 5:
                vStar5.setSelected(true);
            case 4:
                vStar4.setSelected(true);
            case 3:
                vStar3.setSelected(true);
            case 2:
                vStar2.setSelected(true);
            case 1:
                vStar1.setSelected(true);
        }
    }
}
