package com.aura.smartschool.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.FindSchoolActivity;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class MemberSaveDialogActivity extends Activity {
	private int mMode; //0: add, 1:update
	
	private Context mContext;
	private AQuery mAq;
	
	TextView tvTitle, tvParent, tvStudent;
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
	
	private MemberVO mMember;
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
		tvTitle = (TextView) findViewById(R.id.tvTitle);
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
		findViewById(R.id.ll_terms).setVisibility(View.GONE);

        spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_sex, R.layout.spinner_text_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(spinnerAdapter);
        final GregorianCalendar calendar = new GregorianCalendar();
        tvBirthDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(MemberSaveDialogActivity.this,
						android.R.style.Theme_Holo_Light_Dialog_MinWidth,
						dateSetListener,
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				datePickerDialog.show();
			}
		});

		tvParent.setOnClickListener(mClick);
		tvStudent.setOnClickListener(mClick);
        tvSchoolName.setOnClickListener(mClick);
		btn_register.setOnClickListener(mClick);
		fl_user_image.setOnClickListener(mClick);
		
		//et_id : mdn 필드로 활용
		et_id.setHint("phone number");
		et_id.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		//update or add 인지 판단
		mMode = getIntent().getExtras().getInt("mode");
		mMember = (MemberVO)getIntent().getSerializableExtra("member");
		if (mMode == MainActivity.MOD_UPDATE) {
			tvTitle.setText("가족 구성원 수정");
			
			et_id.setText(mMember.mdn);
			et_name.setText(mMember.name);
			et_relation.setText(mMember.relation);
			if(!TextUtils.isEmpty(mMember.photo)) {
				iv_user_image.setImageBitmap(Util.StringToBitmap(mMember.photo));
			}
			if(mMember.is_parent == 0) {
				tvSchoolName.setText(mMember.mSchoolVO.school_name);
				tvBirthDay.setText(mMember.birth_date);
				et_school_grade.setText(mMember.mSchoolVO.school_grade);
				et_school_class.setText(mMember.mSchoolVO.school_class);
				spinnerSex.setSelection(mMember.sex.equals("M") ? 1 : 2);
				tvStudent.performClick(); //학생탭 선택
				tvStudent.setVisibility(View.VISIBLE);
				tvParent.setVisibility(View.GONE);
			} else {
				tvStudent.setVisibility(View.GONE);
				tvParent.setVisibility(View.VISIBLE);
			}
			
		} else {
			tvTitle.setText("가족 구성원 추가");
		}
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
 
        switch (requestCode) {
        case MainActivity.REQ_CODE_PICK_IMAGE:
            if (resultCode == RESULT_OK) {
                if (resultIntent != null) {
                    String filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";

                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    // temp.jpg파일을 Bitmap으로 디코딩한다.
                    mMember.photo = Util.BitmapToString(selectedImage, 200, 200);

                    //iv_user_image.setImageBitmap(selectedImage); 
                    iv_user_image.setImageBitmap(selectedImage);
                    //temp.jpg파일을 이미지뷰에 씌운다.
                }
            }
            break;
        case MainActivity.REQ_CODE_FIND_SCHOOL:
            if (resultCode == RESULT_OK) {
                mSchoolVO = (SchoolVO) resultIntent.getSerializableExtra("school");
                tvSchoolName.setText(mSchoolVO.school_name);
            }
            break;

        }
    }
    
    private void getSave() {
		LoadingDialog.showLoading(this);
		try {
			String url;
			JSONObject json = new JSONObject();

			if(mMode == MainActivity.MOD_ADD) {
				url = Constant.HOST + Constant.API_ADD_MEMBER;
				json.put("home_id", mMember.home_id);

			} else {
				url = Constant.HOST + Constant.API_UPDATE_MEMBER;
				json.put("member_id", mMember.member_id);
			}

			if(!TextUtils.isEmpty(mMember.mdn)) {
				json.put("mdn", mMember.mdn);
			}
			//json.put("gcm_id", PreferenceUtil.getInstance(MemberSaveDialogActivity.this).getRegID());
			json.put("is_parent", mMember.is_parent);
			json.put("name", mMember.name);
			json.put("relation", mMember.relation);
			json.put("photo", mMember.photo);

			if(mMember.is_parent == 0) {
				json.put("school_id", mMember.mSchoolVO.school_id);
				json.put("school_grade", mMember.mSchoolVO.school_grade);
				json.put("school_class", mMember.mSchoolVO.school_class);
				json.put("sex", mMember.sex);
				json.put("birth_date", mMember.birth_date);
			}

			SchoolLog.d("LDK", "url:" + url);
			SchoolLog.d("LDK", "input parameter:" + json.toString(1));

			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					LoadingDialog.hideLoading();
					try {
						if(status.getCode() != 200) {

							return;
						}

						SchoolLog.d("LDK", "result:" + object.toString(1));

						if("0".equals(object.getString("result"))) {
							setResult(RESULT_OK);
							finish();
						} else {
							Util.showToast(MemberSaveDialogActivity.this, object.getString("msg"));
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
				tvParent.setBackgroundColor(getResources().getColor(R.color.orange));
				tvParent.setTextColor(getResources().getColor(R.color.white));
				mMember.is_parent = 1;
				tvStudent.setBackgroundColor(getResources().getColor(R.color.transparent));
				tvStudent.setTextColor(getResources().getColor(R.color.orange));
				school_info.setVisibility(View.GONE);
				break;
			case R.id.tvStudent:
				tvParent.setBackgroundColor(getResources().getColor(R.color.transparent));
				tvParent.setTextColor(getResources().getColor(R.color.orange));
				mMember.is_parent = 0;
				tvStudent.setBackgroundColor(getResources().getColor(R.color.orange));
				tvStudent.setTextColor(getResources().getColor(R.color.white));
				school_info.setVisibility(View.VISIBLE);
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
            case R.id.tv_school_name :
                Intent findSchoolIntent = new Intent(MemberSaveDialogActivity.this, FindSchoolActivity.class);
                startActivityForResult(findSchoolIntent, MainActivity.REQ_CODE_FIND_SCHOOL);
                break;
			case R.id.btn_register:
				String id = et_id.getText().toString();
				/*if(TextUtils.isEmpty(et_id.getText().toString())){
					Util.showToast(mContext, "전화번호를 입력하세요.");
					return;
				}*/
				if(TextUtils.isEmpty(et_name.getText().toString())){
					Util.showToast(mContext, "이름을 입력하세요.");
					return;
				}
				if (!Pattern.matches("^[가-힣]*$", et_name.getText().toString())) {
					Util.showToast(mContext, "이름은 한글만 입력 가능합니다.");
					return;
				}
				if(TextUtils.isEmpty(et_relation.getText().toString())){
					Util.showToast(mContext, "관계를 입력하세요.");
					return;
				}
				if(mMember.is_parent == 0) {
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
				
				mMember.mdn= et_id.getText().toString();
				mMember.name = et_name.getText().toString();
				mMember.relation = et_relation.getText().toString();
//				mMember.mdn = Util.getMdn(mContext);
                mMember.birth_date = tvBirthDay.getText().toString();
                //TODO :차후 관계를 spinner 로 변경해서 성별을 가져오도록 수정
                mMember.sex = (spinnerSex.getSelectedItemPosition() == 1 ? "M": "F");
                if(mSchoolVO != null) {
                    mMember.mSchoolVO = mSchoolVO;
                }
				mMember.mSchoolVO.school_grade = et_school_grade.getText().toString();
				mMember.mSchoolVO.school_class = et_school_class.getText().toString();
				
				getSave();
				
				break;
				
			default:
				break;
			}
		}
	};
}
