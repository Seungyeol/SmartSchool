package com.aura.smartschool.fragment.schoolNoticeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.SchoolNoticeAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolNotiVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-16.
 */
public class SchoolLetterListFragment extends Fragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private RecyclerView mSchoolLetterListView;
    private View ivShowScrapOnly;

    private SchoolNoticeAdapter mLetterAdapter;
    private ArrayList<SchoolNotiVO> mLetterList = new ArrayList<>();

    public static SchoolLetterListFragment newInstance(MemberVO member) {
        SchoolLetterListFragment instance = new SchoolLetterListFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_school_notice, null);

        mSchoolLetterListView = (RecyclerView) view.findViewById(R.id.list_school_notice);
        mSchoolLetterListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ivShowScrapOnly = view.findViewById(R.id.iv_show_scrap_only);
        ivShowScrapOnly.setOnClickListener(scrapClicked);

        mLetterAdapter = new SchoolNoticeAdapter(mMember, mLetterList);
        mLetterAdapter.setScrapChangedListener(scrapChangedListener);
        mSchoolLetterListView.setAdapter(mLetterAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setHeaderView(R.drawable.actionbar_back, mMember.name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setLetterList(ArrayList<SchoolNotiVO> letterList) {
        this.mLetterList = letterList;
        if (mLetterAdapter != null) {
            mLetterAdapter.setNotiList(mLetterList);
            mLetterAdapter.notifyDataSetChanged();
        }
    }

    private View.OnClickListener scrapClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ivShowScrapOnly.setSelected(!ivShowScrapOnly.isSelected());
            mLetterAdapter.showScrapItemOnly(ivShowScrapOnly.isSelected());
        }
    };

    private SchoolNoticeAdapter.OnScrapChangedListener scrapChangedListener = new SchoolNoticeAdapter.OnScrapChangedListener() {
        @Override
        public void onScrapChanged(int notiSeq, boolean isSelected) {
            doScrapChaged(notiSeq, isSelected);
        }
    };

    private void doScrapChaged(final int notiSeq, final boolean isSelected) {
        LoadingDialog.showLoading(getActivity());
        String url;
        if (isSelected) {
            url = Constant.HOST + Constant.API_ADD_NOTI_BOOKMARK;
        } else {
            url = Constant.HOST + Constant.API_REMOVE_NOTI_BOOKMARK;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("member_id", LoginManager.getInstance().getLoginUser().member_id);
            jsonObject.put("noti_seq", notiSeq);
            new AQuery(getActivity()).post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            Log.d("test", "실패 ");
                            showScrapToast(isSelected, false);
                            return;
                        }

                        Log.d("LDK", "result:" + object.toString(1));

                        if ("0".equals(object.getString("result"))) {
                            showScrapToast(isSelected, false);
                            mLetterAdapter.setScrap(notiSeq, isSelected);
                        } else {
                            showScrapToast(isSelected, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showScrapToast(isSelected, false);
                    }
                }
            });
        } catch (JSONException e) {

        }
    }

    private void showScrapToast(boolean isScraped, boolean isSuccess) {
        if (isSuccess) {
            if (isScraped) {
                Toast.makeText(getActivity(), "스크랩 실패 하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "스크랩 해제 실패 하였습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isScraped) {
                Toast.makeText(getActivity(), "스크랩 하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "스크랩 해제 하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
