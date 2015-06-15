package com.aura.smartschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aura.smartschool.Interface.MemberListListener;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.MemberListAdapter;
import com.aura.smartschool.dialog.MemberSaveDialogActivity;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.vo.MemberVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-16.
 */
public class FamilyMembersFragment extends Fragment {

    private View mFamilyListView;

    private ListView mListView;
    private TextView tvAddMember;

    private MemberListAdapter mAdapter;

    public FamilyMembersFragment() {
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

    public void setFamilyMemberList(ArrayList<MemberVO> members) {
        mAdapter.setData(members);
        mAdapter.notifyDataSetChanged();
    }


    MemberListListener mMemberListListener = new MemberListListener() {
        @Override
        public void onSelected(int position) {
//            mFragment = new MainFragment(mMemberList.get(position));
//            mFm.beginTransaction().replace(R.id.container, mFragment).commit();
//            showSubMenu(mMemberList.get(position).name);
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
    };
}
