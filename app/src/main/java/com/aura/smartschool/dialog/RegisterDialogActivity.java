package com.aura.smartschool.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterDialogActivity extends Activity {
	private Context mContext;
	private AQuery mAq;
	//private LoginListener mListener;
	
	TextView tvParent, tvStudent;
	LinearLayout school_info;
	FrameLayout fl_user_image;
	EditText et_id, et_name, et_relation;
	ImageView iv_user_image;
	EditText et_school_name, et_school_grade, et_school_class;
	Button btn_register;
	
	private int mIs_parent = 1; //default: 부모
	private String imageDataString ="";

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
		et_school_name = (EditText) findViewById(R.id.et_school_name);
		et_school_grade = (EditText) findViewById(R.id.et_school_grade);
		et_school_class = (EditText) findViewById(R.id.et_school_class);
		btn_register = (Button) findViewById(R.id.btn_register);
		
		tvParent.setOnClickListener(mClick);
		tvStudent.setOnClickListener(mClick);
		btn_register.setOnClickListener(mClick);
		fl_user_image.setOnClickListener(mClick);
	}
	
    protected void onActivityResult(int requestCode, int resultCode,
            Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
 
        switch (requestCode) {
        case MainActivity.REQ_CODE_PICK_IMAGE:
            if (resultCode == RESULT_OK) {
                if (imageData != null) {
                    String filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";

                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    // temp.jpg파일을 Bitmap으로 디코딩한다.
                    imageDataString = Util.BitmapToString(selectedImage);

                    //iv_user_image.setImageBitmap(selectedImage); 
                    iv_user_image.setImageBitmap(selectedImage);
                    //temp.jpg파일을 이미지뷰에 씌운다.
                }
            }
            break;
        }
    }
    
    private void getRegister(MemberVO member) {
		LoadingDialog.showLoading(this);
        String url = Constant.HOST + Constant.API_SIGNUP;

        Map<String, Object> params = new HashMap<>();
        params.put("home_id", member.home_id);
        params.put("mdn", member.mdn);
        params.put("is_parent", member.is_parent);
        params.put("name", member.name);
        params.put("relation", member.relation);
        params.put("photo", imageDataString);
        if(mIs_parent==0) {
            params.put("school_name", member.school_name);
            params.put("school_grade", member.school_grade);
            params.put("school_ban", member.school_ban);
        }

        Log.d("LDK", "url:" + url);
        Log.d("LDK", "input parameter:" + params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                LoadingDialog.hideLoading();
                try {
                    Log.d("LDK", "result:" + object.toString(1));

                    if (status.getCode() != 200) {

                        return;
                    }

                    if ("0".equals(object.getString("result"))) {
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
					if(TextUtils.isEmpty(et_school_name.getText().toString())){
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
				}
				
				MemberVO member = new MemberVO();
				member.home_id = et_id.getText().toString();
				member.name = et_name.getText().toString();
				member.relation = et_relation.getText().toString();
				member.mdn = Util.getMdn(mContext);
				member.is_parent = mIs_parent;
				member.school_name = et_school_name.toString();
				member.school_grade = et_school_grade.toString();
				member.school_ban = et_school_class.toString();
				
				getRegister(member);
				
				break;
				
			default:
				break;
			}
		}
	};
}
