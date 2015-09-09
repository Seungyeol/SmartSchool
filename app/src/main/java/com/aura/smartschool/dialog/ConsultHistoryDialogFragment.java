package com.aura.smartschool.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015-08-17.
 */
public class ConsultHistoryDialogFragment extends DialogFragment {

    private MemberVO memberVO;

    private TextView tvNumIng;
    private TextView tvNumDone;
    private TextView tvNumTotal;

    public ConsultHistoryDialogFragment() {

    }

    @SuppressLint("ValidFragment")
    public ConsultHistoryDialogFragment(MemberVO memberVO) {
        this.memberVO = memberVO;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_consult_history, container, false);

        tvNumIng = (TextView) view.findViewById(R.id.tv_num_ing);
        tvNumDone = (TextView) view.findViewById(R.id.tv_num_done);
        tvNumTotal = (TextView) view.findViewById(R.id.tv_num_total);

        getConsultHistory();

        return view;
    }

    public void getConsultHistory() {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.HOST + Constant.API_GET_CONSULT_HISTORY;

            JSONObject json = new JSONObject();
            json.put("member_id", memberVO.member_id);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            new AQuery(getActivity()).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            SchoolLog.d("LDK", "FAIL");
                            Toast.makeText(getActivity(), "상담 히스토리를 불러오지 못하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            JSONObject data = object.getJSONObject("data");
                            int progress = data.getInt("progress");
                            int complete = data.getInt("complete");

                            tvNumIng.setText(String.valueOf(progress));
                            tvNumDone.setText(String.valueOf(complete));
                            tvNumTotal.setText(String.valueOf(progress + complete));
                        } else {
                            Toast.makeText(getActivity(), "상담 히스토리를 불러오지 못하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "상담 히스토리를 불러오지 못하였습니다.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
