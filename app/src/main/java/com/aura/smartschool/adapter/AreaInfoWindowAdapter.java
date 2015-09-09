package com.aura.smartschool.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.AreaVO;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by eastflag on 2015-07-24.
 */
public class AreaInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;
    private View mView;
    private HashMap<String, AreaVO> mAreaMap;
    private AQuery mAq;
    TextView tv_title;
    ImageView iv_picture;
    TextView tv_content;
    String imageString;

    public AreaInfoWindowAdapter(Context context, HashMap<String, AreaVO> map) {
        mContext = context;
        mView = View.inflate(mContext, R.layout.area_infowindow_row, null);
        tv_title = (TextView) mView.findViewById(R.id.tv_title);
        iv_picture = (ImageView) mView.findViewById(R.id.iv_picture);
        tv_content = (TextView) mView.findViewById(R.id.tv_content);

        mAreaMap = map;
        mAq = new AQuery(mView);
    }

    @Override
    public View getInfoWindow(final Marker marker) {
       return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        tv_title.setText(marker.getTitle());
        tv_content.setText(marker.getSnippet());

        //if ( mAreaMap.get(marker.getId()) != null) {
        if (imageString == null && mAreaMap.get(marker.getId()) != null ) {
            String url = Constant.HOST + Constant.API_GET_AREA;
            try {
                JSONObject json = new JSONObject();
                json.put("id", mAreaMap.get(marker.getId()).id);

                SchoolLog.d("LDK", "url:" + url);
                SchoolLog.d("LDK", "input:" + json.toString(1));

                mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        try {
                            if (status.getCode() != 200) {
                                return;
                            }
                            SchoolLog.d("LDK", "result:" + object.toString(1));

                            if (object.getInt("result") == 0) {
                                imageString = object.getJSONObject("data").getString("picture");
                                iv_picture.setImageBitmap(Util.StringToBitmap(imageString));
                                marker.showInfoWindow();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            imageString = null;

            //현재위치일 경우
            if(mAreaMap.get(marker.getId()) == null) {
                iv_picture.setImageDrawable(null);
            }
        }
//        } else {
//            iv_picture.setVisibility(View.GONE);
//        }

        return mView;
    }
}
