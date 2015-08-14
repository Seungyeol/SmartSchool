package com.aura.smartschool;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.adapter.QnAAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.fragment.FamilyMembersFragment;
import com.aura.smartschool.fragment.QnAListFragment;
import com.aura.smartschool.vo.BoardVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class QnAActivity extends FragmentActivity {

    private FragmentManager mFm;

    private OnBackPressListener listener;

    public interface OnBackPressListener {
        void onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);
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
