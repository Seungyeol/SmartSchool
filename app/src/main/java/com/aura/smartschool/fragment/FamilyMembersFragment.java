package com.aura.smartschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.aura.smartschool.vo.SchoolVO;

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

        return mFamilyListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO :: Test Code 차후 삭제!!!!! ----START
        //setFamilyMemberList(getTestFamilyList());
        //TODO :: Test Code 차후 삭제!!!!! ----END
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


    //---------------------------------- test code start----------------------------------//
    private ArrayList<MemberVO> getTestFamilyList() {
        ArrayList<MemberVO> testList = new ArrayList<>();
        MemberVO testMember = new MemberVO();
        testMember.member_id =1 ;
        testMember.home_id ="test";
        testMember.mdn ="01010101010";
        testMember.name="홍길동";
        testMember.relation="아들";
        testMember.is_parent=0;
        testMember.photo=null;
        testMember.mSchoolVO = new SchoolVO();
        testMember.mSchoolVO.school_name="초등학교";
        testMember.mSchoolVO.school_grade="1학년";
        testMember.mSchoolVO.school_class="1반";
        testList.add(testMember);
        return testList;
    }
    //---------------------------------- test code end----------------------------------//
}
