package com.studyjams.mdvideo.LocalVideo;

import android.app.Activity;
import android.content.Intent;
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

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemDivider;

import java.lang.ref.WeakReference;

/**
 * Created by zwx on 2016/7/9.
 */

public class LocalVideoListFragment extends Fragment implements LocalVideoContract.View{

    private LocalVideoContract.Presenter mPresenter;

    private static final String TAG = "LocalVideoListFragment";
    private static final String ARG_PARAM = "param";

    private String mParam;

    private RecyclerView mRecyclerView;
    private View mNoVideoView;
    private View mVideoView;

    private LocalVideoCursorAdapter mLocalVideoCursorAdapter;
    private VideoObserver mVideoObserver;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**在Adapter中设置点击事件的回调接口**/
    public interface VideoItemListener{
        void OnClick(Video video);
        void OnLongClick(Video video);
    }

    private VideoItemListener mVideoItemListener = new VideoItemListener() {
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
    public void showVideos(Cursor cursor) {

        /**如果数据刷新完成，隐藏下拉刷新**/
        if (mSwipeRefreshLayout.isRefreshing()) {

            mSwipeRefreshLayout.setRefreshing(false);
        }

        mLocalVideoCursorAdapter.swapCursor(cursor);
        mNoVideoView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoVideos() {
        mLocalVideoCursorAdapter.swapCursor(null);
        mNoVideoView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(LocalVideoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public LocalVideoListFragment() {
        // Required empty public constructor
    }

    public static LocalVideoListFragment newInstance(String param) {
        LocalVideoListFragment fragment = new LocalVideoListFragment();
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
        View parent = inflater.inflate(R.layout.fragment_video_local_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)parent.findViewById(R.id.local_video_list_SwipeRefreshLayout);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorIcon);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadVideos();
            }
        });

        mRecyclerView = (RecyclerView) parent.findViewById(R.id.local_video_list_recycler_view);
        mVideoView = parent.findViewById(R.id.local_show_video);
        mNoVideoView = parent.findViewById(R.id.local_no_video);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity(), RecyclerViewItemDivider.VERTICAL_LIST));

        mLocalVideoCursorAdapter = new LocalVideoCursorAdapter(getActivity(), mVideoItemListener);
        mRecyclerView.setAdapter(mLocalVideoCursorAdapter);
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoObserver = new VideoObserver(new MyHandler(getActivity()));
        //init local video presenter
        new LocalVideoPresenter(getActivity(),getActivity().getSupportLoaderManager(),this);

        mPresenter.start();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode,resultCode);
    }

    private class VideoObserver extends ContentObserver {

        /*package*/ VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "===============onChange: 数据变更");
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