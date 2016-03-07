package com.aura.smartschool;

import android.content.Context;
import android.text.TextUtils;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.exception.LoginMemberNullpointerException;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolVO;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-14.
 */
public class LoginManager {
    private volatile static LoginManager INSTANCE;

    private static final int GUARDIAN_NUM = 3;
    private ArrayList<MemberVO> mMemberList = new ArrayList<MemberVO>();
    private MemberVO mLoginUser;

    public interface ResultListener {
        void onSuccess();
        void onFail();
    }

    private LoginManager(){}

    public static LoginManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LoginManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoginManager();
                }
            }
        }
        return INSTANCE;
    }

    public boolean hasLoginInfo(Context context) {
        String id = PreferenceUtil.getInstance(context).getHomeId();
        String mdn = Util.getMdn(context);

        //usim없는 태블릿은 사용불가
        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(mdn)) {
            return false;
        }
        return true;
    }

    public MemberVO getSavedUserInfo(Context context) {
        return new MemberVO(PreferenceUtil.getInstance(context).getHomeId(),
                                Util.getMdn(context));
    }

    public void doLogIn(MemberVO memberVO, final Context context, final ResultListener resultListener) {
        String gcmRegId = PreferenceUtil.getInstance(context).getRegID();
        if(gcmRegId == null){
            // gcm reg id를 못가져오는 경우 네트워크 상태 메시지 출력
            Util.showToast(context, "network error");
            return;
        }

        try {
            String url = Constant.HOST + Constant.API_SIGNIN;

            JSONObject json = new JSONObject();
            json.put("home_id", memberVO.home_id);
            json.put("mdn", memberVO.mdn);
            json.put("gcm_id", gcmRegId);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            new AQuery(context).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        if(status.getCode() != 200) {
                            resultListener.onFail();
                            return;
                        }

                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if("0".equals(object.getString("result"))) {
                            JSONArray array = object.getJSONArray("data");
                            setMemberList(context, array);
                            resultListener.onSuccess();
                        } else {
                            Util.showToast(context, object.getString("msg"));
                            resultListener.onFail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        resultListener.onFail();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            resultListener.onFail();
        }
    }

    public void refreshMemberList(final Context context, final ResultListener resultListener) {
        try {
            String url = Constant.HOST + Constant.API_GET_MEMBERLIST;

            JSONObject json = new JSONObject();
            json.put("home_id", PreferenceUtil.getInstance(context).getHomeId());

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            new AQuery(context).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if (status.getCode() != 200) {
                            resultListener.onFail();
                            return;
                        }

                        if ("0".equals(object.getString("result"))) {
                            JSONArray array = object.getJSONArray("data");
                            setMemberList(context, array);
                            resultListener.onSuccess();
                        } else {
                            Util.showToast(context, object.getString("msg"));
                            resultListener.onFail();
                        }
                    } catch (JSONException e) {
                        resultListener.onFail();
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hasChild() {
        boolean result = false;
        if (mMemberList != null) {
            for (MemberVO memberVO : mMemberList) {
                if (memberVO.is_parent == 0) {
                    result = true;
                }
            }
        }
        return result;
    }

    private void setMemberList(Context context, JSONArray array) throws JSONException {
        mMemberList.clear();

        for(int i=0; i < array.length(); ++i) {
            JSONObject json = array.getJSONObject(i);
            MemberVO member = new MemberVO();
            member.home_id = json.getString("home_id");
            member.member_id = json.getInt("member_id");
            if(json.isNull("mdn")) {
                member.mdn = "";
            } else {
                member.mdn = json.getString("mdn");
            }
            member.is_parent = json.getInt("is_parent");
            member.name = json.getString("name");
            member.relation = json.getString("relation");
            member.photo = json.getString("photo");
            member.sex = json.getString("sex");
            member.birth_date = json.getString("birth_date");
            member.pay_date = json.getString("pay_date");

            //자녀 정보
            SchoolVO school = new SchoolVO();
            school.school_id = json.getInt("school_id");
            school.school_grade = json.getString("school_grade");
            school.school_class = json.getString("school_class");
            school.school_name =  json.getString("school_name");
            school.lat = json.getString("lat");
            school.lng = json.getString("lng");
            school.address = json.getString("address");
            school.new_address = json.getString("new_address");
            school.contact = json.getString("contact");
            school.homepage = json.getString("homepage");
            school.sido = json.getString("sido");
            school.gugun = json.getString("gugun");
            school.gubun2 = json.getString("gubun2");
            school.code = json.getString("code");
            member.mSchoolVO = school;

            mMemberList.add(member);
            //자기 정보 저장
            if(Util.getMdn(context).equals(member.mdn)) {
                mLoginUser = member;
                PreferenceUtil.getInstance(context).putHomeId(member.home_id);
                PreferenceUtil.getInstance(context).putMemberId(member.member_id);
                PreferenceUtil.getInstance(context).putParent(member.is_parent==1 ? true:false );
                PreferenceUtil.getInstance(context).putName(member.name);
                //자녀일 경우 학교의 위도 경도 저장
                if(member.is_parent==0) {
                    PreferenceUtil.getInstance(context).putSchool_lat(school.lat);
                    PreferenceUtil.getInstance(context).putSchool_lng(school.lng);
                }
            }
        }

        if (mLoginUser.is_parent == 0) {
            if (isGuardianInfoEmpty(context)) {
                saveGuardianInfo(context);
            }
        }

    }

    private void saveGuardianInfo(Context context) {
        int idx = 0;
        for (MemberVO memberVO: mMemberList) {
            if (memberVO.is_parent == 1) {
                if (!StringUtils.isEmpty(memberVO.mdn) && !StringUtils.isEmpty(memberVO.name)){
                    PreferenceUtil.getInstance(context).putGuardianInfo(idx, memberVO.name, memberVO.mdn);
                    idx++;
                }
            }
        }
    }

    private boolean isGuardianInfoEmpty(Context context) {
        for (int i = 0; i < GUARDIAN_NUM; i++) {
            if (!StringUtils.isEmpty(PreferenceUtil.getInstance(context).getGuardianInfo(i)[0])) {
                return false;
            }
        }
        return true;
    }

    public MemberVO getLoginUser() {
        return mLoginUser;
    }

    public ArrayList<MemberVO> getMemberList() throws LoginMemberNullpointerException {
        if (mLoginUser == null) {
            throw new LoginMemberNullpointerException();
        }
        if (mLoginUser.is_parent == 1) {
            return mMemberList;
        } else {
            ArrayList<MemberVO> selfList = new ArrayList<MemberVO>();
            selfList.add(mLoginUser);
            return selfList;
        }
    }

    public MemberVO findMember(int school_id) {
        if (LoginManager.getInstance().getLoginUser().is_parent == 1) {
            for (MemberVO memberVO : mMemberList) {
                if (memberVO.mSchoolVO.school_id == school_id) {
                    return memberVO;
                }
            }
        } else {
            return mLoginUser;
        }

        return null;
    }

    public MemberVO findMemberByID(int member_id) {
        for (MemberVO memberVO : mMemberList) {
            if (memberVO.member_id == member_id) {
                return memberVO;
            }
        }

        return null;
    }
}
