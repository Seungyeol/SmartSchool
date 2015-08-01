package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.ConsultChattingAdapter;
import com.aura.smartschool.database.DBConsultChat;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.vo.ConsultChatVO;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by SeungyeolBak on 15. 7. 11..
 */
public class ConsultChattingFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";

    private AQuery mAq;

    private MemberVO mMember;
    private DBConsultChat.TYPE chatType;

    private DBConsultChat dbConsult;

    private EditText etChat;
    private Button btnEnter;

    private RecyclerView mConsultChattingList;
    private ConsultChattingAdapter mConsultChattingAdapter;

    public static ConsultChattingFragment newInstance(MemberVO member, DBConsultChat.TYPE chatType) {
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
        chatType = (DBConsultChat.TYPE) args.getSerializable("chatType");

        Log.d("ConsultChattingFragment", "ConsultChattingFragment >> onCreate >> type = " + chatType.getTableName());

        dbConsult = DBConsultChat.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_consult_chatting, null);

        mAq = new AQuery(view);

        mConsultChattingList = (RecyclerView) view.findViewById(R.id.list_consult_chatting);
        mConsultChattingList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mConsultChattingAdapter = new ConsultChattingAdapter(dbConsult.getAllMsg(chatType));
        mConsultChattingList.setAdapter(mConsultChattingAdapter);
        if (mConsultChattingAdapter.getItemCount() > 0) {
            mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getItemCount()-1);
        }

        etChat = (EditText) view.findViewById(R.id.et_chat);
        btnEnter = (Button) view.findViewById(R.id.btn_enter);

        etChat.addTextChangedListener(mWatcher);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long dbIndex = dbConsult.insertMsg(chatType, etChat.getText().toString(), DBConsultChat.MSG_FROM_ME, new Date(), -1);
                mConsultChattingAdapter.addItem(new ConsultChatVO(dbIndex, DBConsultChat.MSG_FROM_ME, etChat.getText().toString(), new Date(), 0));
                mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getItemCount() - 1);
                sendConsultMessage(etChat.getText().toString(), dbIndex);
            }
        });

        return view;
    }

    private void sendConsultMessage(String msg, final long dbIndex) {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.HOST + Constant.API_ADD_CONSULT;

            JSONObject json = new JSONObject();
            json.put("content", msg);
            json.put("category", chatType.getCode());
            json.put("who", DBConsultChat.MSG_FROM_ME);
            json.put("member_id", mMember.member_id);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    etChat.setText("");
                    try {
                        if (status.getCode() != 200) {
                            Log.d("LDK", "FAIL");
                            updateResult(dbIndex, -1);
                            return;
                        }
                        Log.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            updateResult(dbIndex, 0);
                        } else {
                            updateResult(dbIndex, -1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        updateResult(dbIndex, -1);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            updateResult(dbIndex, -1);
        }
    }

    private void updateResult(long dbIndex, int result) {
        dbConsult.updateSendResult(chatType, dbIndex, result);
        if (result == -1) {
            mConsultChattingAdapter.setFailMsg(dbIndex);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.actionbar_back, mMember.name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbConsult != null) {
            dbConsult.close();
        }
    }
}
