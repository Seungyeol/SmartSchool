package com.aura.smartschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.adapter.SchoolListAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.vo.SchoolVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-21.
 */
public class FindSchoolActivity extends Activity {
    private static final String TAG = FindSchoolActivity.class.getSimpleName();

    private AQuery mAq;

    private Handler mHandler = new Handler();
    private Runnable mTask;

    private EditText mSearchSchool;
    private RecyclerView mSchoolListView;
    private ProgressBar mProgressbar;

    private SchoolListAdapter mSchoolListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_school);
        mAq = new AQuery(this);
        initViews();
    }

    private void initViews() {
        mSearchSchool = (EditText) findViewById(R.id.et_search_school);
        mSchoolListView = (RecyclerView) findViewById(R.id.school_list);
        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);

        mSchoolListView.setLayoutManager(new LinearLayoutManager(this));
        mSchoolListAdapter = new SchoolListAdapter();
        mSchoolListView.setAdapter(mSchoolListAdapter);
        mSchoolListAdapter.setOnItemClickListener(new SchoolListAdapter.SchoolItemClickListener() {
            @Override
            public void onItemClicked(SchoolVO school) {
                Intent intent = new Intent();
                intent.putExtra("school", school);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        mSearchSchool.addTextChangedListener(mWatcher);
    }

    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(final Editable s) {
            if (mTask != null) {
                mHandler.removeCallbacks(mTask);
            }

            if (!s.toString().isEmpty()) {
                mTask = new Runnable() {
                    @Override
                    public void run() {
                        doSearchSchool(s.toString());
                    }
                };

                mHandler.postDelayed(mTask, 1000);
            }
        }
    };

    private void doSearchSchool(String s) {
        mProgressbar.setVisibility(View.VISIBLE);
        try {
            String url = Constant.HOST + Constant.API_GET_SCHOOLLIST;

            JSONObject json = new JSONObject();
            json.put("school_name", s);

            Log.d(TAG, "doSearchSchool >> url:" + url);
            Log.d(TAG, "doSearchSchool >> input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    mProgressbar.setVisibility(View.GONE);
                    try {
                        if(status.getCode() != 200) {
                            return;
                        }
                        Log.d(TAG, "doSearchSchool >> result :" + object.toString(1));

                        showSchoolList(parseSchoolList(object.getJSONArray("data")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSchoolList(ArrayList<SchoolVO> schoolList) {
        mSchoolListAdapter.setSchoolList(schoolList);
        mSchoolListAdapter.notifyDataSetChanged();
    }

    private ArrayList<SchoolVO> parseSchoolList(JSONArray array) {
        ArrayList<SchoolVO> schoolList = new ArrayList<>();
        for(int i=0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                SchoolVO school = new SchoolVO();
                school.school_id = obj.getInt("school_id");
                school.school_name = obj.getString("school_name");
                school.gubun1 = obj.getString("gubun1");
                school.gubun2 = obj.getString("gubun2");
                school.zipcode = obj.getString("zipcode");
                school.address = obj.getString("address");
                school.new_address = obj.getString("new_address");
                school.lat = obj.getString("lat");
                school.lng = obj.getString("lng");
                school.homepage = obj.getString("homepage");
                school.fax = obj.getString("fax");
                school.contact = obj.getString("contact");
                schoolList.add(school);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return schoolList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mHandler.removeCallbacks(mTask);
        }
    }
}
