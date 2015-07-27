package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;

public class GrowthGradeDescriptionFragment extends BaseFragment {

    private View mView;

    private MemberVO mMember;

    private TextView tvBmiStatus, tvObesityRisk, tvDietaryHabits, tvRecommededExercise;

    public GrowthGradeDescriptionFragment() {
        // Required empty public constructor
    }

    public static GrowthGradeDescriptionFragment newInstance(MemberVO member) {

        GrowthGradeDescriptionFragment instance = new GrowthGradeDescriptionFragment();

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
        mView = View.inflate(getActivity(), R.layout.fragment_growth_grade_detail, null);

        tvBmiStatus = (TextView) mView.findViewById(R.id.tv_bmiStatus);
        tvObesityRisk = (TextView) mView.findViewById(R.id.tv_obesity_risk);
        tvDietaryHabits = (TextView) mView.findViewById(R.id.tv_dietary_habits);
        tvRecommededExercise = (TextView) mView.findViewById(R.id.tv_recommend_exercise);

        tvBmiStatus.setText(mMember.mMeasureSummaryVO.bmiStatus);
        GROWTH_GRADE growthGrade = GROWTH_GRADE.findDescription(mMember.mMeasureSummaryVO.bmiStatus);
        if(growthGrade != null) {
            tvObesityRisk.setText(growthGrade.obesityRiskDesRId);
            tvDietaryHabits.setText(growthGrade.dietaryHabitsDesRId);
            tvRecommededExercise.setText(growthGrade.exerciseDesRId);
        }

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    enum GROWTH_GRADE {
        LOW_WEIGHT_A("저체중 A", R.string.obesity_risk_1, R.string.dietary_habits_1, R.string.recommend_exercise_1),
        LOW_WEIGHT_B("저체중 B", R.string.obesity_risk_1, R.string.dietary_habits_1, R.string.recommend_exercise_1),
        LOW_WEIGHT_C("저체중 C", R.string.obesity_risk_1, R.string.dietary_habits_1, R.string.recommend_exercise_1),
        NOMAL_WEIGHT_A("정상체중 A", R.string.obesity_risk_2, R.string.dietary_habits_2_3, R.string.recommend_exercise_2_3),
        NOMAL_WEIGHT_B("정상체중 B", R.string.obesity_risk_3, R.string.dietary_habits_2_3, R.string.recommend_exercise_2_3),
        OVER_WEIGHT_A("과체중 A", R.string.obesity_risk_4, R.string.dietary_habits_4, R.string.recommend_exercise_4),
        OVER_WEIGHT_B("과체중 B", R.string.obesity_risk_4, R.string.dietary_habits_4, R.string.recommend_exercise_4),
        MILD_OBESITY_A("비만 A", R.string.obesity_risk_5, R.string.dietary_habits_5, R.string.recommend_exercise_5),
        MILD_OBESITY_B("비만 B", R.string.obesity_risk_5, R.string.dietary_habits_5, R.string.recommend_exercise_5),
        MILD_OBESITY_C("비만 C", R.string.obesity_risk_5, R.string.dietary_habits_5, R.string.recommend_exercise_5),
        MEDIUM_OBESITY_A("중도비만 A", R.string.obesity_risk_6, R.string.dietary_habits_6_7, R.string.recommend_exercise_6),
        MEDIUM_OBESITY_B("중도비만 B", R.string.obesity_risk_6, R.string.dietary_habits_6_7, R.string.recommend_exercise_6),
        MEDIUM_OBESITY_C("중도비만 C", R.string.obesity_risk_6, R.string.dietary_habits_6_7, R.string.recommend_exercise_6),
        EXTREME_OBESITY_A("고도비만 A", R.string.obesity_risk_7, R.string.dietary_habits_6_7, R.string.recommend_exercise_7),
        EXTREME_OBESITY_B("고도비만 A", R.string.obesity_risk_7, R.string.dietary_habits_7, R.string.recommend_exercise_7),
        EXTREME_OBESITY_C("고도비만 A", R.string.obesity_risk_7, R.string.dietary_habits_7, R.string.recommend_exercise_7);

        String name;
        int obesityRiskDesRId;
        int dietaryHabitsDesRId;
        int exerciseDesRId;

        GROWTH_GRADE(String name, int obesityRiskDesRId, int dietaryHabitsDesRId, int exerciseDesRId) {
            this.name = name;
            this.obesityRiskDesRId = obesityRiskDesRId;
            this.dietaryHabitsDesRId = dietaryHabitsDesRId;
            this.exerciseDesRId = exerciseDesRId;
        }

        public static GROWTH_GRADE findDescription(String name) {
            for(GROWTH_GRADE growthGrade : values()) {
                if (growthGrade.name.equalsIgnoreCase(name)) {
                    return growthGrade;
                }
            }
            return null;
        }
    }
}