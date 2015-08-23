package com.aura.smartschool;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.aura.smartschool.dialog.TermsDialogFragment;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class DevInfoActivity extends FragmentActivity {

    private View btnLogo;

    private TextView tvCurrentVersion;
    private TextView tvLatestVersion;
    private View tvUpdate;

    private View tvTerms;
    private View tvPrivateTerms;
    private View tvLocationTerms;

    private View tvShowTerms;

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
        tvCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
        tvLatestVersion = (TextView) findViewById(R.id.tv_latest_version);
        tvUpdate = findViewById(R.id.tv_update);

        tvCurrentVersion.setText("v" + getCurrentAppVersion());
        tvLatestVersion.setText("v" + getLatestAppVersion());
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

    private String getLatestAppVersion() {
        return "";
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

}
