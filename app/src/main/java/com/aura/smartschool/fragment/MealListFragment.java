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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.MealMenuListAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolApi;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.MenuData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015-07-18.
 */
public class MealListFragment extends Fragment {
    private static String KEY_MEMBER = "member";

    private MemberVO mMember;

    private Calendar mealMonth;

    private TextView tvMonth;
    private View llMeal;
    private ListView mMenuListView;
    private TextView tvEmpty;

    private View ivLastMonth;
    private View ivNextMonth;

    private MealMenuListAdapter mealAdapter;
    private Map<String, Boolean> mealPicMap;

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
        this.mealMonth = Calendar.getInstance();
        this.mealMonth.set(Calendar.DAY_OF_MONTH, 1);
        mealPicMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_meal_list, null);
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

        llMeal = view.findViewById(R.id.ll_meal);
        tvEmpty = (TextView) view.findViewById(R.id.tv_empty_list);

        ivLastMonth = view.findViewById(R.id.iv_last_month);
        ivNextMonth = view.findViewById(R.id.iv_next_month);

        ivLastMonth.setOnClickListener(mClick);
        ivNextMonth.setOnClickListener(mClick);

        mMenuListView = (ListView) view.findViewById(R.id.list_meal);
        mealAdapter = new MealMenuListAdapter(getActivity(), mealMonth, new MenuData[0], mealPicMap);
        mMenuListView.setAdapter(mealAdapter);

    }

    private void loadMealList() {
        tvMonth.setText(String.format(getActivity().getResources().getText(R.string.meal_title).toString(), (mealMonth.get(Calendar.MONTH) + 1)));
        LoadingDialog.showLoading(getActivity());
        mealPicMap.clear();
        final AsyncTask mealTask = new AsyncTask<Object, Void, MenuData[]>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected MenuData[] doInBackground(Object... params) {
                return SchoolApi.getMonthlyMenu(SchoolApi.getContry(mMember.mSchoolVO.sido),
                        mMember.mSchoolVO.code,
                        SchoolApi.getSchoolType(mMember.mSchoolVO.gubun2),
                        mealMonth.get(Calendar.YEAR),
                        mealMonth.get(Calendar.MONTH) + 1);
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
                    mealAdapter.setMenuArray(mealMonth, menuList);
                    mealAdapter.notifyDataSetChanged();
                    if (mealMonth.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                        mMenuListView.post(new Runnable() {
                            @Override
                            public void run() {
                                mMenuListView.setSelection(mealAdapter.getPosition(Calendar.getInstance().get(Calendar.DATE)));
                            }
                        });
                    }

                }
            }
        };
        try {
            String url = Constant.HOST + Constant.API_GET_DINING_LIST;
            String dateString = String.valueOf(mealMonth.get(Calendar.YEAR)) + "-" + String.valueOf(mealMonth.get(Calendar.MONTH) + 1);
            JSONObject json = new JSONObject();
            json.put("school_id", LoginManager.getInstance().getLoginUser().mSchoolVO.school_id);
            json.put("dining_date", dateString);

            SchoolLog.d("LDK", "url:" + url);
            SchoolLog.d("LDK", "input parameter:" + json.toString(1));

            new AQuery(getActivity()).post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if (status.getCode() != 200) {
                            return;
                        }

                        if ("0".equals(object.getString("result"))) {
                            JSONArray array = object.getJSONArray("data");
                            for(int i=0; i < array.length(); ++i) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                mealPicMap.put(jsonObject.getString("dining_date") + "_" + jsonObject.getString("category"), true);
                            }
                        } else {
                            Util.showToast(getActivity(), object.getString("msg"));
                        }
                        mealTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_last_month) {
                if (mealMonth.get(Calendar.MONTH) == 0) {
                    mealMonth.set(Calendar.YEAR, mealMonth.get(Calendar.YEAR) - 1);
                }
                mealMonth.set(Calendar.MONTH, mealMonth.get(Calendar.MONTH) - 1);
            } else if (v.getId() == R.id.iv_next_month) {
                if (mealMonth.get(Calendar.MONTH) == 11) {
                    mealMonth.set(Calendar.YEAR, mealMonth.get(Calendar.YEAR) +1);
                }
                mealMonth.set(Calendar.MONTH, mealMonth.get(Calendar.MONTH) + 1);
            }
            setButtonVisible();
            loadMealList();
        }
    };

    private void setButtonVisible() {
        if (mealMonth.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
            ivLastMonth.setVisibility(View.VISIBLE);
            ivNextMonth.setVisibility(View.VISIBLE);
        } else if (mealMonth.get(Calendar.MONTH) < Calendar.getInstance().get(Calendar.MONTH)) {
            ivLastMonth.setVisibility(View.GONE);
            ivNextMonth.setVisibility(View.VISIBLE);
        } else {
            ivLastMonth.setVisibility(View.VISIBLE);
            ivNextMonth.setVisibility(View.GONE);
        }
    }
}
