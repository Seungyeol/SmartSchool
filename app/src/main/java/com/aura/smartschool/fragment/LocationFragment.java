package com.aura.smartschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.LocationVO;
import com.aura.smartschool.vo.MemberVO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends BaseFragment {
    private View mView;

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    private AQuery mAq;
    private MemberVO mMember;
    private ArrayList<LocationVO> mLocationList = new ArrayList<LocationVO>();

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance(MemberVO member) {

        LocationFragment instance = new LocationFragment();

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
        mView = View.inflate(getActivity(), R.layout.fragment_location, null);
        mAq = new AQuery(mView);

        setUpMapIfNeeded();

        if (mMember.lat != 0) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mMember.lat, mMember.lng), 16));
        }

        getLocationList();

        return mView;
    }

    private void setUpMapIfNeeded() {
        // check if we have got the googleMap already
        if (mGoogleMap == null) {
            mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            mGoogleMap = mMapFragment.getMap();
        }
    }

    @Override
    public void onDestroyView() {
        if(mMapFragment != null) {
            getFragmentManager().beginTransaction().remove(mMapFragment).commit();
        }
        super.onDestroy();
    }

    //비동기로 위치정보 리스트를 가져온다.
    private void getLocationList() {
        mLocationList.clear();
        try {
            String url = Constant.HOST + Constant.API_GET_LOCATIONLIST;

            JSONObject json = new JSONObject();
            json.put("member_id", mMember.member_id);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        if (status.getCode() != 200) {
                            return;
                        }
                        Log.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject json = array.getJSONObject(i);
                                double lat = 0;
                                double lng = 0;
                                try {
                                    lat = Double.parseDouble(json.getString("lat"));
                                    lng = Double.parseDouble(json.getString("lng"));
                                } catch (NumberFormatException e) {

                                }
                                String created_date = json.getString("created_date");
                                LocationVO location = new LocationVO();
                                location.lat = lat;
                                location.lng = lng;
                                location.created_date = created_date;
                                mLocationList.add(location);
                            }

                            //날짜 순서대로 소팅
                            Collections.sort(mLocationList, new Comparator<LocationVO>() {
                                @Override
                                public int compare(LocationVO lhs, LocationVO rhs) {
                                    return lhs.created_date.compareTo(rhs.created_date);
                                }
                            });

                            drawPath();
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

    private void drawPath() {
        //출발지부터 현재지점부터 라인 그리기
        for(int i = 0; i < mLocationList.size() - 1; ++i) {
            if(i==0) {
                Marker startMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLocationList.get(i).lat, mLocationList.get(i).lng))
                        .title("start")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                        .snippet(Util.getAddress(getActivity(), mLocationList.get(i).lat, mLocationList.get(i).lng)));
                startMarker.showInfoWindow();
            }

            mGoogleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(mLocationList.get(i).lat, mLocationList.get(i).lng),
                            new LatLng(mLocationList.get(i + 1).lat, mLocationList.get(i + 1).lng))
                    .width(15).color(Color.RED).geodesic(true));

            if(i==(mLocationList.size()-2)) {
                Marker endMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLocationList.get(i+1).lat, mLocationList.get(i+1).lng))
                        .title("now")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                        .snippet(Util.getAddress(getActivity(), mLocationList.get(i+1).lat, mLocationList.get(i+1).lng)));
                endMarker.showInfoWindow();
            }
        }
    }
}
