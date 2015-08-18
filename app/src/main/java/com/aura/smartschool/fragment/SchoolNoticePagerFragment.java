package com.aura.smartschool.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.R;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.fragment.schoolNoticeFragments.SchoolLetterListFragment;
import com.aura.smartschool.fragment.schoolNoticeFragments.SchoolNoticeListFragment;
import com.aura.smartschool.fragment.schoolNoticeFragments.SchoolScheduleFragment;
import com.aura.smartschool.utils.SchoolApi;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.ScheduleData;
import com.aura.smartschool.vo.SchoolNotiVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2015-06-14.
 */
public class SchoolNoticePagerFragment extends Fragment implements View.OnClickListener{

    private static final int CATEGORY_LETTER = 1;
    private static final int CATEGORY_NOTI = 2;
    private static final int CATEGORY_SCHEDULE = 3;

    private volatile boolean isScheduleJobDone, isNotiJobDone, isLetterJobDone;

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private View mView;
    private FragmentStatePagerAdapter adapterViewPager;
    private ViewPager mViewPager;

    private View mTabMonthSchedule;
    private View mTabNotice;
    private View mTabSchoolLetter;

    private SchoolLetterListFragment mSchoolLetterFragment;
    private SchoolNoticeListFragment mSchoolNotiFragment;
    private SchoolScheduleFragment mSchoolScheduleFragment;

    private ArrayList<SchoolNotiVO> mSchoolLetterList = new ArrayList<>();
    private ArrayList<SchoolNotiVO> mSchoolNotiList = new ArrayList<>();

    private Calendar selCalendar = Calendar.getInstance();

    public static SchoolNoticePagerFragment newInstance(MemberVO member) {
        SchoolNoticePagerFragment instance = new SchoolNoticePagerFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mMember = (MemberVO) args.getSerializable(KEY_MEMBER);
        getSchoolSchedule(selCalendar);
        getSchoolNotiList(CATEGORY_LETTER);
        getSchoolNotiList(CATEGORY_NOTI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_notice_view_pager, null);

        mTabMonthSchedule = mView.findViewById(R.id.fl_tab_month_schedule);
        mTabNotice = mView.findViewById(R.id.fl_tab_notice);
        mTabSchoolLetter = mView.findViewById(R.id.fl_tab_school_letter);

        mTabMonthSchedule.setOnClickListener(this);
        mTabNotice.setOnClickListener(this);
        mTabSchoolLetter.setOnClickListener(this);

        mTabMonthSchedule.setSelected(true);

        mSchoolLetterFragment = SchoolLetterListFragment.newInstance(mMember);
        mSchoolNotiFragment = SchoolNoticeListFragment.newInstance(mMember);
        mSchoolScheduleFragment = SchoolScheduleFragment.newInstance(mMember, selCalendar);
        mSchoolScheduleFragment.setMonthChangedListener(new SchoolScheduleFragment.OnMonthChangedListener() {
            @Override
            public void onMonthChaged(Calendar month) {
                getSchoolSchedule(month);
            }
        });

        mViewPager = (ViewPager) mView.findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(this.getActivity().getSupportFragmentManager(), mMember);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mTabMonthSchedule.setSelected(false);
                mTabNotice.setSelected(false);
                mTabSchoolLetter.setSelected(false);

                if (position == 0) {
                    mTabMonthSchedule.setSelected(true);
                } else if (position == 1) {
                    mTabNotice.setSelected(true);
                } else {
                    mTabSchoolLetter.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_tab_month_schedule:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.fl_tab_notice:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fl_tab_school_letter:
                mViewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    private void getSchoolSchedule(final Calendar selectedMonth) {
        AsyncTask scheduleTask = new AsyncTask<Object, Void, ScheduleData[]>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isScheduleJobDone = false;
                showLoading(CATEGORY_SCHEDULE);
            }

            @Override
            protected ScheduleData[] doInBackground(Object... params) {

                return SchoolApi.getMonthlySchedule(SchoolApi.getContry(mMember.mSchoolVO.sido),
                        mMember.mSchoolVO.code,
                        SchoolApi.getSchoolType(mMember.mSchoolVO.gubun2),
                        selectedMonth.get(Calendar.YEAR),
                        selectedMonth.get(Calendar.MONTH) + 1);
            }

            @Override
            protected void onPostExecute(ScheduleData[] scheduleDatas) {
                super.onPostExecute(scheduleDatas);
                isScheduleJobDone = true;
                mSchoolScheduleFragment.setScheduleDatas(scheduleDatas);
                hideLoading(CATEGORY_SCHEDULE);
            }
        };
        scheduleTask.execute();
    }

