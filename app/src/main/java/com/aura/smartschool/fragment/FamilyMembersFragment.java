package com.aura.smartschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class FamilyMembersFragment extends BaseFragment {

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

/*        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("LDK", "onItemClick");
                getFragmentManager().beginTransaction().replace(R.id.content_frame, HealthMainFragment.newInstance(mAdapter.getItem(position))).addToBackStack(null).commit();
            }
        });*/

        return mFamilyListView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQ_DIALOG_MEMBER_UPDATE:
            case MainActivity.REQ_DIALOG_MEMBER_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    ((MainActivity)getActivity()).getMemberList();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.home, PreferenceUtil.getInstance(this.getActivity()).getHomeId());
    }

    public void setFamilyMemberList(ArrayList<MemberVO> members) {
        mAdapter.setData(members);
        mAdapter.notifyDataSetChanged();
    }


    MemberListListener mMemberListListener = new MemberListListener() {
        @Override
        public void onSelected(int position) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, HealthMainFragment.newInstance(mAdapter.getItem(position))).addToBackStack(null).commit();
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
