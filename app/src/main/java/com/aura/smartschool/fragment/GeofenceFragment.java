package com.aura.smartschool.fragment;


import android.os.Bundle;
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
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.GeofenceAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.GeofenceVO;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeofenceFragment extends Fragment {

    private AQuery mAq;
    private MemberVO mMember;
    private View mView;
    private GeofenceAdapter mAdapter;
    private List<GeofenceVO> mGeofenceList = new ArrayList<GeofenceVO>();

    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    public static GeofenceFragment newInstance(MemberVO member) {
        GeofenceFragment instance = new GeofenceFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable("member");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_geofence, container, false);
        ButterKnife.bind(this, mView);
        mAq = new AQuery(getActivity(), mView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new GeofenceAdapter(mGeofenceList);
        mRecyclerView.setAdapter(mAdapter);

        getList();

        return mView;
    }

    private void getList() {
        LoadingDialog.showLoading(getActivity());
        mGeofenceList.clear();
        try {
            String url = Constant.HOST + Constant.API_GET_GEOFENCELIST;

            JSONObject json = new JSONObject();
            json.put("member_id", mMember.member_id);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            return;
                        }
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject json = array.getJSONObject(i);

                                GeofenceVO geofenceVO = new GeofenceVO();
                                geofenceVO.created = json.getString("created");
                                geofenceVO.sex = json.getString("sex");
                                geofenceVO.school_name = json.getString("school_name");
                                geofenceVO.name = json.getString("name");
                                geofenceVO.type = json.getInt("type");

                                mGeofenceList.add(geofenceVO);
                            }

                            mAdapter.setData(mGeofenceList);
                            mAdapter.notifyDataSetChanged();
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
