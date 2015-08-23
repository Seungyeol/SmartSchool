package com.aura.smartschool.Interface;

import com.aura.smartschool.vo.MemberVO;

public interface LoginDialogListener {
	void doLogin(MemberVO member);
	void gotoRegister();
	void onRegister(MemberVO member);
	void onPreView();
}
