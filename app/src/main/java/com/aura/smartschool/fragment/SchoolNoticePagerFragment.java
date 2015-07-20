package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.smartschool.R;
import com.aura.smartschool.fragment.schoolNoticeFragments.SchoolLetterListFragment;
import com.aura.smartschool.fragment.schoolNoticeFragments.SchoolNoticeListFragment;
import com.aura.smartschool.fragment.schoolNoticeFragments.SchoolScheduleFragment;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-06-14.
 */
public class SchoolNoticePagerFragment extends BaseFragment implements View.OnClickListener{

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private View mView;
    private FragmentStatePagerAdapter adapterViewPager;
    private ViewPager mViewPager;

    private View mTabMonthSchedule;
    private View mTabNotice;
    private View mTabSchoolLetter;

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
                    return SchoolScheduleFragment.newInstance(mMember);
                case 1:
                    return SchoolNoticeListFragment.newInstance(mMember);
                case 2:
                    return SchoolLetterListFragment.newInstance(mMember);
                default:
                    return null;
            }
        }
   }
}
