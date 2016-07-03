package com.aura.smartschool.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    private static final String RANKING_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&member_id=%d&u=/rankingHeight";
    private static final String MAGAZINE_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&member_id=%d&u=/magazine";
    private static final String CHALLENGE_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&member_id=%d&u=/challenge";
    private static final String HEIGHT_HISTORY_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&member_id=%d&u=/heightHistory";
    private static final String WEIGHT_HISTORY_URL = "https://aurasystem.kr:9000/hybrid/index.html#!/webviewLogin?home_id=%s&mdn=%s&member_id=%d&u=/weightHistory";

    private int mUrlType;
    private MemberVO mMember;

    private ProgressBar mProgress;

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
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);

        String url = "";
        switch(mUrlType) {
            case 1: //랭킹화면
                url =  RANKING_URL;
                break;
            case 2: //매거진
                url = MAGAZINE_URL;
                break;
            case 3: //도전건강
                url = CHALLENGE_URL;
                break;
            case 4: //키상세
                url = HEIGHT_HISTORY_URL;
                break;
            case 5: //몸무게 상세
                url = WEIGHT_HISTORY_URL;
                break;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(String.format(url, mMember.home_id, Util.getMdn(getActivity()), mMember.member_id));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        return view;
    }


}
