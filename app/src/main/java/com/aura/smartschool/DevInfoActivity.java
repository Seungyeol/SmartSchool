package com.aura.smartschool;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.dialog.TermsDialogFragment;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.VersionVO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class DevInfoActivity extends FragmentActivity {

    private View btnLogo;

    private TextView tvCurrentVersion;
    private TextView tvLatestVersion;
    private TextView tvUpdate;

    private View tvTerms;
    private View tvPrivateTerms;
    private View tvLocationTerms;

    private View tvShowTerms;

    private VersionVO latestVersion = new VersionVO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_info);

        btnLogo = findViewById(R.id.logo);
        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        initVersionViews();
        initTermsViews();
    }

    private void initVersionViews() {
        loadAppVersion();
        tvCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
        tvLatestVersion = (TextView) findViewById(R.id.tv_latest_version);
        tvUpdate = (TextView) findViewById(R.id.tv_update);

        tvCurrentVersion.setText("v" + getCurrentAppVersion());
        tvLatestVersion.setText("");
    }

    private String getCurrentAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void initTermsViews() {
        tvTerms = findViewById(R.id.tv_terms);
        tvPrivateTerms = findViewById(R.id.tv_private_terms);
        tvLocationTerms = findViewById(R.id.tv_location_terms);
        tvShowTerms = findViewById(R.id.tv_show_terms);

        tvShowTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TermsDialogFragment termsDialogFragment = new TermsDialogFragment();
                termsDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
                termsDialogFragment.setButtonVisible(false);
                termsDialogFragment.show(getSupportFragmentManager(), "termsDialog");
            }
        });
    }

    private void loadAppVersion() {
        LoadingDialog.showLoading(this);
        String url = Constant.HOST + Constant.API_GET_OS_INFO;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("os_name", "android");
            new AQuery(this).post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            return;
                        }

                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if ("0".equals(object.getString("result"))) {
                            JSONObject data = object.getJSONObject("data");
                            latestVersion.osName = data.getString("os_name");
                            latestVersion.versionName = data.getString("version_name");
                            latestVersion.versionCode = data.getInt("version_code");
                            setLatestVersion();
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (JSONException e) {

        }
    }

    private void setLatestVersion() {
        tvLatestVersion.setText("v" + latestVersion.versionName);
        if (getCurrentAppVersion().compareTo(latestVersion.versionName) < 0) {
            tvUpdate.setText("업데이트");
            tvUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(intent);
                }
            });
        } else {
            tvUpdate.setText("최신버전");
            tvUpdate.setOnClickListener(null);
        }
    }
}
