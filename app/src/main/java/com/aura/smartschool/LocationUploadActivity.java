package com.aura.smartschool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.adapter.CategoryAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;


public class LocationUploadActivity extends Activity {

    private AQuery mAq;
    private int category = 0;

    private EditText et_title;
    private EditText et_content;
    private ImageView iv_picture;
    private Button btn_submit;

    private String imageDataString ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_upload);

        mAq = new AQuery(this);

        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);
        iv_picture = (ImageView) findViewById(R.id.iv_picture);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        getActionBar().hide();

        String [] strings = {getResources().getString(R.string.category_prompt),
                getResources().getString(R.string.map_1),
                getResources().getString(R.string.map_2),
                getResources().getString(R.string.map_3),
                getResources().getString(R.string.map_4),
                getResources().getString(R.string.map_5),
                getResources().getString(R.string.map_6)};

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new CategoryAdapter(this, R.layout.spinner_category, strings));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = position;
                Log.d("LDK", "position:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        iv_picture.setOnClickListener(mClick);
        btn_submit.setOnClickListener(mClick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MainActivity.REQ_CODE_PICK_IMAGE:
                    if (data != null) {
                        String filePath = Util.getTempFile().getAbsolutePath();

                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        // temp.jpg파일을 Bitmap으로 디코딩한다.
                        imageDataString = Util.BitmapToString(selectedImage);

                        iv_picture.setImageBitmap(selectedImage);
                    }
                    break;
            }
        }
    }

    private void doSave() {
        LoadingDialog.showLoading(this);
        try {
            String url = Constant.HOST + Constant.API_ADD_AREA;

            JSONObject json = new JSONObject();
            json.put("lat", PreferenceUtil.getInstance(this).getLat());
            json.put("lng", PreferenceUtil.getInstance(this).getLng());
            json.put("title", et_title.getText().toString());
            json.put("content",et_content.getText().toString());
            json.put("category", category);
            json.put("picture", imageDataString);

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

                        if(object.getInt("result") == 0) {
                            finish();
                            Util.showToast(LocationUploadActivity.this, "등록되었습니다.");
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
            switch(v.getId()) {
                case R.id.iv_picture:
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");              // 모든 이미지
                    intent.putExtra("crop", "true");        // Crop기능 활성화
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX",  500);
                    intent.putExtra("outputY",  500);
                    intent.putExtra("aspectX",  1);
                    intent.putExtra("aspectY",  1);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Util.getTempFile()));     // 임시파일 생성
                    intent.putExtra("outputFormat",         // 포맷방식
                            Bitmap.CompressFormat.JPEG.toString());

                    startActivityForResult(intent, MainActivity.REQ_CODE_PICK_IMAGE);
                    break;

                case R.id.btn_submit:
                    if(category == 0){
                        Util.showToast(LocationUploadActivity.this, "카테고리를 선택하세요.");
                        return;
                    }
                    if(TextUtils.isEmpty(et_title.getText().toString())){
                        Util.showToast(LocationUploadActivity.this, "제목을 입력하세요.");
                        return;
                    }
                    if(TextUtils.isEmpty(et_content.getText().toString())){
                        Util.showToast(LocationUploadActivity.this, "설명을 입력하세요.");
                        return;
                    }
                    if(TextUtils.isEmpty(imageDataString)){
                        Util.showToast(LocationUploadActivity.this, "이미지를 입력하세요.");
                        return;
                    }
                    doSave();
                    break;
            }
        }
    };

}
