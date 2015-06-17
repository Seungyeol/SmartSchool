package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class HealthMainFragment extends BaseFragment {
	private View mView;
	private MemberVO mMember;

    private static String KEY_MEMBER = "member";

    public static HealthMainFragment newInstance(MemberVO member) {

        HealthMainFragment instance = new HealthMainFragment();

        Bundle args = new Bundle();
        args.putSerializable(KEY_MEMBER, member);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar(R.drawable.btn_pre, mMember.name);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_main, null);
		
		return mView;
	}

}
