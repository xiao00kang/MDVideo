package com.studyjams.mdvideo.RecordVideo;


import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyjams.mdvideo.Data.bean.Video;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemDivider;

import java.lang.ref.WeakReference;

public class VideoPlayHistoryFragment extends Fragment implements VideoPlayHistoryContract.View{

    private VideoPlayHistoryContract.Presenter mPresenter;

    private static final String TAG = "HistoryFragment";
    private static final String ARG_PARAM = "param";

    private String mParam;

    private RecyclerView mRecyclerView;
    private View mNoVideoView;
    private View mVideoView;

    private VideoPlayHistoryCursorAdapter mVideoPlayHistoryCursorAdapter;

    private VideoObserver mVideoObserver;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**在Adapter中设置点击事件的回调接口**/
    public interface VideoItemListener{
        void OnClick(Video video);
        void OnLongClick(Video video);
    }

    private VideoPlayHistoryFragment.VideoItemListener mVideoItemListener = new VideoPlayHistoryFragment.VideoItemListener() {
        @Override
        public void OnClick(Video video) {
            mPresenter.playVideo(video);
        }

        @Override
        public void OnLongClick(Video video) {
            mPresenter.playVideo(video);
        }
    };

    @Override
    public void showNoVideos() {
        /**如果数据刷新完成，隐藏下拉刷新**/
        if (mSwipeRefreshLayout.isRefreshing()) {

            mSwipeRefreshLayout.setRefreshing(false);
        }
        mVideoPlayHistoryCursorAdapter.swapCursor(null);

        mNoVideoView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
    }

    @Override
    public void showVideos(Cursor cursor) {
        /**如果数据刷新完成，隐藏下拉刷新**/
        if (mSwipeRefreshLayout.isRefreshing()) {

            mSwipeRefreshLayout.setRefreshing(false);
        }

        mNoVideoView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);

        mVideoPlayHistoryCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void setPresenter(VideoPlayHistoryContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public VideoPlayHistoryFragment() {
        // Required empty public constructor
    }

    public static VideoPlayHistoryFragment newInstance(String param) {
        VideoPlayHistoryFragment fragment = new VideoPlayHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_video_play_history, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)parent.findViewById(R.id.video_play_history_SwipeRefreshLayout);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorIcon);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshData();
            }
        });

        mNoVideoView = parent.findViewById(R.id.record_no_video);
        mVideoView = parent.findViewById(R.id.record_show_video);

        mRecyclerView = (RecyclerView) parent.findViewById(R.id.video_play_history_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity(), RecyclerViewItemDivider.VERTICAL_LIST));
        mVideoPlayHistoryCursorAdapter = new VideoPlayHistoryCursorAdapter(getActivity(),mVideoItemListener);
        mRecyclerView.setAdapter(mVideoPlayHistoryCursorAdapter);
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new VideoPlayHistoryPresenter(getActivity(),getActivity().getSupportLoaderManager(),this);
        mPresenter.start();
        mVideoObserver = new VideoObserver(new MyHandler(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册数据库变化监听
        getActivity().getContentResolver().registerContentObserver(SamplesPersistenceContract.VideoEntry.buildVideosUri(), true, mVideoObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消数据库变化监听
        getActivity().getContentResolver().unregisterContentObserver(mVideoObserver);
    }



    private class VideoObserver extends ContentObserver {

        public VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "===============onChange: 更新播放历史");
            //此处可以进行相应的业务处理
            mPresenter.refreshData();
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                Log.d(TAG, "handleMessage: " + msg.what);
            }
        }
    }
}
