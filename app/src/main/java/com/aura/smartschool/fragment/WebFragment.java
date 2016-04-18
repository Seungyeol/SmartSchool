package com.aura.smartschool.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.vo.MemberVO;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    private static final String RANKING_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&u=/rankingHeight";
    private static final String MAGAZINE_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&u=/challenge";

    private int mUrlType;
    private MemberVO mMember;

    public WebFragment() {
        // Required empty public constructor
    }

    public static WebFragment newInstance(int urlType, MemberVO member) {

        WebFragment instance = new WebFragment();

        Bundle args = new Bundle();
        args.putSerializable("member", member);
        args.putInt("urlType", urlType);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable("member");
        mUrlType = args.getInt("urlType");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webView);

        String url = "";
        switch(mUrlType) {
            case 1: //랭킹화면
                url =  RANKING_URL;
                break;
            case 2: //도전건강매거진
                url = MAGAZINE_URL;
                break;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(String.format(url, mMember.home_id, mMember.mdn));

        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }


}
