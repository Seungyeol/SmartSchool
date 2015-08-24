package com.aura.smartschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.Interface.MemberListListener;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.MemberListAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.dialog.MemberSaveDialogActivity;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-16.
 */
public class FamilyMembersFragment extends Fragment implements LoginManager.ResultListener {

    private View mFamilyListView;

    private ListView mListView;
    private TextView tvAddMember;

    private MemberListAdapter mAdapter;

    public FamilyMembersFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFamilyListView = inflater.inflate(R.layout.fragment_family_members, container, false);
        mListView = (ListView) mFamilyListView.findViewById(R.id.listview);

        mAdapter = new MemberListAdapter(getActivity(), new ArrayList<MemberVO>(), mMemberListListener);
        mListView.setAdapter(mAdapter);
        tvAddMember = (TextView) mFamilyListView.findViewById(R.id.tvAddMember);

        tvAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberVO member = new MemberVO();
                member.home_id = PreferenceUtil.getInstance(getActivity()).getHomeId();

                Intent intent = new Intent(getActivity(), MemberSaveDialogActivity.class);
                intent.putExtra("mode", MainActivity.MOD_ADD);
                intent.putExtra("member", member);
                startActivityForResult(intent, MainActivity.REQ_DIALOG_MEMBER_ADD);
            }
        });

        return mFamilyListView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQ_DIALOG_MEMBER_UPDATE:
            case MainActivity.REQ_DIALOG_MEMBER_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    LoadingDialog.showLoading(getActivity());
                    LoginManager.getInstance().refreshMemberList(getActivity(), FamilyMembersFragment.this);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setHeaderView(R.drawable.home, PreferenceUtil.getInstance(this.getActivity()).getHomeId());
        refreshMemberList();
    }

    private void refreshMemberList() {
        mAdapter.setData(LoginManager.getInstance().getMemberList());
        mAdapter.notifyDataSetChanged();
    }

    MemberListListener mMemberListListener = new MemberListListener() {
        @Override
        public void onSelected(int position) {
            //자녀의 경우 헬스메인으로 이동
            if(mAdapter.getItem(position).is_parent == 0) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, HealthMainFragment.newInstance(mAdapter.getItem(position))).addToBackStack(null).commit();
            }
        }

        @Override
        public void onUpdateClicked(int position) {
            Intent intent = new Intent(getActivity(), MemberSaveDialogActivity.class);
            intent.putExtra("mode", MainActivity.MOD_UPDATE);
            intent.putExtra("member", mAdapter.getItem(position));
            startActivityForResult(intent, MainActivity.REQ_DIALOG_MEMBER_UPDATE);
        }

        @Override
        public void onAddClicked(MemberVO member) {

        }

        @Override
        public void onRemoveClicked(MemberVO member) {
            LoadingDialog.showLoading(getActivity());
            try {
                String url = Constant.HOST + Constant.API_REMOVE_MEMBER;

                JSONObject json = new JSONObject();
                json.put("member_id", member.member_id);

                new AQuery(getActivity()).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        LoadingDialog.hideLoading();
                        try {
                            if (status.getCode() != 200) {
                                Util.showToast(getActivity(), "실패 하였습니다");
                                return;
                            }
                            Log.d("LDK", "result:" + object.toString(1));

                            if (object.getInt("result") == 0) {
                                LoadingDialog.showLoading(getActivity());
                                LoginManager.getInstance().refreshMemberList(getActivity(), FamilyMembersFragment.this);
                            } else {
                                Util.showToast(getActivity(), "실패 하였습니다");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Util.showToast(getActivity(), "실패 하였습니다");
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onSuccess() {
        LoadingDialog.hideLoading();
        refreshMemberList();
    }

    @Override
    public void onFail() {
        LoadingDialog.hideLoading();
    }
}
