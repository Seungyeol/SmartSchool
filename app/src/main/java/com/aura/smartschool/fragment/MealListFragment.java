package com.aura.smartschool.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.MealMenuListAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolApi;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.MenuData;

import java.util.Calendar;

/**
 * Created by Administrator on 2015-07-18.
 */
public class MealListFragment extends Fragment {
    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private Calendar thisMonth;

    private TextView tvMonth;
    private View llMeal;
    private ListView mMenuListView;
    private TextView tvEmpty;

    private MealMenuListAdapter mealAdapter;


    public static MealListFragment newInstance(MemberVO member) {
        MealListFragment instance = new MealListFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
        this.thisMonth = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_meanl_list, null);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setHeaderView(R.drawable.actionbar_back, mMember.name);
        loadMealList();
    }

    private void initViews(View view) {
        tvMonth = (TextView) view.findViewById(R.id.tv_month);
        tvMonth.setText(String.format(getActivity().getResources().getText(R.string.meal_title).toString(), (thisMonth.get(Calendar.MONTH) + 1)));

        llMeal = view.findViewById(R.id.ll_meal);
        tvEmpty = (TextView) view.findViewById(R.id.tv_empty_list);

        mMenuListView = (ListView) view.findViewById(R.id.list_meal);
        mealAdapter = new MealMenuListAdapter(getActivity(), thisMonth, new MenuData[0]);
        mMenuListView.setAdapter(mealAdapter);

    }

    private void loadMealList() {
        AsyncTask mealTask = new AsyncTask<Object, Void, MenuData[]>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingDialog.showLoading(getActivity());
            }

            @Override
            protected MenuData[] doInBackground(Object... params) {
                return SchoolApi.getMonthlyMenu(SchoolApi.getContry(mMember.mSchoolVO.sido),
                        mMember.mSchoolVO.code,
                        SchoolApi.getSchoolType(mMember.mSchoolVO.gubun2),
                        thisMonth.get(Calendar.YEAR),
                        thisMonth.get(Calendar.MONTH) + 1);
            }

            @Override
            protected void onPostExecute(MenuData[] menuList) {
                super.onPostExecute(menuList);
                LoadingDialog.hideLoading();
                if (menuList == null || menuList.length == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    llMeal.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    llMeal.setVisibility(View.VISIBLE);
                    mealAdapter.setMenuArray(menuList);
                    mealAdapter.notifyDataSetChanged();
                    mMenuListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mMenuListView.setSelection(thisMonth.get(Calendar.DATE)-1);
                        }
                    });
                }
            }
        };
        mealTask.execute();
    }
}
