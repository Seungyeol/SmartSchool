package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.VideoListAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.VideoVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoFragment extends BaseFragment {

    private View mView;
    private AQuery mAq;

    private MemberVO mMember;
    private ArrayList<VideoVO> mVideoList = new ArrayList<VideoVO>();
    private ListView mListview;
    private VideoListAdapter mAdapter;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(MemberVO member) {

        VideoFragment instance = new VideoFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_growth, null);
        mAq = new AQuery(getActivity(), mView);

        mListview = (ListView) mView.findViewById(R.id.listview);
        mAdapter = new VideoListAdapter(getActivity(), mVideoList);
        mListview.setAdapter(mAdapter);

        getVideoList();

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void getVideoList() {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.API_GET_VIDEOLIST;

            JSONObject json = new JSONObject();
            json.put("masterGradeId", "1");
            json.put("schoolGradeId", "1");

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            return;
                        }
                        Log.d("LDK", "result:" + object.toString(1));

                        if ("0".equals(object.getString("result"))) {
                            JSONArray array = object.getJSONArray("value");
                            for(int i=0; i < array.length(); ++i) {
                                VideoVO video = new VideoVO();
                                video.image_url = array.getJSONObject(i).getString("thumbnailUrl");
                                video.video_url = array.getJSONObject(i).getString("videoUrl");
                                video.title = array.getJSONObject(i).getString("title");
                                video.duration = array.getJSONObject(i).getString("duration");
                                mVideoList.add(video);
                            }
                            mAdapter.setData(mVideoList);
                            mAdapter.notifyDataSetChanged();
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {

                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {

            }
        }
    };
}