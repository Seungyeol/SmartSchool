package com.aura.smartschool.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StopSmokeHelpFragment extends Fragment {


    public StopSmokeHelpFragment() {
        // Required empty public constructor
    }

    public static StopSmokeHelpFragment newInstance() {
        StopSmokeHelpFragment instance = new StopSmokeHelpFragment();

        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stop_smoke_help, container, false);
    }


}
