package com.aura.smartschool;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-14.
 */
public class LoginManager {
    private volatile static LoginManager INSTANCE;

    private AQuery mAq;
    private Context mContext;

    private ArrayList<MemberVO> mMemberList = new ArrayList<MemberVO>();
    private MemberVO mLoginUser;

    public interface LoginResultListener {
        void onLoginSuccess();
        void onLoginFail();
    }

    private LoginManager(Context context){
        mAq = new AQuery(context);
        mContext = context;
    }

    public static LoginManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LoginManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoginManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public boolean hasLoginInfo() {
        String id = PreferenceUtil.getInstance(mContext).getHomeId();
        String mdn = Util.getMdn(mContext);

        //usim없는 태블릿은 사용불가
        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(mdn)) {
            return false;
        }
        return true;
    }

    public void doLogIn(LoginResultListener loginResultListener) {
        String homeId = PreferenceUtil.getInstance(mContext).getHomeId();
        String mdn = Util.getMdn(mContext);
        String gcmRegId = PreferenceUtil.getInstance(mContext).getRegID();
        if(gcmRegId == null){
            // gcm reg id를 못가져오는 경우 네트워크 상태 메시지 출력
            Util.showToast(mContext, "network error");
            return;
        }

        try {
            String url = Constant.HOST + Constant.API_SIGNIN;

            JSONObject json = new JSONObject();
            json.put("home_id", homeId);
            json.put("mdn", mdn);
            json.put("gcm_id", gcmRegId);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if(status.getCode() != 200) {

                            return;
                        }

                        Log.d("LDK", "result:" + object.toString(1));

                        if("0".equals(object.getString("result"))) {
                            JSONArray array = object.getJSONArray("data");
                            loginResultListener.onLoginSuccess();
                        } else {
                            loginResultListener.onLoginFail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loginResultListener.onLoginFail();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            loginResultListener.onLoginFail();
        }
    }

    private void displayMemberList(JSONArray array) throws JSONException {
        mMemberList.clear();

        for(int i=0; i < array.length(); ++i) {
            JSONObject json = array.getJSONObject(i);
            MemberVO member = new MemberVO();
            member.home_id = json.getString("home_id");
            member.member_id = json.getInt("member_id");
            member.mdn = json.getString("mdn");
            member.is_parent = json.getInt("is_parent");
            member.name = json.getString("name");
            member.relation = json.getString("relation");
            member.photo = json.getString("photo");
            member.sex = json.getString("sex");
            member.birth_date = json.getString("birth_date");

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
            member.mSchoolVO = school;

            mMemberList.add(member);
            //자기 정보 저장
            if(Util.getMdn(mContext).equals(member.mdn)) {
                mLoginUser = member;
                PreferenceUtil.getInstance(mContext).putHomeId(member.home_id);
                PreferenceUtil.getInstance(mContext).putMemberId(member.member_id);
                PreferenceUtil.getInstance(mContext).putParent(member.is_parent==1 ? true:false );
                PreferenceUtil.getInstance(mContext).putName(member.name);
            }
        }
    }

    public MemberVO getLoginUser() {
        return mLoginUser;
    }

    public ArrayList<MemberVO> getMemberList() {
        return mMemberList;
    }
}
