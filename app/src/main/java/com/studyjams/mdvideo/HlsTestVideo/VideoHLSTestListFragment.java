package com.studyjams.mdvideo.HlsTestVideo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyjams.mdvideo.R;


public class VideoHLSTestListFragment extends Fragment {

    private static final String TAG = "VideoListFragment";
    private static final String ARG_PARAM = "param";

    private String mParam;

    private RecyclerView mRecyclerView;
    private VideoHLSTestListAdapter mVideoListAdapter;

    public VideoHLSTestListFragment() {
        // Required empty public constructor
    }

    public static VideoHLSTestListFragment newInstance(String param) {
        VideoHLSTestListFragment fragment = new VideoHLSTestListFragment();
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
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_video_list, container, false);
        mRecyclerView = (RecyclerView)parent.findViewById(R.id.video_list_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mVideoListAdapter = new VideoHLSTestListAdapter(getActivity());
        mRecyclerView.setAdapter(mVideoListAdapter);
        return parent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
