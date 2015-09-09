package com.aura.smartschool.fragment.walkingfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.WalkingHistoryAdapter;
import com.aura.smartschool.database.DBStepCounter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.WalkingVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-07.
 */
public class WalkingHistoryFragment extends Fragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private RecyclerView mWalkingHistory;
    private WalkingHistoryAdapter mWalkingHistoryAdapter;

    private ArrayList<WalkingVO> walkingList;

    public static WalkingHistoryFragment newInstance(MemberVO member) {
        WalkingHistoryFragment instance = new WalkingHistoryFragment();
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
        walkingList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_walking_history, null);
        mWalkingHistory = (RecyclerView) view.findViewById(R.id.list_walking_history);
        mWalkingHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        mWalkingHistoryAdapter = new WalkingHistoryAdapter(walkingList);
        mWalkingHistory.setAdapter(mWalkingHistoryAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setHeaderView(R.drawable.actionbar_back, mMember.name);
        getWalkingHistory();
    }

    private void getWalkingHistory() {
        if (LoginManager.getInstance().getLoginUser().is_parent == 0) {
            walkingList = DBStepCounter.getInstance(getActivity()).getAllSteps();
            mWalkingHistoryAdapter.setWalkingHistory(walkingList);
            mWalkingHistoryAdapter.notifyDataSetChanged();
        } else {
            LoadingDialog.showLoading(getActivity());
            String url = Constant.HOST + Constant.API_GET_ACTIVITY_LIST;
            JSONObject json = new JSONObject();
            try {

                json.put("member_id", mMember.member_id);

                new AQuery(getActivity()).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        //update or insert
                        LoadingDialog.hideLoading();
                        try {
                            SchoolLog.d("LDK", "result:" + object.toString(1));
                            if (object.getInt("result") == 0) {
                                JSONArray array = object.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject json = array.getJSONObject(i);
                                    walkingList.add(new WalkingVO(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("activity_date")).getTime(),
                                            json.getInt("step"),
                                            json.getInt("calory"),
                                            json.getInt("distance"),
                                            json.getInt("time")));
                                }
                                mWalkingHistoryAdapter.setWalkingHistory(walkingList);
                                mWalkingHistoryAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
