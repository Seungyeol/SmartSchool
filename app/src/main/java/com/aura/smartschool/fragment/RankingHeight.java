package com.aura.smartschool.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.MemberVO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingHeight extends Fragment {

    private MemberVO mMember;
    private int mType = 1;

    private View mView;
    private AQuery mAq;

    private Button HeightTab;
    private Button WeightTab;
    private Button BMITab;
    private Button MuscleTab;
    private Button FatTab;

    private TextView ranking_title;
    private TextView updateDay;
    private TextView schoolRanking;
    private TextView localRanking;
    private TextView nationRanking;

    private TextView changedSchoolRanking;
    private TextView changedLocalRanking;
    private TextView changedNationRanking;

    private RelativeLayout school_ranking_detail;
    private RelativeLayout local_ranking_detail;
    private RelativeLayout nation_ranking_detail;

    //랭킹 두번째 페이지
    private TextView p2SchoolRanking;
    private TextView p2SchoolRankingChanged;
    private TextView p2SchoolGrade;
    private TextView p2SchoolHeight;
    private TextView p2SchoolHeightChanged;

    private TextView p2LocalName;
    private TextView p2LocalRanking;
    private TextView p2LocalRankingChanged;

    private TextView p2NationRanking;
    private TextView p2NationRankingChanged;

    private Button p2SchoolRankingDetail;
    private Button p2LocalRankingDetail;
    private Button p2NationRankingDetail;

    public RankingHeight() {
        // Required empty public constructor
    }
    public static RankingHeight newInstance(MemberVO member, int type) {
        RankingHeight instance = new RankingHeight();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        args.putInt("type", type);
        instance.setArguments(args);
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable("member");
        mType = args.getInt("type");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = View.inflate(getActivity(), R.layout.fragment_ranking_height, null);
        mAq = new AQuery(mView);

        HeightTab = (Button)mView.findViewById(R.id.heightTab);
        WeightTab = (Button)mView.findViewById(R.id.weightTab);
        BMITab = (Button)mView.findViewById(R.id.bmiTab);
        MuscleTab = (Button)mView.findViewById(R.id.muscleTab);
        FatTab = (Button)mView.findViewById(R.id.fatTab);

        //랭킹 첫 페이지
        ranking_title = (TextView) mView.findViewById(R.id.RankingTitle);
        updateDay = (TextView) mView.findViewById(R.id.updateDay);
        schoolRanking = (TextView) mView.findViewById(R.id.school_ranking);
        localRanking = (TextView) mView.findViewById(R.id.local_ranking);
        nationRanking = (TextView) mView.findViewById(R.id.nation_ranking);

        changedSchoolRanking = (TextView) mView.findViewById(R.id.changedSchoolRanking);
        changedLocalRanking = (TextView) mView.findViewById(R.id.changedLocalRanking);
        changedNationRanking = (TextView) mView.findViewById(R.id.changedNationRanking);


        school_ranking_detail = (RelativeLayout)mView.findViewById(R.id.school_ranking_detail);
        local_ranking_detail = (RelativeLayout)mView.findViewById(R.id.local_ranking_detail);
        nation_ranking_detail = (RelativeLayout)mView.findViewById(R.id.nation_ranking_detail);

        HeightTab.setOnClickListener(mClick);
        WeightTab.setOnClickListener(mClick);
        BMITab.setOnClickListener(mClick);
        MuscleTab.setOnClickListener(mClick);
        FatTab.setOnClickListener(mClick);

        school_ranking_detail.setOnClickListener(mClick);
        local_ranking_detail.setOnClickListener(mClick);
        nation_ranking_detail.setOnClickListener(mClick);


        p2SchoolRanking=(TextView)mView.findViewById(R.id.p2_school_ranking);
        p2SchoolRankingChanged=(TextView)mView.findViewById(R.id.p2_local_ranking_changed);
        p2SchoolGrade=(TextView)mView.findViewById(R.id.p2_school_grade);
        p2SchoolHeight=(TextView)mView.findViewById(R.id.p2_school_height);
        p2SchoolHeightChanged=(TextView)mView.findViewById(R.id.p2_school_height_changed);
        p2LocalName=(TextView)mView.findViewById(R.id.p2_local_name);
        p2LocalRanking=(TextView)mView.findViewById(R.id.p2_local_ranking);
        p2LocalRankingChanged=(TextView)mView.findViewById(R.id.p2_local_ranking_changed);

        p2NationRanking=(TextView)mView.findViewById(R.id.p2_nation_ranking);
        p2NationRankingChanged=(TextView)mView.findViewById(R.id.p2_local_ranking_changed);

        p2SchoolRankingDetail=(Button) mView.findViewById(R.id.p2_btn_school_detail);
        p2LocalRankingDetail=(Button)mView.findViewById(R.id.p2_btn_local_detail);
        p2NationRankingDetail=(Button)mView.findViewById(R.id.p2_btn_nation_detail);




        String url = null;
        SchoolLog.d("LDK", "getHeight......1");


            if(mType == 1) {
                url = Constant.HOST + "/api/getRankingHeight";
                ranking_title.setText("신장");
                HeightTab.setSelected(true);
                WeightTab.setSelected(false);
                BMITab.setSelected(false);
                MuscleTab.setSelected(false);
                FatTab.setSelected(false);
            } else if(mType==2) {
                url = Constant.HOST + "/api/getRankingWeight";
                ranking_title.setText("체중");
                HeightTab.setSelected(false);
                WeightTab.setSelected(true);
                BMITab.setSelected(false);
                MuscleTab.setSelected(false);
                FatTab.setSelected(false);
            }else if(mType==3) {
                url = Constant.HOST + "/api/getRankingBmi";
                ranking_title.setText("BMI");
                HeightTab.setSelected(false);
                WeightTab.setSelected(false);
                BMITab.setSelected(true);
                MuscleTab.setSelected(false);
                FatTab.setSelected(false);
            }else if(mType==4) {
                url = Constant.HOST + "/api/getRankingMuscle";
                ranking_title.setText("근육량");
                HeightTab.setSelected(false);
                WeightTab.setSelected(false);
                BMITab.setSelected(false);
                MuscleTab.setSelected(true);
                FatTab.setSelected(false);
            }else{
                url = Constant.HOST + "/api/getRankingFat";
                ranking_title.setText("체지방량");
                HeightTab.setSelected(false);
                WeightTab.setSelected(false);
                BMITab.setSelected(false);
                MuscleTab.setSelected(false);
                FatTab.setSelected(true);
            }

        getDataPage1(url);
        getDataPage2(url);

       return mView;
    }


    private void getDataPage2(String url) {
    }

    private void getDataPage1(String url){
        try {
            JSONObject object = new JSONObject();
            object.put("member_id", mMember.member_id);
            SchoolLog.d("LDK", "getHeight......2");
            mAq.post(url, object, JSONObject.class, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    try {

                        String triangle="▲";
                        String updateDate = json.getJSONObject("data").getString("measureDate");
                        updateDay.setText("정보 Update "+updateDate);

                        //학교 순위
                        int rankOfSchool = json.getJSONObject("data").getInt("rankOfSchool");
                        schoolRanking.setText(rankOfSchool+"위");
                        int beforeRankOfSchool = json.getJSONObject("data").getInt("beforeRankOfSchool");
                        int changedschool = beforeRankOfSchool-rankOfSchool;

                        if(changedschool<0){
                            triangle="▼";
                            changedschool=  - changedschool;
                            schoolRanking.setTextColor(Color.BLUE);
                            changedSchoolRanking.setTextColor(Color.BLUE);
                        }
                        changedSchoolRanking.setText(triangle+changedschool+"위");

                        //지역순위
                        double rankOfLocal = json.getJSONObject("data").getDouble("rankOfLocal");
                        double totalOfLocal = json.getJSONObject("data").getDouble("totalOfLocal");
                        double beforeRankOfLocal = json.getJSONObject("data").getDouble("beforeRankOfLocal");
                        double beforeTotalOfLocal = json.getJSONObject("data").getDouble("beforeTotalOfLocal");

                        rankOfLocal = rankOfLocal/totalOfLocal*100;
                        rankOfLocal = Double.parseDouble(String.format("%.2f", rankOfLocal));
                        localRanking.setText(rankOfLocal+"%");

                        beforeRankOfLocal = beforeRankOfLocal/beforeTotalOfLocal*100;
                        double changedLocal = beforeRankOfLocal - rankOfLocal;
                        changedLocal = Double.parseDouble(String.format("%.2f", changedLocal));
                        if(changedLocal<0){
                            triangle="▼";
                            changedLocal = - changedLocal;
                            localRanking.setTextColor(Color.BLUE);
                            changedLocalRanking.setTextColor(Color.BLUE);
                        }
                        changedLocalRanking.setText(triangle+changedLocal+"%");

                        //전국순위
                        double rankOfNation = json.getJSONObject("data").getDouble("rankOfNation");
                        double totalOfNation = json.getJSONObject("data").getDouble("totalOfNation");
                        double beforeRankOfNation = json.getJSONObject("data").getDouble("beforeRankOfNation");
                        double beforeTotalOfNation = json.getJSONObject("data").getDouble("beforeTotalOfNation");

                        rankOfNation = rankOfNation/totalOfNation*100;
                        rankOfNation = Double.parseDouble(String.format("%.2f", rankOfNation));
                        nationRanking.setText(rankOfNation+"%");

                        beforeRankOfNation = beforeRankOfNation/beforeTotalOfNation*100;
                        double changedNation = beforeRankOfNation - rankOfNation;
                        changedNation = Double.parseDouble(String.format("%.2f", changedNation));
                        if(changedNation<0){
                            triangle="▼";
                            changedNation = - changedNation;
                            nationRanking.setTextColor(Color.BLUE);
                            changedNationRanking.setTextColor(Color.BLUE);
                        }
                        changedNationRanking.setText(triangle+changedNation+"%");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    View.OnClickListener mClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.heightTab:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, RankingHeight.newInstance(mMember,1)).addToBackStack(null).commit();
                    break;
                case R.id.weightTab:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, RankingHeight.newInstance(mMember,2)).addToBackStack(null).commit();
                    break;
                case R.id.bmiTab:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, RankingHeight.newInstance(mMember,3)).addToBackStack(null).commit();
                    break;
                case R.id.muscleTab:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, RankingHeight.newInstance(mMember,4)).addToBackStack(null).commit();
                    break;
                case R.id.fatTab:
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, RankingHeight.newInstance(mMember,5)).addToBackStack(null).commit();
                    break;
                case R.id.school_ranking_detail:
                case R.id.local_ranking_detail:
                case R.id.nation_ranking_detail:
                    //getFragmentManager().beginTransaction().replace(R.id.content_frame, RankingHeightDetail.newInstance(mMember,mType)).addToBackStack(null).commit();
                    break;


            }
        }
    };



}


