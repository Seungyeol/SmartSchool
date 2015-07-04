package com.aura.smartschool.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.FindSchoolActivity;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterDialogActivity extends Activity {
    private Context mContext;
	private AQuery mAq;
	//private LoginListener mListener;
	
	TextView tvParent, tvStudent;
	LinearLayout school_info;
	FrameLayout fl_user_image;
	EditText et_id, et_name, et_relation;
	ImageView iv_user_image;
	TextView tvBirthDay;
	TextView tvSchoolName;
	EditText et_school_grade, et_school_class;
    Spinner spinnerSex;
	Button btn_register;
    ArrayAdapter<CharSequence> spinnerAdapter;

	private int mIs_parent = 1; //default: 부모
	private String imageDataString ="";

    private SchoolVO mSchoolVO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.6f;
		getWindow().setAttributes(lpWindow);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		setContentView(R.layout.dialog_register);

		mAq = new AQuery(this);
		mContext = this;
		school_info = (LinearLayout) findViewById(R.id.school_info);
		tvParent = (TextView) findViewById(R.id.tvParent);
		tvStudent = (TextView) findViewById(R.id.tvStudent);
		iv_user_image = (ImageView) findViewById(R.id.iv_user_image);
		fl_user_image = (FrameLayout) findViewById(R.id.fl_user_image);
		et_id = (EditText) findViewById(R.id.et_id);
		et_name = (EditText) findViewById(R.id.et_name);
		et_relation = (EditText) findViewById(R.id.et_relation);
        tvBirthDay = (TextView) findViewById(R.id.tv_birthday);
		tvSchoolName = (TextView) findViewById(R.id.tv_school_name);
		et_school_grade = (EditText) findViewById(R.id.et_school_grade);
		et_school_class = (EditText) findViewById(R.id.et_school_class);
		btn_register = (Button) findViewById(R.id.btn_register);
        spinnerSex = (Spinner) findViewById(R.id.spinner_sex);
        spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_sex, R.layout.spinner_text_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(spinnerAdapter);
        final GregorianCalendar calendar = new GregorianCalendar();
        tvBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterDialogActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

		tvParent.setOnClickListener(mClick);
		tvStudent.setOnClickListener(mClick);
        tvSchoolName.setOnClickListener(mClick);
		btn_register.setOnClickListener(mClick);
		fl_user_image.setOnClickListener(mClick);
	}

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tvBirthDay.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
        }
    };

    protected void onActivityResult(int requestCode, int resultCode,
            Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MainActivity.REQ_CODE_PICK_IMAGE:
                    if (resultIntent != null) {
                        String filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";

                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        // temp.jpg파일을 Bitmap으로 디코딩한다.
                        imageDataString = Util.BitmapToString(selectedImage);

                        //iv_user_image.setImageBitmap(selectedImage);
                        iv_user_image.setImageBitmap(selectedImage);
                        //temp.jpg파일을 이미지뷰에 씌운다.
                    }
                    break;
                case MainActivity.REQ_CODE_FIND_SCHOOL:
                    mSchoolVO = (SchoolVO) resultIntent.getSerializableExtra("school");
                    tvSchoolName.setText(mSchoolVO.school_name);
                    break;
            }
        }
    }
    
    private void getRegister(MemberVO member) {
		LoadingDialog.showLoading(this);
		try {
			String url = Constant.HOST + Constant.API_SIGNUP;

			JSONObject json = new JSONObject();
			json.put("home_id", member.home_id);
			json.put("mdn", member.mdn);
			json.put("gcm_id", PreferenceUtil.getInstance(RegisterDialogActivity.this).getRegID());
			json.put("is_parent", member.is_parent);
			json.put("name", member.name);
			json.put("relation", member.relation);
			json.put("photo", imageDataString);
			if(mIs_parent==0) {
				json.put("school_name", member.mSchoolVO.school_name);
				json.put("school_grade", member.mSchoolVO.school_grade);
				json.put("school_ban", member.mSchoolVO.school_class);
			}

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
							//home id 저장
							PreferenceUtil.getInstance(mContext).putHomeId(et_id.getText().toString());
							//
							setResult(RESULT_OK);
							finish();
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
	
    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }
    
    private boolean isSdcardMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
 
        return false;
    }
	
    private File getTempFile() {
        if (isSdcardMounted()) {
            File f = new File(Environment.getExternalStorageDirectory(), // 외장메모리 경로
            		MainActivity.TEMP_PHOTO_FILE);
            try {
                f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성
            } catch (IOException e) {
            }
 
            return f;
        } else
            return null;
    }
	
	View.OnClickListener mClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tvParent:
				tvParent.setBackgroundColor(0xFF930D03);
				tvParent.setTextColor(0xFFF0F0F0);
				mIs_parent = 1;
				tvStudent.setBackgroundColor(0xFFF0F0F0);
				tvStudent.setTextColor(0xFF930D03);
				school_info.setVisibility(View.GONE);
				break;
			case R.id.tvStudent:
				tvParent.setBackgroundColor(0xFFF0F0F0);
				tvParent.setTextColor(0xFF930D03);
				mIs_parent = 0;
				tvStudent.setBackgroundColor(0xFF930D03);
				tvStudent.setTextColor(0xFFF0F0F0);
				school_info.setVisibility(View.VISIBLE);
				break;

            case R.id.tv_school_name:
                Intent findSchoolIntent = new Intent(RegisterDialogActivity.this, FindSchoolActivity.class);
                startActivityForResult(findSchoolIntent, MainActivity.REQ_CODE_FIND_SCHOOL);
                break;
				
			case R.id.fl_user_image:
				Intent intent = new Intent(Intent.ACTION_PICK,
	                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	            intent.setType("image/*");              // 모든 이미지
	            intent.putExtra("crop", "true");        // Crop기능 활성화
	            intent.putExtra("scale", true);
	            intent.putExtra("outputX",  200);
	            intent.putExtra("outputY",  200);
	            intent.putExtra("aspectX",  1);
	            intent.putExtra("aspectY",  1);

	            intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());     // 임시파일 생성
	            intent.putExtra("outputFormat",         // 포맷방식
	                    Bitmap.CompressFormat.JPEG.toString());

	            startActivityForResult(intent, MainActivity.REQ_CODE_PICK_IMAGE);
	            break;
				
			case R.id.btn_register:
				String id = et_id.getText().toString();
				if(TextUtils.isEmpty(et_id.getText().toString())){
					Util.showToast(mContext, "Home ID를 입력하세요.");
					return;
				}
				if(TextUtils.isEmpty(et_name.getText().toString())){
					Util.showToast(mContext, "이름을 입력하세요.");
					return;
				}
				if(TextUtils.isEmpty(et_relation.getText().toString())){
					Util.showToast(mContext, "관계를 입력하세요.");
					return;
				}
				if(mIs_parent==0) {
                    if(TextUtils.isEmpty(tvBirthDay.getText().toString())){
                        Util.showToast(mContext, "생년월일을 입력하세요.");
                        return;
                    }
					if(TextUtils.isEmpty(tvSchoolName.getText().toString())){
						Util.showToast(mContext, "학교명을 입력하세요.");
						return;
					}
					if(TextUtils.isEmpty(et_school_grade.getText().toString())){
						Util.showToast(mContext, "학년을 입력하세요.");
						return;
					}
					if(TextUtils.isEmpty(et_school_class.getText().toString())){
						Util.showToast(mContext, "반을 입력하세요.");
						return;
					}
                    if(spinnerSex.getSelectedItemPosition() == 0){
                        Util.showToast(mContext, "성별을 입력하세요.");
                        return;
                    }
				}
				
				MemberVO member = new MemberVO();
				member.home_id = et_id.getText().toString();
				member.name = et_name.getText().toString();
				member.relation = et_relation.getText().toString();
				member.mdn = Util.getMdn(mContext);
                member.birth_date = tvBirthDay.getText().toString();
                //TODO :차후 관계를 spinner 로 변경해서 성별을 가져오도록 수정
                member.sex = (spinnerSex.getSelectedItemPosition() == 1 ? "M": "F");
				member.is_parent = mIs_parent;
                if(mSchoolVO != null) {
                    member.mSchoolVO = mSchoolVO;
                }
				member.mSchoolVO.school_grade = et_school_grade.getText().toString();
				member.mSchoolVO.school_class = et_school_class.getText().toString();
				
				getRegister(member);
				break;
				
			default:
				break;
			}
		}
	};
}
