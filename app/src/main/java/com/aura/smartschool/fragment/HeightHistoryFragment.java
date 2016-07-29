package com.aura.smartschool.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.MemberVO;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeightHistoryFragment extends Fragment {
    private View mView;
    private AQuery mAq;

    private MemberVO mMember;
    private int mType = 1;

    private TextView tv_title;
    private TextView myHeight;
    private TextView tvNationAvg;
    private TextView tvLocalAvg;
    private Spinner spinner_year;

    private BarChart mChart;

    public HeightHistoryFragment() {
        // Required empty public constructor
    }

    public static HeightHistoryFragment newInstance(MemberVO member, int type) {

        HeightHistoryFragment instance = new HeightHistoryFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_height_history, null);
        mAq = new AQuery(mView);

        tv_title = (TextView) mView.findViewById(R.id.tv_title);
        myHeight = (TextView) mView.findViewById(R.id.tvMyHeight);
        tvNationAvg = (TextView) mView.findViewById(R.id.tvNationAvg);
        tvLocalAvg = (TextView) mView.findViewById(R.id.tvLocalAvg);
        spinner_year = (Spinner) mView.findViewById(R.id.spinner_year);
        ArrayList<String> list = new ArrayList<String>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int year = currentYear ; year >= 2015; --year) {
            list.add(year + "년");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
        spinner_year.setAdapter(adapter);;
        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(mType == 1) {
            tv_title.setText("키 상세이력");
        } else {
            tv_title.setText("몸무게 상세이력");
       }

        mChart = (BarChart) mView.findViewById(R.id.heightChart);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("데이터 없음");
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

    private void getData() {

        try {
            String url = null;
            if(mType == 1) {
                url = Constant.HOST + "/api/getHeightHistoryList";
            } else {
                url = Constant.HOST + "/api/getWeightHistoryList";
            }
            JSONObject object = new JSONObject();
            object.put("member_id", mMember.member_id);
            object.put("search_year", spinner_year.getSelectedItem().toString().replace("년", "")); //현재년도 가져오기
            mAq.post(url, object, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    try {
                        if(json.isNull("data")) {
                            mChart.clear();
                            myHeight.setText("");
                            tvNationAvg.setText("");
                            tvLocalAvg.setText("");
                            return;
                        }
                        SchoolLog.d("LDK", json.toString());
                        String unit = null;
                        if(mType == 1) {
                            unit = "cm";
                        } else {
                            unit = "kg";
                        }
                        String growth = json.getJSONObject("data").getString("growth");
                        myHeight.setText(growth + unit);
                        String avgOfNation = json.getJSONObject("data").getString("avgOfNation");
                        tvNationAvg.setText(avgOfNation + unit);
                        String avgOfLocal = json.getJSONObject("data").getString("avgOfLocal");
                        tvLocalAvg.setText(avgOfLocal + unit);

                        //차트 그리기-------------------------------------------------
                        //x축 키
                        ArrayList<String> xVals = new ArrayList<String>();
                        //x축 값
                        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

                        JSONArray array = json.getJSONObject("data").getJSONArray("list");
                        for(int i=0; i<array.length(); ++i) {
                            JSONObject data = array.getJSONObject(i);
                            xVals.add(data.getString("month")+"월");
                            if(data.has("value")) {
                                yVals.add(new BarEntry(Float.parseFloat(data.getString("value")), i));
                            }
                        }

                        BarDataSet set1 = new BarDataSet(yVals, unit);
                        set1.setBarSpacePercent(35f);

                        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
                        dataSets.add(set1);

                        BarData data = new BarData(xVals, dataSets);
                        data.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return new DecimalFormat("0.#").format(value);
                            }
                        });
                        data.setValueTextSize(10f);

                        //기준선 그리기: 평균
                        float avgOfStandard = Float.parseFloat(json.getJSONObject("data").getString("avgOfStandard"));
                        LimitLine ll1 = new LimitLine(avgOfStandard, "Upper Limit");
                        ll1.setLineWidth(4f);
                        ll1.enableDashedLine(10f, 10f, 0f);
                        ll1.setLabelPosition(LimitLine.LimitLabelPosition.POS_LEFT);
                        ll1.setTextSize(10f);
                        YAxis leftAxis = mChart.getAxisLeft();
                        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                        leftAxis.addLimitLine(ll1);

                        mChart.animateY(800, Easing.EasingOption.EaseInOutQuad);

                        mChart.setData(data);

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
