package com.aura.smartschool;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Interface.LoginListener;
import com.aura.smartschool.adapter.DrawerAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.dialog.LoginDialog;
import com.aura.smartschool.dialog.RegisterDialogActivity;
import com.aura.smartschool.fragment.FamilyMembersFragment;
import com.aura.smartschool.service.MyLocationService;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolVO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
	public static final int REQ_DIALOG_SIGNUP = 100;
	public static final int REQ_DIALOG_MEMBER_UPDATE = 101;
	public static final int REQ_DIALOG_MEMBER_ADD = 102;
	public static final int REQ_CODE_PICK_IMAGE = 200;
	public static final int REQ_CODE_FIND_SCHOOL = 201;
	private final static int REQ_PLAY_SERVICES_RESOLUTION = 9000;

	public static final String TEMP_PHOTO_FILE = "temp.jpg";       // 임시 저장파일
	public static final int MOD_ADD = 0;
	public static final int MOD_UPDATE = 1;

	private static final String SENDER_ID = "345146841450";
	
	private TextView tvTitle;
	private ImageView ivHome;
    private View ivShowDrawerMenu;

    DrawerLayout mDrawerLayout;
    RecyclerView mDrawerList;

	private LoginDialog mLoginDialog;
	
	private AQuery mAq;

    private FragmentManager mFm;
	private FamilyMembersFragment mFamilyMemberFragment;

    private ArrayList<MemberVO> mMemberList = new ArrayList<MemberVO>();

	private GoogleCloudMessaging _gcm;
	private String _regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAq = new AQuery(this);
		mFamilyMemberFragment = new FamilyMembersFragment();

		mFm = getSupportFragmentManager();
		mFm.beginTransaction().replace(R.id.content_frame, mFamilyMemberFragment).commit();

		initDrawerView();
		initActionBar();

		if(checkPlayServices()){
			if(TextUtils.isEmpty(getRegistrationId())) {
				registerInBackground();
			}
		}

		checkLogin();
	}


	private void initActionBar() {
		//액션바 처리
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		View mCustomView = View.inflate(this, R.layout.actionbar, null);
		tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
		ivHome = (ImageView) mCustomView.findViewById(R.id.logo);
		ivHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFm.getBackStackEntryCount() > 0) {
					mFm.popBackStack();
				}
			}
		});

		mCustomView.findViewById(R.id.fl_more).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			}
		});

		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

	private void initDrawerView() {
		String[] menu;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.drawer_menu);

        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
		if (PreferenceUtil.getInstance(MainActivity.this).isParent()) {
			menu = getResources().getStringArray(R.array.parent_drawer_menu);
		} else {
			menu = getResources().getStringArray(R.array.child_drawer_menu);
		}

        mDrawerList.setAdapter(new DrawerAdapter(menu));
    }

    @Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else if(mFm.getBackStackEntryCount() > 0) {
            mFm.popBackStack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Smart School")
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                            if(mLoginDialog != null) {
                                mLoginDialog.dismiss();
                            }
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
		}
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case REQ_DIALOG_SIGNUP:
            if (resultCode == RESULT_OK) {
            	getMemberList();
            }
            break;
        }
    }

	//GCM----------------------------------------------------------------------------------------------
	// google play service가 사용가능한가
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, REQ_PLAY_SERVICES_RESOLUTION);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
					}
				});
				dialog.show();

			} else {
				Log.i("MainActivity.java ",
						"| checkPlayService |This device is not supported.|");
				// _textStatus.append("\n This device is not supported.\n");
				finish();
			}
			return false;
		}
		return true;
	}

	// registration id를 가져온다.
	private String getRegistrationId() {
		String registrationId = PreferenceUtil.getInstance(getApplicationContext()).getRegID();
		if (TextUtils.isEmpty(registrationId)) {
			Log.e("LDK", "|Registration not found.|");
			return "";
		}

		int registeredVersion = PreferenceUtil.getInstance(getApplicationContext()).appVersion();
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.i("LDK", "|App version changed.|");
			// _textStatus.append("\n App version changed.\n");
			return "";
		}
		Log.i("LDK", "registrationId:" + registrationId);
		return registrationId;
	}

	// gcm 서버에 접속해서 registration id를 발급받는다.
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (_gcm == null) {
						_gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					_regId = _gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + _regId;
					storeRegistrationId(_regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}

				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.e("LDK", "|" + msg + "|");
			}
		}.execute(null, null, null);
	}

	// app version을 가져온다.
	private int getAppVersion() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	// registraion id를 preference에 저장한다.
	private void storeRegistrationId(String regId) {
		int appVersion = getAppVersion();
		PreferenceUtil.getInstance(getApplicationContext()).putRegID(regId);
		PreferenceUtil.getInstance(getApplicationContext()).putAppVersion(appVersion);
	}
	//GCM 로직 끝--------------------------------------------------------------------------------------------------
	
	private void checkLogin() {
		String id = PreferenceUtil.getInstance(this).getHomeId();
		String mdn = Util.getMdn(this);
		Log.d("LDK", id + "," + mdn);
		
		//usim없는 태블릿은 사용불가
		if(TextUtils.isEmpty(id) || TextUtils.isEmpty(mdn)) {
			showLoginDialog();
			return;
		}
		
		getLogin(new MemberVO(id, mdn));
 	}
	
	private void showLoginDialog(){
		if(mLoginDialog == null){
			mLoginDialog = new LoginDialog(this, mLoginListener);
		}
		
		if(mLoginDialog.isShowing() == false){
			mLoginDialog.show();
		}
	}
	
	private void hideLoginDialog() {
		if(mLoginDialog != null) {
			mLoginDialog.dismiss();
		}
	}
	
	private void getLogin(MemberVO member) {
		String gcmRegId = PreferenceUtil.getInstance(this).getRegID();
		if(gcmRegId == null){
			// gcm reg id를 못가져오는 경우 네트워크 상태 메시지 출력
			Util.showToast(this, "network error");
			return;
		}

		LoadingDialog.showLoading(this);
		try {
			String url = Constant.HOST + Constant.API_SIGNIN;

			JSONObject json = new JSONObject();
			json.put("home_id", member.home_id);
			json.put("mdn", member.mdn);
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
							hideLoginDialog();
							//location tracking
							startService(new Intent(MainActivity.this, MyLocationService.class));
							JSONArray array = object.getJSONArray("data");
							displayMemberList(array);
						} else {
							showLoginDialog();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void getMemberList() {
		LoadingDialog.showLoading(this);
		try {
			String url = Constant.HOST + Constant.API_GET_MEMBERLIST;

			JSONObject json = new JSONObject();
			json.put("home_id", PreferenceUtil.getInstance(MainActivity.this).getHomeId());

			Log.d("LDK", "url:" + url);
			Log.d("LDK", "input parameter:" + json.toString(1));

			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					LoadingDialog.hideLoading();
					try {
						Log.d("LDK", "result:" + object.toString(1));

						if(status.getCode() != 200) {

							return;
						}

						if("0".equals(object.getString("result"))) {
							JSONArray array = object.getJSONArray("data");
							displayMemberList(array);
						} else {

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
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
			if(Util.getMdn(this).equals(member.mdn)) {
				PreferenceUtil.getInstance(this).putHomeId(member.home_id);
				PreferenceUtil.getInstance(this).putMemberId(member.member_id);
				PreferenceUtil.getInstance(this).putParent(member.is_parent==1 ? true:false );
				PreferenceUtil.getInstance(this).putName(member.name);
			}
		}

        mFamilyMemberFragment.setFamilyMemberList(mMemberList);
	}

	//more menu
	PopupMenu.OnMenuItemClickListener mMoreMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_settings:

				break;
			}
			
			return true;
		}
	};
	
	LoginListener mLoginListener = new LoginListener() {
		@Override
		public void onLogin(MemberVO member) {
			getLogin(member);
		}

		@Override
		public void gotoRegister() {
			//mLoginDialog.dismiss();
			//mRegisterDialog = new RegisterDialog(MainActivity.this, mLoginListener);
			//mRegisterDialog.show();
			Intent intent = new Intent(MainActivity.this, RegisterDialogActivity.class);
			startActivityForResult(intent, REQ_DIALOG_SIGNUP);
		}

		@Override
		public void onRegister(MemberVO member) {
			//getRegister(member);
		}
	};
}
