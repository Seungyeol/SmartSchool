package com.aura.smartschool;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.adapter.AppNoticeAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.vo.AppNoticeVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class AppNoticeActivity extends Activity {

    private ArrayList<AppNoticeVO> appNotiList;

    private RecyclerView mNoticeListView;
    private AppNoticeAdapter mNoticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_notice);

        appNotiList = new ArrayList<>();

        mNoticeListView = (RecyclerView) findViewById(R.id.app_notice_list);
        mNoticeListView.setLayoutManager(new LinearLayoutManager(this));
        mNoticeAdapter = new AppNoticeAdapter();
        mNoticeListView.setAdapter(mNoticeAdapter);

        loadAppNotice();
    }

    private void loadAppNotice() {
        LoadingDialog.showLoading(this);
        String url = Constant.HOST + Constant.API_GET_APP_NOTI_LIST;

        new AQuery(this).post(url, new JSONObject(), JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                LoadingDialog.hideLoading();
                try {
                    if (status.getCode() != 200) {
                        showToast("공지사항 불러오기에 실패하였습니다.");
                        return;
                    }

                    Log.d("LDK", "result:" + object.toString(1));

                    if ("0".equals(object.getString("result"))) {
                        JSONArray array = object.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            AppNoticeVO noti = new AppNoticeVO();
                            noti.notiId = json.getInt("noti_id");
                            noti.title = json.getString("title");
                            noti.content = json.getString("content");
                            noti.created = json.getString("created");
                            appNotiList.add(noti);
                        }
                        mNoticeAdapter.setNotiList(appNotiList);
                        mNoticeAdapter.notifyDataSetChanged();
                    } else {
                        showToast("공지사항 불러오기에 실패하였습니다.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("공지사항 불러오기에 실패하였습니다.");
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