    private void getSchoolNotiList(final int category) {
        isNotiJobDone = false;
        showLoading(category);
        try {
            AQuery aQuery = new AQuery(getActivity());
            String url = Constant.HOST + Constant.API_GET_SCHOOL_NOTI_LIST_BY_MEMBER;

            JSONObject json = new JSONObject();
            json.put("school_id", mMember.mSchoolVO.school_id);
            json.put("category", category);
            json.put("member_id", mMember.member_id);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            aQuery.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    isNotiJobDone = true;
                    hideLoading(category);

                    try {
                        if (status.getCode() != 200) {
                            return;
                        }
                        Log.d("LDK", "result:" + object.toString(1));

                        if (object.getInt("result") == 0) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for(int i = 0; i < jsonArray.length(); i++){
                                SchoolNotiVO notiVO = new SchoolNotiVO();
                                notiVO.notiSeq = ((JSONObject)jsonArray.get(i)).getInt("noti_seq");
                                notiVO.schoolId = ((JSONObject)jsonArray.get(i)).getInt("school_id");
                                notiVO.category = ((JSONObject)jsonArray.get(i)).getInt("category");
                                notiVO.title = ((JSONObject)jsonArray.get(i)).getString("title");
                                notiVO.content = ((JSONObject)jsonArray.get(i)).getString("content");
                                notiVO.notiDate = ((JSONObject)jsonArray.get(i)).getString("noti_date");
                                notiVO.startIndex = ((JSONObject)jsonArray.get(i)).getInt("start_index");
                                notiVO.pageSize = ((JSONObject)jsonArray.get(i)).getInt("page_size");
                                notiVO.memberId = ((JSONObject)jsonArray.get(i)).getInt("member_id");
                                addNotiToList(notiVO);
                            }
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

    private void showLoading(int category) {
        switch (category) {
            case CATEGORY_LETTER:
                isLetterJobDone = false;
                break;
            case CATEGORY_NOTI:
                isNotiJobDone = false;
                break;
            case CATEGORY_SCHEDULE:
                isScheduleJobDone = false;
                break;
        }
        LoadingDialog.showLoading(getActivity());
    }

    private void hideLoading(int category) {
        switch (category) {
            case CATEGORY_LETTER:
                isLetterJobDone = true;
                break;
            case CATEGORY_NOTI:
                isNotiJobDone = true;
                break;
            case CATEGORY_SCHEDULE:
                isScheduleJobDone = true;
                break;
        }
        if (isNotiJobDone && isScheduleJobDone && isLetterJobDone) {
            LoadingDialog.hideLoading();
        }
    }

    private void addNotiToList(SchoolNotiVO notiVO) {
        switch (notiVO.category) {
            case CATEGORY_LETTER:
                mSchoolLetterList.add(notiVO);
                mSchoolLetterFragment.setLetterList(mSchoolLetterList);
                break;
            case CATEGORY_NOTI:
                mSchoolNotiList.add(notiVO);
                mSchoolNotiFragment.setNotiList(mSchoolNotiList);
                break;
            //나이스 api 로 변경됨
//            case CATEGORY_SCHEDULE:
//                mSchoolScheduleList.add(notiVO);
//                mSchoolScheduleFragment.setScheduleList(mSchoolScheduleList);
//                break;
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 3;
        private MemberVO mMember;
        public MyPagerAdapter(FragmentManager fragmentManager, MemberVO memberVO) {
            super(fragmentManager);
            this.mMember = memberVO;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mSchoolScheduleFragment;
                case 1:
                    return mSchoolNotiFragment;
                case 2:
                    return mSchoolLetterFragment;
                default:
                    return null;
            }
        }
    }
}
