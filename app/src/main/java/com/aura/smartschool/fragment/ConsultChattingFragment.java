package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.ConsultFragmentVisibleManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.ConsultChattingAdapter;
import com.aura.smartschool.database.ConsultType;
import com.aura.smartschool.database.DBConsultChatFail;
import com.aura.smartschool.dialog.AppraisalDialogFragment;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.ConsultVO;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SeungyeolBak on 15. 7. 11..
 */
public class ConsultChattingFragment extends Fragment {

    private static String KEY_MEMBER = "member";

    private AQuery mAq;

    private MemberVO mMember;
    private ConsultType chatType;

    private DBConsultChatFail dbConsultFail;

    private EditText etChat;
    private View btnEnter;
    private View btnAppriaisal;

    private RecyclerView mConsultChattingList;
    private ConsultChattingAdapter mConsultChattingAdapter;

    private int sessionId;

    private ArrayList<ConsultVO> consultList = new ArrayList<>();

    public static ConsultChattingFragment newInstance(MemberVO member, ConsultType chatType) {
        ConsultChattingFragment instance = new ConsultChattingFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_MEMBER, member);
        args.putSerializable("chatType", chatType);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
        chatType = (ConsultType) args.getSerializable("chatType");

        dbConsultFail = DBConsultChatFail.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_consult_chatting, null);

        ConsultFragmentVisibleManager.getInstance().setVisible(true, chatType.code, messageReceiveObserver);

        mAq = new AQuery(view);

        mConsultChattingList = (RecyclerView) view.findViewById(R.id.list_consult_chatting);
        mConsultChattingList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mConsultChattingAdapter = new ConsultChattingAdapter(new ArrayList<ConsultVO>(), dbConsultFail.getAllFailMsg(chatType), retryManager);
        mConsultChattingList.setAdapter(mConsultChattingAdapter);
        if (mConsultChattingAdapter.getItemCount() > 0) {
            mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getItemCount()-1);
        }

        etChat = (EditText) view.findViewById(R.id.et_chat);
        btnEnter = view.findViewById(R.id.btn_enter);
        btnAppriaisal = view.findViewById(R.id.btn_appraisal);

        etChat.addTextChangedListener(mWatcher);
        btnAppriaisal.setOnClickListener(mBtnClicked);
        btnEnter.setOnClickListener(mBtnClicked);

        loadConsultMessage();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setHeaderView(R.drawable.actionbar_back, chatType.consultName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ConsultFragmentVisibleManager.getInstance().setVisible(false, -1, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbConsultFail != null) {
            dbConsultFail.close();
        }
    }

    private void loadConsultMessage() {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.HOST + Constant.API_GET_CONSULT_LIST;

            JSONObject json = new JSONObject();
            json.put("member_id", mMember.member_id);
            json.put("category", chatType.code);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            SchoolLog.d("LDK", "FAIL");
                            return;
                        }
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                ConsultVO consult = new ConsultVO();
                                consult.consultId = json.getInt("consult_id");
                                consult.sessionId = json.getInt("session_id");
                                sessionId = json.getInt("session_id");
                                consult.who = json.getInt("who");
                                consult.content = json.getString("content");
                                consult.created = Util.getDateTimeFromString(json.getString("created"));
                                consultList.add(consult);
                            }
                            mConsultChattingAdapter.setConsultMessageList(consultList);
                            if (consultList.size() > 0) {
                                btnAppriaisal.setEnabled(true);
                            }
                            mConsultChattingAdapter.notifyDataSetChanged();
                            mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getItemCount() - 1);
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendConsultMessage(final String msg) {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.HOST + Constant.API_ADD_CONSULT;

            JSONObject json = new JSONObject();
            json.put("content", msg);
            json.put("category", chatType.code);
            json.put("who", ConsultType.MSG_FROM_STUDENT);
            json.put("member_id", mMember.member_id);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    etChat.setText("");
                    try {
                        if (status.getCode() != 200) {
                            SchoolLog.d("LDK", "FAIL");
                            addFailMessage(msg);
                            return;
                        }
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            ConsultVO consultVO = new ConsultVO();
                            consultVO.content = msg;
                            consultVO.who = ConsultType.MSG_FROM_STUDENT;
                            consultVO.created = new Date();
                            mConsultChattingAdapter.addItem(consultVO);
                            mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getChatMessageListItemCount() - 1);
                        } else {
                            addFailMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        addFailMessage(msg);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addFailMessage(String msg) {
        long id = dbConsultFail.insertMsg(chatType, msg);
        ConsultVO consultVO = new ConsultVO();
        consultVO.consultId = id;
        consultVO.content = msg;
        consultVO.who = ConsultType.MSG_FROM_STUDENT;
        consultVO.created = new Date();
        mConsultChattingAdapter.addFailItem(consultVO);
        mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getItemCount() - 1);
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
            if (s.length() == 0) {
                btnEnter.setEnabled(false);
            } else {
                btnEnter.setEnabled(true);
            }
        }
    };

    private View.OnClickListener mBtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_appraisal:
                    AppraisalDialogFragment dialogFragment = new AppraisalDialogFragment();
                    dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    dialogFragment.setAppraisalSelectedListener(appraisalSelectedListener);
                    dialogFragment.show(getFragmentManager(), "appraisalDailog");
                    break;
                case R.id.btn_enter:
                    sendConsultMessage(etChat.getText().toString());
                    break;
            }
        }
    };

    private AppraisalDialogFragment.OnAppraisalSelectedListener appraisalSelectedListener = new AppraisalDialogFragment.OnAppraisalSelectedListener() {
        @Override
        public void onApprisalSelected(int num) {
            LoadingDialog.showLoading(getActivity());
            try {
                String url = Constant.HOST + Constant.API_RATE_CONSULT;

                JSONObject json = new JSONObject();
                json.put("session_id", sessionId);
                json.put("rate", num);

                SchoolLog.d("LDK", "url:" + url);
                SchoolLog.d("LDK", "input parameter:" + json.toString(1));

                mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        LoadingDialog.hideLoading();
                        try {
                            if (status.getCode() != 200) {
                                SchoolLog.d("LDK", "FAIL");
                                Toast.makeText(getActivity(), "평가 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            SchoolLog.d("LDK", "result:" + object.toString(1));

                            if (object.getInt("result") == 0) {
                                Toast.makeText(getActivity(), "평가 하였습니다.", Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getActivity(), "평가 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "평가 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private ConsultChattingAdapter.FailMessageManager retryManager = new ConsultChattingAdapter.FailMessageManager() {
        @Override
        public void OnRetry(ConsultVO message) {
            mConsultChattingAdapter.removeItem(message);
            dbConsultFail.removeMessage(chatType, message.consultId);
            sendConsultMessage(message.content);
        }

        @Override
        public void OnRemove(ConsultVO message) {
            mConsultChattingAdapter.removeItem(message);
            dbConsultFail.removeMessage(chatType, message.consultId);
        }
    };

    private ConsultFragmentVisibleManager.OnMessageReceiveObserver messageReceiveObserver = new ConsultFragmentVisibleManager.OnMessageReceiveObserver() {
        @Override
        public void onReceived(String message) {
            ConsultVO consultVO = new ConsultVO();
            consultVO.content = message;
            consultVO.who = ConsultType.MSG_FROM_TEACHER;
            consultVO.created = new Date();
            mConsultChattingAdapter.addItem(consultVO);
            mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getChatMessageListItemCount() - 1);
        }
    };
}
