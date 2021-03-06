package com.aura.smartschool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Interface.DrawerSelectedListener;
import com.aura.smartschool.Interface.LoginDialogListener;
import com.aura.smartschool.adapter.DrawerAdapter;
import com.aura.smartschool.database.ConsultType;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.dialog.LoginDialog;
import com.aura.smartschool.dialog.IntroDialog;
import com.aura.smartschool.dialog.ModifyFamilyNameDialogFragment;
import com.aura.smartschool.dialog.RegisterDialogActivity;
import com.aura.smartschool.exception.LoginMemberNullpointerException;
import com.aura.smartschool.fragment.ConsultChattingFragment;
import com.aura.smartschool.fragment.FamilyMembersFragment;
import com.aura.smartschool.fragment.GeofenceFragment;
import com.aura.smartschool.fragment.HelpViewFragment;
import com.aura.smartschool.fragment.SchoolNoticePagerFragment;
import com.aura.smartschool.fragment.WalkingPagerFragment;
import com.aura.smartschool.service.MyLocationService;
import com.aura.smartschool.service.SOSIconService;
import com.aura.smartschool.service.StepCounterService;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements LoginManager.ResultListener {
	//Handler
	public static final int MSG_CHECK_ANIMATION = 0;
	public static final int MSG_INCREASE_NUMBER = 1;

	//ACTIVITY REQUEST
	public static final int REQ_DIALOG_SIGNUP = 100;
	public static final int REQ_DIALOG_MEMBER_UPDATE = 101;
	public static final int REQ_DIALOG_MEMBER_ADD = 102;
	public static final int REQ_CODE_PICK_IMAGE = 200;
	public static final int REQ_CODE_CAMERA_IMAGE = 300;
	public static final int REQ_CODE_FIND_SCHOOL = 201;
	private final static int REQ_PLAY_SERVICES_RESOLUTION = 9000;

	public static final String TEMP_PHOTO_FILE = "temp.jpg";       // 임시 저장파일
	public static final int MOD_ADD = 0;
	public static final int MOD_UPDATE = 1;

	private static final String SENDER_ID = "552376275094";
	
	private TextView tvTitle;
	private ImageView ivHome;

	private View hearderView;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
	private DrawerAdapter mDrawerAdapter;

	private View llPreviewLayout;
	private FragmentStatePagerAdapter helpViewAdapter;
	private ViewPager mHelpViewPager;

	private View btnLogin;
	private View btnSignUp;

	private LoginDialog mLoginDialog;
	
    private FragmentManager mFm;

	private GoogleCloudMessaging _gcm;
	private String _regId;

	LoginManager mLoginManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFm = getSupportFragmentManager();

		mLoginManager = LoginManager.getInstance();

		hearderView = findViewById(R.id.header_view);
		llPreviewLayout = findViewById(R.id.ll_preview);

		initDrawerView();
		initActivityHeaderView();
		initPreView();

		if(checkPlayServices()){
			if(TextUtils.isEmpty(getRegistrationId())) {
				registerInBackground();
			}
		}

		doLoginProcess();
	}

	private void initPreView() {
		btnLogin = findViewById(R.id.btn_login);
		btnSignUp = findViewById(R.id.btn_sign_up);

		btnLogin.setOnClickListener(mClicked);
		btnSignUp.setOnClickListener(mClicked);

		mHelpViewPager = (ViewPager) findViewById(R.id.vp_helpview);
		helpViewAdapter = new HelpViewAdater(this.getSupportFragmentManager());
		mHelpViewPager.setAdapter(helpViewAdapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processNotificationIntent(intent);
	}

	private void doLoginProcess() {
		if(mLoginManager.hasLoginInfo(this)) {
			requestLogin(mLoginManager.getSavedUserInfo(this));
		} else {
			showLoginDialog();
		}
	}

	private void initActivityHeaderView() {
		//액션바 처리
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		ivHome = (ImageView) findViewById(R.id.logo);
		ivHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFm.getBackStackEntryCount() > 0) {
					mFm.popBackStack();
				}
			}
		});

		findViewById(R.id.fl_more).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			}
		});
	}

	public void setHeaderView(int img_id, String titleText) {
		ivHome.setImageResource(img_id);
		tvTitle.setText(titleText);
	}

	private void initDrawerView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.drawer_menu);

        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
		mDrawerAdapter = new DrawerAdapter(mDrawerSelectedListener);
        mDrawerList.setAdapter(mDrawerAdapter);
		mDrawerLayout.setVisibility(View.GONE);
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
				requestLogin(mLoginManager.getSavedUserInfo(this));
            } else {
				showLoginDialog();
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
				SchoolLog.i("MainActivity.java ",
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
			SchoolLog.e("LDK", "|Registration not found.|");
			return "";
		}

		int registeredVersion = PreferenceUtil.getInstance(getApplicationContext()).appVersion();
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			SchoolLog.i("LDK", "|App version changed.|");
			// _textStatus.append("\n App version changed.\n");
			return "";
		}
		SchoolLog.i("LDK", "registrationId:" + registrationId);
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
				SchoolLog.e("LDK", "|" + msg + "|");
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

	LoginDialogListener mLoginListener = new LoginDialogListener() {
		@Override
		public void doLogin(MemberVO member) {
			requestLogin(member);
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

		@Override
		public void onPreView() {
			mLoginDialog.dismiss();
			setLayoutVisibility(false);
		}
	};

	private void requestLogin(MemberVO memberVO) {
		//2016-03-17 로고 보여주기
		//LoadingDialog.showLoading(this);
		IntroDialog.showLoading(this);

		mLoginManager.doLogIn(memberVO, this, this);
	}

	@Override
	public void onSuccess() {
		//LoadingDialog.hideLoading();
		hideLoginDialog();

		setLayoutVisibility(true);

//		if (mLoginManager.getLoginUser().is_parent == 1) {
			mDrawerAdapter.setDrawerMenu(mLoginManager.getLoginUser().is_parent == 1);
			mFm.beginTransaction().replace(R.id.content_frame,  new FamilyMembersFragment()).commit();
//		} else {
//			mDrawerAdapter.setDrawerMenu(false);
//			mFm.beginTransaction().replace(R.id.content_frame,  HealthMainFragment.newInstance(mLoginManager.getLoginUser())).commit();
//		}
		mDrawerAdapter.notifyDataSetChanged();

		processNotificationIntent(getIntent());

		//부모의 경우는 위치서비스와 활동량 서비스를 하지 않는다.
		if (!PreferenceUtil.getInstance(this).isParent()) {
			Intent intent = new Intent(this, MyLocationService.class);
			startService(intent);
			Intent stepIntent = new Intent(this, StepCounterService.class);
			startService(stepIntent);
			if (PreferenceUtil.getInstance(this).isSOSEnabled()) {
				Intent sosIntent = new Intent(this, SOSIconService.class);
				startService(sosIntent);
			}
		}

		//지역 구분 로고를 보여준다. 부천, 시흥, none
		int area = 0;
		try {
			for(MemberVO member: LoginManager.getInstance().getMemberList()) {
                if (member.is_parent == 0) {
					String gugun = member.mSchoolVO.gugun;
					Log.d("LDK", "gugun:" + gugun);
/*					if ("부천시".equals(gugun)) {
						area = 1;
						break;
					}*/
					if ("시흥시".equals(gugun)) {
						area = 2;
						break;
					}
				}
            }
		} catch (LoginMemberNullpointerException e) {
		}

		IntroDialog.showArea(area);
	}

	@Override
	public void onFail() {
		//LoadingDialog.hideLoading();
		IntroDialog.hideLoading();

		PreferenceUtil.getInstance(this).putHomeId("");
		Toast.makeText(this, "홈아이디가 존재하지 않거나 가족등록이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
		showLoginDialog();
	}

	DrawerSelectedListener mDrawerSelectedListener = new DrawerSelectedListener() {
			@Override
			public void onSelected(DrawerAdapter.DRAWER_MENU menu) {
				switch(menu) {
					case NOTICE:
						startDrawerMenuActivity(new Intent(MainActivity.this, AppNoticeActivity.class));
						break;
					case QnA:
						startDrawerMenuActivity(new Intent(MainActivity.this, QnAActivity.class));
						break;
					case DEV_INFO:
						startDrawerMenuActivity(new Intent(MainActivity.this, DevInfoActivity.class));
						break;
					case SERVICE_ASK:
					case DROP_OUT:
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "15441284"));
						startActivity(intent);
						break;
					case DIET_MENU:
						startDrawerMenuActivity(new Intent(MainActivity.this, SchoolDietMenuUploadActivity.class));
						break;
					case HOMEID:
						modifyFamilyName();
						break;
					case SOS_ICON:
						startDrawerMenuActivity(new Intent(MainActivity.this, SOSSettingActivity.class));
						break;
				}
			}

		private void modifyFamilyName() {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			}

			ModifyFamilyNameDialogFragment dialogFragment = new ModifyFamilyNameDialogFragment();
			dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			dialogFragment.setModifyListener(new ModifyFamilyNameDialogFragment.OnModifyListener() {
				@Override
				public void onModify(String newName) {
					doModifyFamilyName(newName);
				}
			});
			dialogFragment.show(getSupportFragmentManager(), "modifyDialog");
		}
	};

	private void doModifyFamilyName(final String newName) {
		LoadingDialog.showLoading(MainActivity.this);
		try {
			String url = Constant.HOST + Constant.API_GET_MODIFYHOME;

			JSONObject json = new JSONObject();
			json.put("home_id", PreferenceUtil.getInstance(MainActivity.this).getHomeId());
			json.put("new_home_id", newName);

			SchoolLog.d("LDK", "url:" + url);
			SchoolLog.d("LDK", "input parameter:" + json.toString(1));

			new AQuery(MainActivity.this).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {
						LoadingDialog.hideLoading();
						SchoolLog.d("LDK", "result:" + object.toString(1));

						if (status.getCode() != 200) {
							return;
						}

						if ("0".equals(object.getString("result"))) {
							if(mFm.getBackStackEntryCount() == 0) {
								setHeaderView(R.drawable.home, newName);
							}
							LoginManager.getInstance().getLoginUser().home_id = newName;
							PreferenceUtil.getInstance(MainActivity.this).putHomeId(newName);
							mDrawerAdapter.notifyDataSetChanged();

						} else {
							Util.showToast(MainActivity.this, object.getString("msg"));
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

	private void startDrawerMenuActivity(Intent intent) {
		startActivity(intent);
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	private void processNotificationIntent(Intent intent) {
		int desFragment = intent.getIntExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, -1);

		if (desFragment == Constant.NOTIFICATION_CONSULT) {
			int category = intent.getIntExtra(Constant.CATEGORY, -1);
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
					ConsultChattingFragment.newInstance(mLoginManager.getLoginUser(), ConsultType.findConsultTypeByConsultCode(category))
			).addToBackStack(null).commitAllowingStateLoss();
		} else if (desFragment == Constant.NOTIFICATION_QNA) {
			Intent qnaIntent = new Intent(MainActivity.this, QnAActivity.class);
			qnaIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			this.startActivity(qnaIntent);
		} else if (desFragment == Constant.NOTIFICATION_SCHOOL_ALIMI) {
			int school_id = intent.getIntExtra(Constant.SCHOOL_ID, -1);
			MemberVO memberVO = mLoginManager.findMember(school_id);
			if (memberVO != null) {
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
						SchoolNoticePagerFragment.newInstance(memberVO)
				).addToBackStack(null).commitAllowingStateLoss();
			}
		} else if (desFragment == Constant.NOTIFICATION_STEP) {
			if (mLoginManager.getLoginUser().isVIPUser()) {
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
						WalkingPagerFragment.newInstance(mLoginManager.getLoginUser())
				).addToBackStack(null).commitAllowingStateLoss();
			}
		} else if (desFragment == Constant.NOTIFICATION_NOTICE) {
			Intent qnaIntent = new Intent(MainActivity.this, AppNoticeActivity.class);
			qnaIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			this.startActivity(qnaIntent);
		} else if (desFragment == Constant.NOTIFICATION_GEOFENCE) {
			int member_id = intent.getIntExtra("member_id", 0);
			Log.d("LDK", "member_id:" + member_id);
			MemberVO memberVO = mLoginManager.findMemberByID(member_id);
			if (memberVO != null) {
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
						GeofenceFragment.newInstance(memberVO)
				).addToBackStack(null).commitAllowingStateLoss();
			}
		}
	}

	private View.OnClickListener mClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			setLayoutVisibility(true);
			switch (id) {
				case R.id.btn_login:
					showLoginDialog();
					break;
				case R.id.btn_sign_up:
					Intent intent = new Intent(MainActivity.this, RegisterDialogActivity.class);
					startActivityForResult(intent, REQ_DIALOG_SIGNUP);
					break;
				default:
					break;
			}
		}
	};

	private void setLayoutVisibility(boolean isMainLayout) {
		hearderView.setVisibility(isMainLayout?View.VISIBLE:View.GONE);
		mDrawerLayout.setVisibility(isMainLayout?View.VISIBLE:View.GONE);
		llPreviewLayout.setVisibility(isMainLayout?View.GONE:View.VISIBLE);
	}

	private class HelpViewAdater extends FragmentStatePagerAdapter {
		private static final int PREVIEW_NUM = 4;
		public HelpViewAdater(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return PREVIEW_NUM;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return HelpViewFragment.newInstance(R.drawable.help_01);
				case 1:
					return HelpViewFragment.newInstance(R.drawable.help_02);
				case 2:
					return HelpViewFragment.newInstance(R.drawable.help_03);
				case 3:
					return HelpViewFragment.newInstance(R.drawable.help_04);
				default:
					return null;
			}
		}
	}
}
