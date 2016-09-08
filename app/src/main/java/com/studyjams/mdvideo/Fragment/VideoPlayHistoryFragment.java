package com.studyjams.mdvideo.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyjams.mdvideo.Adapter.VideoPlayHistoryCursorAdapter;
import com.studyjams.mdvideo.Data.source.SamplesProvider;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.PlayerModule.PlayerActivity;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemClickListener;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemDivider;

import java.lang.ref.WeakReference;

public class VideoPlayHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewItemClickListener.OnItemClickListener{
    private static final String TAG = "HistoryFragment";
    private static final String ARG_PARAM = "param";

    private String mParam;

    private RecyclerView mRecyclerView;
    private VideoPlayHistoryCursorAdapter mVideoPlayHistoryCursorAdapter;

    /**历史记录Loader的编号**/
    private static final int VIDEO_PLAY_HISTORY_LOADER = 1;
    /**Loader管理器**/
    private LoaderManager mLoaderManager;
    private VideoObserver mVideoObserver;

    private SwipeRefreshLayout mSwipeRefreshLayout;

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
                mLoaderManager.restartLoader(VIDEO_PLAY_HISTORY_LOADER,null,VideoPlayHistoryFragment.this);
            }
        });
        mRecyclerView = (RecyclerView) parent.findViewById(R.id.video_play_history_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity(), RecyclerViewItemDivider.VERTICAL_LIST));
        mVideoPlayHistoryCursorAdapter = new VideoPlayHistoryCursorAdapter(getActivity());
        mRecyclerView.setAdapter(mVideoPlayHistoryCursorAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), this));
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoaderManager = getActivity().getSupportLoaderManager();
        mLoaderManager.initLoader(VIDEO_PLAY_HISTORY_LOADER, null, this);
        mVideoObserver = new VideoObserver(new MyHandler(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册数据库变化监听
        getActivity().getContentResolver().registerContentObserver(SamplesProvider.VIDEO_CHANGE_URI, true, mVideoObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消数据库变化监听
        getActivity().getContentResolver().unregisterContentObserver(mVideoObserver);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), PlayerActivity.class)
                .setData(Uri.parse(mVideoPlayHistoryCursorAdapter.getItemData(position).getPath()))
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, String.valueOf(mVideoPlayHistoryCursorAdapter.getItemData(position).getId()))
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, mVideoPlayHistoryCursorAdapter.getItemData(position).getMimeType())
                .putExtra(PlayerActivity.PROVIDER_EXTRA,String.valueOf(mVideoPlayHistoryCursorAdapter.getItemData(position).getPlayDuration()));
        getActivity().startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case VIDEO_PLAY_HISTORY_LOADER:
                return new CursorLoader(
                        getActivity(),
                        SamplesProvider.VIDEO_PLAY_HISTORY_URI,
                        null,
                        SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PLAY_DURATION + " > -1",
                        null,
                        null
                );
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case VIDEO_PLAY_HISTORY_LOADER:
                mVideoPlayHistoryCursorAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case VIDEO_PLAY_HISTORY_LOADER:
                mVideoPlayHistoryCursorAdapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
        /**如果数据刷新完成，隐藏下拉刷新**/
        if (mSwipeRefreshLayout.isRefreshing()) {

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class VideoObserver extends ContentObserver {

        public VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "===============onChange: 更新播放历史");
            //此处可以进行相应的业务处理
            mLoaderManager.restartLoader(VIDEO_PLAY_HISTORY_LOADER,null,VideoPlayHistoryFragment.this);
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
