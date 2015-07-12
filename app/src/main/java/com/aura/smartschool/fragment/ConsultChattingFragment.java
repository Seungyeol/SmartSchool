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

import com.aura.smartschool.R;
import com.aura.smartschool.adapter.ConsultChattingAdapter;
import com.aura.smartschool.adapter.WalkingHistoryAdapter;
import com.aura.smartschool.database.DBConsultChat;
import com.aura.smartschool.database.DBStepCounter;
import com.aura.smartschool.vo.ConsultChatVO;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.WalkingVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SeungyeolBak on 15. 7. 11..
 */
public class ConsultChattingFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private DBConsultChat dbConsult;

    private EditText etChat;
    private Button btnEnter;

    private RecyclerView mConsultChattingList;
    private ConsultChattingAdapter mConsultChattingAdapter;

    public static ConsultChattingFragment newInstance(MemberVO member) {
        ConsultChattingFragment instance = new ConsultChattingFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
        dbConsult = DBConsultChat.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_consult_chatting, null);

        mConsultChattingList = (RecyclerView) view.findViewById(R.id.list_consult_chatting);
        mConsultChattingList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mConsultChattingAdapter = new ConsultChattingAdapter(dbConsult.getAllMsg());
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
                long id = dbConsult.insertMsg(0, etChat.getText().toString(), new Date(), -1);
                mConsultChattingAdapter.addItem(new ConsultChatVO(0, etChat.getText().toString(), new Date(), 0));
                etChat.setText("");
                mConsultChattingList.scrollToPosition(mConsultChattingAdapter.getItemCount() - 1);
            }
        });

        return view;
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
        setActionbar(R.drawable.btn_pre, mMember.name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbConsult != null) {
            dbConsult.close();
        }
    }
}
