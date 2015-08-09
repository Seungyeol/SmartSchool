package com.aura.smartschool.fragment;

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
import com.aura.smartschool.vo.MemberVO;
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
    private ArrayList<SchoolNotiVO> mSchoolScheduleList = new ArrayList<>();

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
        getSchoolNotiList();
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

    private void getSchoolNotiList() {
        LoadingDialog.showLoading(getActivity());
        try {
            AQuery aQuery = new AQuery(getActivity());
            String url = Constant.HOST + Constant.API_GET_SCHOOL_NOTI_LIST;

            JSONObject json = new JSONObject();
//            json.put("school_id", mMember.mSchoolVO.school_id);
            json.put("school_id", 18247);

            Log.d("LDK", "url:" + url);
            Log.d("LDK", "input parameter:" + json.toString(1));

            aQuery.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();

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
            case CATEGORY_SCHEDULE:
                mSchoolScheduleList.add(notiVO);
                mSchoolScheduleFragment.setScheduleList(mSchoolScheduleList);
                break;
        }
    }
}
