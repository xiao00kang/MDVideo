package com.studyjams.mdvideo.Fragment;

import android.app.Activity;
import android.content.Context;
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

import com.studyjams.mdvideo.Adapter.VideoLocalCursorAdapter;
import com.studyjams.mdvideo.DatabaseHelper.VideoProvider;
import com.studyjams.mdvideo.PlayerModule.PlayerActivity;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemClickListener;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemDivider;

import java.lang.ref.WeakReference;

/**
 * Created by zwx on 2016/7/9.
 */

public class VideoLocalListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewItemClickListener.OnItemClickListener{
    private static final String TAG = "LocalVideoListFragment";
    private static final String ARG_PARAM = "param";

    private String mParam;

    private RecyclerView mRecyclerView;

    //本地视频的loader编号
    private static final int LOCAL_VIDEO_LOADER = 0;
    private VideoLocalCursorAdapter mLocalVideoCursorAdapter;
    /**Loader管理器**/
    private LoaderManager mLoaderManager;
    private VideoObserver mVideoObserver;

    private OnVideoRefreshListener mOnVideoRefreshListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public VideoLocalListFragment() {
        // Required empty public constructor
    }

    public static VideoLocalListFragment newInstance(String param) {
        VideoLocalListFragment fragment = new VideoLocalListFragment();
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

    // Container Activity must implement this interface
    public interface OnVideoRefreshListener {
         void onVideoRefresh();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnVideoRefreshListener = (OnVideoRefreshListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoRefreshListener");
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
                mOnVideoRefreshListener.onVideoRefresh();
            }
        });

        mRecyclerView = (RecyclerView) parent.findViewById(R.id.local_video_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity(), RecyclerViewItemDivider.VERTICAL_LIST));

        mLocalVideoCursorAdapter = new VideoLocalCursorAdapter(getActivity());
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
        //注册数据库变化监听
        getActivity().getContentResolver().registerContentObserver(VideoProvider.VIDEO_CHANGE_URI, true, mVideoObserver);
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
                        VideoProvider.VIDEO_PLAY_HISTORY_URI,
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

        public VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "===============onChange: 数据变更");
            //此处可以进行相应的业务处理
            mLoaderManager.restartLoader(LOCAL_VIDEO_LOADER,null,VideoLocalListFragment.this);
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