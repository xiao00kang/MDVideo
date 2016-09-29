package com.studyjams.mdvideo.MainFrame;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.studyjams.mdvideo.RecordVideo.VideoPlayHistoryFragment;
import com.studyjams.mdvideo.LocalVideo.LocalVideoListFragment;
import com.studyjams.mdvideo.HlsTestVideo.VideoHLSTestListFragment;

import java.util.List;

/**
 * Created by syamiadmin on 2016/7/6.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<String> mData;
    public MainPagerAdapter(FragmentManager fm, List<String> list) {
        super(fm);
        mData = list;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 2:
                return VideoHLSTestListFragment.newInstance(mData.get(position));
            case 0:
                return LocalVideoListFragment.newInstance(mData.get(position));
            default:
                return VideoPlayHistoryFragment.newInstance(mData.get(position));
        }

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position);
    }
}
