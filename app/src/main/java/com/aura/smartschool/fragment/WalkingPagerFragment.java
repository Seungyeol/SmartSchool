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
import com.aura.smartschool.fragment.walkingfragments.WalkingAmountFragment;
import com.aura.smartschool.vo.MemberVO;

/**
 * Created by Administrator on 2015-06-14.
 */
public class WalkingPagerFragment extends BaseFragment {

    private static String KEY_MEMBER = "member";
    private MemberVO mMember;

    private View mView;
    private FragmentStatePagerAdapter adapterViewPager;
    private ViewPager mVpPager;

    private View mTabWalkingCount;
    private View mTabWalkingHistory;
    private View mTabTracking;

    public static WalkingPagerFragment newInstance(MemberVO member) {

        WalkingPagerFragment instance = new WalkingPagerFragment();

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
        mView = View.inflate(getActivity(), R.layout.fragment_view_pager, null);

        mTabWalkingCount = mView.findViewById(R.id.fl_tab_walking_count);
        mTabWalkingHistory = mView.findViewById(R.id.fl_tab_walking_history);
        mTabTracking = mView.findViewById(R.id.fl_tab_tracking);
        mTabWalkingCount.setSelected(true);

        mVpPager = (ViewPager) mView.findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(this.getActivity().getSupportFragmentManager(), mMember);
        mVpPager.setAdapter(adapterViewPager);
        mVpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mTabWalkingCount.setSelected(false);
                mTabWalkingHistory.setSelected(false);
                mTabTracking.setSelected(false);
                if (position == 0) {
                    mTabWalkingCount.setSelected(true);
                } else if (position == 1) {
                    mTabWalkingHistory.setSelected(true);
                } else {
                    mTabTracking.setSelected(true);
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
        mVpPager.setCurrentItem(0);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 1;
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
                    return WalkingAmountFragment.newInstance(mMember);

                default:
                    return null;
            }
        }
   }
}
