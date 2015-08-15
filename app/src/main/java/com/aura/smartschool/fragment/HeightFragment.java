package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.vo.MeasureVO;
import com.aura.smartschool.vo.MemberVO;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HeightFragment extends Fragment {

    private View mView;

    private AQuery mAq;
    private MemberVO mMember;
    private int mType; //1:height, 2:weight
    private MeasureVO mMeasureVO;

    private BarChart mChart;

    private TextView tv_title;
    private TextView tv_grade, tv_grade_desc;
    private TextView tv_lastmonth;
    private TextView tv_babel;

    private PopupWindow mPopup;

    public HeightFragment() {
        // Required empty public constructor
    }

    public static HeightFragment newInstance(MemberVO member, int type) {

        HeightFragment instance = new HeightFragment();

        Bundle args = new Bundle();
        args.putSerializable("member", member);
        args.putInt("type", type);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable("member");
        mType = args.getInt("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_height, null);
        mAq = new AQuery(mView);

        tv_title = (TextView) mView.findViewById(R.id.tv_title);
        tv_grade = (TextView) mView.findViewById(R.id.tv_grade);
        tv_grade_desc = (TextView) mView.findViewById(R.id.tv_grade_desc);
        tv_lastmonth = (TextView) mView.findViewById(R.id.tv_lastmonth);
        tv_babel = (TextView) mView.findViewById(R.id.tv_babel);

        tv_lastmonth.setOnClickListener(mClick);
        tv_babel.setOnClickListener(mClick);

        if(mType == 1) {
            tv_title.setText("신장");
        } else {
            tv_title.setText("체중");
        }

        mChart = (BarChart) mView.findViewById(R.id.heightChart);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("");
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.getLegend().setEnabled(false); //legend 안그림

        //가로축 라벨을 차트 아래에 위치
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(3);

        //y축 왼쪽
        //ValueFormatter custom = new MyValueFormatter();
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setStartAtZero(false); //y값이 0부터 시작하지 않음

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawLabels(false);

        getData();

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
        }
    }

    private void getData() {
        LoadingDialog.showLoading(getActivity());
        try {
            String url = Constant.HOST;
            if(mType == 1) {
                url += Constant.API_GET_HEIGHT;
            } else {
                url += Constant.API_GET_WEIGHT;
            }

            JSONObject json = new JSONObject();
            json.put("member_id", mMember.member_id);

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

                        if (object.getInt("result") == 0) {
                            JSONObject json = object.getJSONObject("data");

                            mMeasureVO = new MeasureVO();
                            mMeasureVO.value = Float.parseFloat(json.getString("value"));
                            mMeasureVO.beforeValue =  Float.parseFloat(json.getString("beforeValue"));
                            mMeasureVO.gradeId = json.getString("gradeId");
                            mMeasureVO.gradeString = json.getString("gradeString");
                            mMeasureVO.schoolGrade = json.getString("schoolGrade");
                            mMeasureVO.beforeSchoolGrade = json.getString("beforeSchoolGrade");
                            mMeasureVO.totalNumberOfStudent = json.getString("totalNumberOfStudent");
                            mMeasureVO.averageOfSchool =  Float.parseFloat(json.getString("averageOfSchool"));
                            mMeasureVO.averageOfLocal =  Float.parseFloat(json.getString("averageOfLocal"));
                            mMeasureVO.averageOfNation = Float.parseFloat(json.getString("averageOfNation"));
                            mMeasureVO.averageOfStandard =  Float.parseFloat(json.getString("averageOfStandard"));
                            mMeasureVO.rank = json.getString("rank");
                            mMeasureVO.beforeRank = json.getString("beforeRank");

                            drawGraph();

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

    private void drawGraph() {
        tv_grade.setText(String.format("%s/100", mMeasureVO.rank));
        tv_grade_desc.setText(mMeasureVO.gradeString);
        //Model Data--------------------------------------------------------
        //색깔
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        //x축
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("나");
        xVals.add("표준");
        xVals.add("학교평균");
        xVals.add("지역평균");
        xVals.add("전국평균");

        //y축
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        yVals.add(new BarEntry(mMeasureVO.value, 0));
        yVals.add(new BarEntry(mMeasureVO.averageOfStandard, 1));
        yVals.add(new BarEntry(mMeasureVO.averageOfSchool, 2));
        yVals.add(new BarEntry(mMeasureVO.averageOfLocal, 3));
        yVals.add(new BarEntry(mMeasureVO.averageOfNation, 4));

        BarDataSet set1 = new BarDataSet(yVals, "cm");
        set1.setBarSpacePercent(35f);
        set1.setColors(colors);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        mChart.animateY(800, Easing.EasingOption.EaseInOutQuad);

        mChart.setData(data);
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.tv_lastmonth:
                    if(mPopup != null && mPopup.isShowing()) {
                        mPopup.dismiss();
                        return;
                    }
                    DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
                    int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, dm);
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, dm);
                    View view = View.inflate(getActivity(), R.layout.popup_height, null);

                    ImageView iv_left = (ImageView) view.findViewById(R.id.iv_left);
                    TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                    TextView tv_increase = (TextView) view.findViewById(R.id.tv_increase);
                    TextView tv_rank = (TextView) view.findViewById(R.id.tv_rank);
                    TextView tv_rank_before = (TextView) view.findViewById(R.id.tv_rank_before);

                    if(mType == 1) {
                        tv_title.setText("신장 변화");
                        tv_increase.setText(String.format("%.1fcm 증가", mMeasureVO.value - mMeasureVO.beforeValue));

                    } else {
                        tv_title.setText("체중 변화");
                        tv_increase.setText(String.format("%.1fkg 증가", mMeasureVO.value - mMeasureVO.beforeValue));
                        iv_left.setBackgroundResource(R.drawable.scale);
                    }

                    tv_rank.setText(String.valueOf(mMeasureVO.rank) + "등");
                    tv_rank_before.setText(String.format("이전 %s등", mMeasureVO.beforeRank));

                    mPopup = new PopupWindow(view, width, height);
                    mPopup.setAnimationStyle(-1);
                    mPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    //mPopup.showAsDropDown(tv_lastmonth);
                    break;

                case R.id.tv_babel:
                    if(mType == 1) {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, VideoFragment.newInstance(mMember, 2)).addToBackStack(null).commit();
                    } else {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, VideoFragment.newInstance(mMember, 3)).addToBackStack(null).commit();
                    }
                    break;
            }
        }
    };
}