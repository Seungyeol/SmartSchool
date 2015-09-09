package com.aura.smartschool.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.QnAActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolLog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SeungyeolBak on 15. 8. 14..
 */
public class QnASendFragment extends Fragment {

    EditText etTitle;
    EditText etContent;
    View btnSendQnA;

    public QnASendFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((QnAActivity) activity).setBackKeyListener(backPressListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((QnAActivity) getActivity()).setBackKeyListener(null);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_qna, container, false);

        etTitle = (EditText) v.findViewById(R.id.et_title);
        etContent = (EditText) v.findViewById(R.id.et_content);
        btnSendQnA = v.findViewById(R.id.btn_send_qna);

        btnSendQnA.setOnClickListener(sendClicked);
        return v;
    }

    QnAActivity.OnBackPressListener backPressListener = new QnAActivity.OnBackPressListener() {
        @Override
        public void onBackPressed() {
            if (StringUtils.isBlank(etTitle.getText()) && StringUtils.isBlank(etContent.getText())) {
                getFragmentManager().popBackStack();
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setMessage("작성된 내용은 저장되지 않습니다.\n취소 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getFragmentManager().popBackStack();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("취소", null);
                dialogBuilder.show();
            }
        }
    };

    View.OnClickListener sendClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (StringUtils.isBlank(etTitle.getText())) {
                Toast.makeText(v.getContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (StringUtils.isBlank(etContent.getText())) {
                Toast.makeText(v.getContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            doSendQnA();
        }
    };

    private void doSendQnA() {
        LoadingDialog.showLoading(getActivity());
        String url = Constant.HOST + Constant.API_ADD_BOARD;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("member_id", LoginManager.getInstance().getLoginUser().member_id);
            jsonObject.put("board_type", 1);
            jsonObject.put("title", etTitle.getText());
            jsonObject.put("content", etContent.getText());
            new AQuery(getActivity()).post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            SchoolLog.d("test", "실패 ");
                            showToast("질문 등록에 실패하였습니다.");
                            return;
                        }

                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if ("0".equals(object.getString("result"))) {
                            showToast("질문이 등록되었습니다.");
                            getFragmentManager().popBackStack();
                        } else {
                            showToast("질문 등록에 실패하였습니다.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("질문 등록에 실패하였습니다.");
                    }
                }
            });
        } catch (JSONException e) {

        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
