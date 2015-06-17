package com.aura.smartschool.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;

/**
 * Created by Administrator on 2015-06-18.
 */
public class BaseFragment extends Fragment {
    private TextView tvTitle;
    private ImageView ivHome;

    public BaseFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        View customActionbar = activity.getActionBar().getCustomView();
        tvTitle = (TextView) customActionbar.findViewById(R.id.tvTitle);
        ivHome = (ImageView) customActionbar.findViewById(R.id.logo);
    }

    protected void setActionbar(int img_id, String titleText) {
        ivHome.setImageResource(img_id);
        tvTitle.setText(titleText);
    }
}
