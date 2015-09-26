package com.aura.smartschool;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.aura.smartschool.fragment.HelpViewFragment;
import com.aura.smartschool.utils.PreferenceUtil;

import org.jsoup.helper.StringUtil;

/**
 * Created by Administrator on 2015-09-02.
 */
public class StartPreviewActivity extends FragmentActivity {

    private FragmentStatePagerAdapter startViewAdapter;
    private ViewPager mStartViewPager;
    private View btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceUtil.getInstance(this).getStartState() ||
                !StringUtil.isBlank(PreferenceUtil.getInstance(this).getHomeId())) {
            startMainActivity();
        } else {
            PreferenceUtil.getInstance(this).putStartState(true);
            setContentView(R.layout.activity_start_preview);

            btnLogin = findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMainActivity();
                }
            });

            mStartViewPager = (ViewPager) findViewById(R.id.vp_startview);
            startViewAdapter = new StartViewAdater(this.getSupportFragmentManager());
            mStartViewPager.setAdapter(startViewAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private class StartViewAdater extends FragmentStatePagerAdapter {
        private static final int PREVIEW_NUM = 4;
        public StartViewAdater(FragmentManager fragmentManager) {
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
