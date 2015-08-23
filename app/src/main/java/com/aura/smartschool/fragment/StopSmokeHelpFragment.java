package com.aura.smartschool.fragment;


import android.content.Intent;
import android.net.Uri;
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
        View mView = View.inflate(getActivity(), R.layout.fragment_stop_smoke_help, null);

        mView.findViewById(R.id.stopsmoke1).setOnClickListener(mClick);
        mView.findViewById(R.id.stopsmoke2).setOnClickListener(mClick);
        mView.findViewById(R.id.stopsmoke3).setOnClickListener(mClick);

        return mView;
    }

    View.OnClickListener mClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            switch (v.getId()) {
                case R.id.stopsmoke1:
                    intent.setData(Uri.parse("http://www.nosmoke.or.kr"));
                    break;
                case R.id.stopsmoke2:
                    intent.setData(Uri.parse("http://www.kash.or.kr"));
                    break;
                case R.id.stopsmoke3:
                    intent.setData(Uri.parse("http://www.ynsa.or.kr"));
                    break;
            }
            startActivity(intent);
        }
    };


}
