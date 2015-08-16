package com.aura.smartschool.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.database.ConsultType;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-07-21.
 */
public class ConsultMainFragment extends Fragment implements View.OnClickListener {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private View tvBtnSchoolViolence;
    private View tvBtnFriendRelationship;
    private View tvBtnFamily;
    private View tvBtnSexual;
    private View tvBtnAcademic;
    private View tvBtnCareer;
    private View tvBtnPsychology;
    private View tvBtnGrowth;
    private View tvBtnSmoking;

    public static ConsultMainFragment newInstance(MemberVO member) {
        ConsultMainFragment instance = new ConsultMainFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_consult_main, null);

        tvBtnSchoolViolence = view.findViewById(R.id.tv_school_violence);
        tvBtnFriendRelationship = view.findViewById(R.id.tv_friend_relationship);
        tvBtnFamily = view.findViewById(R.id.tv_family);
        tvBtnSexual = view.findViewById(R.id.tv_sexual);
        tvBtnAcademic = view.findViewById(R.id.tv_academic);
        tvBtnCareer = view.findViewById(R.id.tv_career);
        tvBtnPsychology = view.findViewById(R.id.tv_psychology);
        tvBtnGrowth = view.findViewById(R.id.tv_growth);
        tvBtnSmoking = view.findViewById(R.id.tv_smoking);

        tvBtnSchoolViolence.setOnClickListener(this);
        tvBtnFriendRelationship.setOnClickListener(this);
        tvBtnFamily.setOnClickListener(this);
        tvBtnSexual.setOnClickListener(this);
        tvBtnAcademic.setOnClickListener(this);
        tvBtnCareer.setOnClickListener(this);
        tvBtnPsychology.setOnClickListener(this);
        tvBtnGrowth.setOnClickListener(this);
        tvBtnSmoking.setOnClickListener(this);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setHeaderView(R.drawable.actionbar_back, mMember.name);
    }

    @Override
    public void onClick(View v) {
        ConsultType chatType = null;
        switch (v.getId()) {
            case R.id.tv_school_violence:
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01011111111"));
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "01011111111"));
                startActivity(intent);
                return;
                //chatType = DBConsultChat.TYPE.SCHOOL_VIOLENCE_CONSULT;
            case R.id.tv_friend_relationship:
                chatType = ConsultType.FRIEND_RELATIONSHIP_CONSULT;
                break;
            case R.id.tv_family:
                chatType = ConsultType.FAMILY_CONSULT;
                break;
            case R.id.tv_sexual:
                chatType = ConsultType.SEXUAL_CONSULT;
                break;
            case R.id.tv_academic:
                chatType = ConsultType.ACADEMIC_CONSULT;
                break;
            case R.id.tv_career:
                chatType = ConsultType.CAREER_CONSULT;
                break;
            case R.id.tv_psychology:
                chatType = ConsultType.PSYCHOLOGY_CONSULT;
                break;
            case R.id.tv_growth:
                chatType = ConsultType.GROWTH_CONSULT;
                break;
            case R.id.tv_smoking:
                chatType = ConsultType.SMOKING_CONSULT;
                break;
            default:
                break;
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, ConsultChattingFragment.newInstance(mMember, chatType)).addToBackStack(null).commit();
    }
}
