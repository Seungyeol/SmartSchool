package com.aura.smartschool;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class SchoolDietMenuUploadActivity extends Activity {

    private AQuery mAq;

    private TextView tvSchool;
    private TextView tvDate;
    private RadioGroup rgrDietType;
    private ImageView iv_picture;
    private TextView tvPictureHint;
    private Button btn_submit;
    private Button btnCamera, btnGallery;

    private String imageDataString ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_diet_menu_upload);

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAq = new AQuery(this);

        tvSchool = (TextView) findViewById(R.id.tv_school);
        tvDate = (TextView) findViewById(R.id.tv_date);
        rgrDietType = (RadioGroup) findViewById(R.id.rgr_diet_type);
        tvPictureHint = (TextView) findViewById(R.id.tv_picture_hint);
        iv_picture = (ImageView) findViewById(R.id.iv_picture);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnGallery = (Button) findViewById(R.id.btnGallery);

        Calendar calendar = Calendar.getInstance();
        tvSchool.setText(LoginManager.getInstance().getLoginUser().mSchoolVO.school_name);
        tvDate.setText(String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        tvDate.setOnClickListener(mClick);
        iv_picture.setOnClickListener(mClick);
        btn_submit.setOnClickListener(mClick);
        btnCamera.setOnClickListener(mClick);
        btnGallery.setOnClickListener(mClick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MainActivity.REQ_CODE_PICK_IMAGE:
                    if (data != null) {
                        tvPictureHint.setVisibility(View.GONE);
                        String filePath = Util.getTempFile().getAbsolutePath();

                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        // temp.jpg파일을 Bitmap으로 디코딩한다.
                        imageDataString = Util.BitmapToString(selectedImage, 600, 480);
                        iv_picture.setImageBitmap(selectedImage);
                    }
                    break;

                case MainActivity.REQ_CODE_CAMERA_IMAGE:
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(Uri.fromFile(Util.getTempFile()), "image/*");
                    intent.putExtra("crop", "true");        // Crop기능 활성화
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX",  600);
                    intent.putExtra("outputY",  480);
                    intent.putExtra("aspectX",  5);
                    intent.putExtra("aspectY",  4);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Util.getTempFile()));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, MainActivity.REQ_CODE_PICK_IMAGE);

                    break;
            }
        }
    }

    private void doSave() {
        LoadingDialog.showLoading(this);
        try {
            String url = Constant.HOST + Constant.API_ADD_DINING;

            JSONObject json = new JSONObject();
            json.put("school_id", LoginManager.getInstance().getLoginUser().mSchoolVO.school_id);
            json.put("dining_date", tvDate.getText());
            if (rgrDietType.getCheckedRadioButtonId() == R.id.rb_breakfast) {
                json.put("category", 1);
            } else if (rgrDietType.getCheckedRadioButtonId() == R.id.rb_lunch) {
                json.put("category", 2);
            } else if (rgrDietType.getCheckedRadioButtonId() == R.id.rb_dinner) {
                json.put("category", 3);
            }
            json.put("image", imageDataString);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if(status.getCode() != 200) {
                            return;
                        }

                        if(object.getInt("result") == 0) {
                            Util.showToast(SchoolDietMenuUploadActivity.this, "등록되었습니다.");
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

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()) {
                case R.id.btnCamera:
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Util.getTempFile()));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, MainActivity.REQ_CODE_CAMERA_IMAGE);
                    break;

                case R.id.iv_picture: //fall-through
                case R.id.btnGallery:
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");              // 모든 이미지
                    intent.putExtra("crop", "true");        // Crop기능 활성화
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX",  1200);
                    intent.putExtra("outputY",  960);
                    intent.putExtra("aspectX",  5);
                    intent.putExtra("aspectY",  4);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Util.getTempFile()));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intent, MainActivity.REQ_CODE_PICK_IMAGE);
                    break;

                case R.id.btn_submit:
                    if(rgrDietType.getCheckedRadioButtonId() == -1){
                        Util.showToast(SchoolDietMenuUploadActivity.this, "아침, 점심, 저녁 설정을 해주세요.");
                        return;
                    }
                    if(TextUtils.isEmpty(imageDataString)){
                        Util.showToast(SchoolDietMenuUploadActivity.this, "이미지를 입력하세요.");
                        return;
                    }
                    doSave();
                    break;
                case R.id.tv_date:
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(SchoolDietMenuUploadActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    tvDate.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
            }
        }
    };

}
