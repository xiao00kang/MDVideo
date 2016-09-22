package com.studyjams.mdvideo.LocalVideo;

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

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.Data.source.remote.SyncService;
import com.studyjams.mdvideo.PlayerModule.PlayerActivity;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemClickListener;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemDivider;

import java.lang.ref.WeakReference;

/**
 * Created by zwx on 2016/7/9.
 */

public class LocalVideoListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewItemClickListener.OnItemClickListener,LocalVideoContract.View{

    private LocalVideoContract.Presenter mPresenter;

    private static final String TAG = "LocalVideoListFragment";
    private static final String ARG_PARAM = "param";

    private String mParam;

    private RecyclerView mRecyclerView;
    private View mNoVideoView;
    private View mVideoView;

    //本地视频的loader编号
    private static final int LOCAL_VIDEO_LOADER = 0;
    private LocalVideoCursorAdapter mLocalVideoCursorAdapter;
    /**Loader管理器**/
    private LoaderManager mLoaderManager;
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
        mLocalVideoCursorAdapter.swapCursor(cursor);
        mNoVideoView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoVideos() {
        mNoVideoView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(LocalVideoContract.Presenter presenter) {
        Log.d(TAG, "===========setPresenter: " + presenter);
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

    public void refreshData(){

        SyncService.startActionCheck(getActivity());
        SyncService.startActionTraversal(getActivity());
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
                refreshData();
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

        mLocalVideoCursorAdapter = new LocalVideoCursorAdapter(getActivity());
        mRecyclerView.setAdapter(mLocalVideoCursorAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), this));
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoaderManager = getActivity().getSupportLoaderManager();
        mLoaderManager.initLoader(LOCAL_VIDEO_LOADER, null, this);
        mVideoObserver = new VideoObserver(new MyHandler(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "=======onResume: " + mPresenter);
//        mPresenter.start();

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
        if(requestCode == getActivity().RESULT_OK){

        }
    }

    @Override
    public void onItemClick(View view, int position) {

            Intent intent = new Intent(getActivity(), PlayerActivity.class)
                    .setData(Uri.parse(mLocalVideoCursorAdapter.getItemData(position).getPath()))
                    .putExtra(PlayerActivity.CONTENT_ID_EXTRA, String.valueOf(mLocalVideoCursorAdapter.getItemData(position).getId()))
                    .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, mLocalVideoCursorAdapter.getItemData(position).getMimeType())
                    .putExtra(PlayerActivity.PROVIDER_EXTRA,"0");
            getActivity().startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOCAL_VIDEO_LOADER:
                return new CursorLoader(
                        getActivity(),
                        SamplesPersistenceContract.VideoEntry.buildVideosUri(),
                        null,
                        null,
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
            case LOCAL_VIDEO_LOADER:
                mLocalVideoCursorAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case LOCAL_VIDEO_LOADER:
                mLocalVideoCursorAdapter.swapCursor(data);
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

        /*package*/ VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "===============onChange: 数据变更");
            //此处可以进行相应的业务处理
            mLoaderManager.restartLoader(LOCAL_VIDEO_LOADER,null,LocalVideoListFragment.this);
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