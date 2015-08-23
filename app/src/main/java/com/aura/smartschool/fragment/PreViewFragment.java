package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aura.smartschool.R;

/**
 * Created by Administrator on 2015-08-23.
 */
public class PreViewFragment extends Fragment {

    private int imgResId;
    private ImageView ivPreView;
    private PreViewFragment() {
        // Required empty public constructor
    }

    public static PreViewFragment newInstance(int imgId) {
        PreViewFragment instance = new PreViewFragment();
        Bundle args = new Bundle();
        args.putInt("resId", imgId);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.imgResId = args.getInt("resId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_preview, null);
        ivPreView = (ImageView) view.findViewById(R.id.iv_preview);
        ivPreView.setImageResource(imgResId);
        return view;
    }
}
