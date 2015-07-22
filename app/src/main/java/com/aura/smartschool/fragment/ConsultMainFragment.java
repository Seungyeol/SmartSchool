package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;
import com.aura.smartschool.database.DBConsultChat;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-07-21.
 */
public class ConsultMainFragment extends BaseFragment implements View.OnClickListener {
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
        setActionbar(R.drawable.actionbar_back, mMember.name);
    }

    @Override
    public void onClick(View v) {
        DBConsultChat.TYPE chatType = null;
        switch (v.getId()) {
            case R.id.tv_school_violence:
                chatType = DBConsultChat.TYPE.SCHOOL_VIOLENCE_CONSULT;
                break;
            case R.id.tv_friend_relationship:
                chatType = DBConsultChat.TYPE.FRIEND_RELATIONSHIP_CONSULT;
                break;
            case R.id.tv_family:
                chatType = DBConsultChat.TYPE.FAMILY_CONSULT;
                break;
            case R.id.tv_sexual:
                chatType = DBConsultChat.TYPE.SEXUAL_CONSULT;
                break;
            case R.id.tv_academic:
                chatType = DBConsultChat.TYPE.ACADEMIC_CONSULT;
                break;
            case R.id.tv_career:
                chatType = DBConsultChat.TYPE.CAREER_CONSULT;
                break;
            case R.id.tv_psychology:
                chatType = DBConsultChat.TYPE.PSYCHOLOGY_CONSULT;
                break;
            case R.id.tv_growth:
                chatType = DBConsultChat.TYPE.GROWTH_CONSULT;
                break;
            case R.id.tv_smoking:
                chatType = DBConsultChat.TYPE.SMOKING_CONSULT;
                break;
            default:
                break;
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, ConsultChattingFragment.newInstance(mMember, chatType)).addToBackStack(null).commit();
    }
}