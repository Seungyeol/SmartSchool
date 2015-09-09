package com.aura.smartschool;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.aura.smartschool.fragment.QnAListFragment;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class QnAActivity extends FragmentActivity {

    private FragmentManager mFm;
    private View btnLogo;
    private OnBackPressListener listener;

    public interface OnBackPressListener {
        void onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);
        btnLogo = findViewById(R.id.logo);
        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });
        mFm = getSupportFragmentManager();
        mFm.beginTransaction().replace(R.id.content_frame,  new QnAListFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if (listener != null) {
            listener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void setBackKeyListener(OnBackPressListener listener) {
        this.listener = listener;
    }
}
