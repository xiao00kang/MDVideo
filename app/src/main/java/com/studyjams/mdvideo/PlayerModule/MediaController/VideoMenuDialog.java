package com.studyjams.mdvideo.PlayerModule.MediaController;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.studyjams.mdvideo.Adapter.DialogVideoMenuAdapter;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.PlayerModule.PlayerActivity;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemClickListener;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewItemDivider;

/**
 * Created by syamiadmin on 2016/8/30.
 */
public class VideoMenuDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewItemClickListener.OnItemClickListener{
    private static final String TAG = "VideoMenuDialog";
    private VideoSelected mVideoSelected;
    private LoaderManager mLoaderManager;
    private static final int DIALOG_LOADER = 121;
    private RecyclerView mRecyclerView;
    private DialogVideoMenuAdapter mDialogVideoMenuAdapter;
    /**播放视频的ID**/
    private String contentId = "";

    public VideoMenuDialog(){}

    public static VideoMenuDialog newInstance(){

        return new VideoMenuDialog();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        contentId = tag;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface VideoSelected{
        void onVideoSelected(Intent intent);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        Window window = dialog.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogMenuAnimation;
        window.setGravity(Gravity.END);
        super.setupDialog(dialog, style);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mVideoSelected = (VideoSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoRefreshListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.dialogfragment_video_list, container, false);

        ImageButton back = (ImageButton) parent.findViewById(R.id.dialog_menu_exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mRecyclerView = (RecyclerView) parent.findViewById(R.id.dialog_fragment_video_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity(), RecyclerViewItemDivider.VERTICAL_LIST));
        mDialogVideoMenuAdapter = new DialogVideoMenuAdapter(getActivity(),contentId);
        mRecyclerView.setAdapter(mDialogVideoMenuAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), this));

        return parent;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), PlayerActivity.class)
                .setData(Uri.parse(mDialogVideoMenuAdapter.getItemData(position).getPath()))
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, String.valueOf(mDialogVideoMenuAdapter.getItemData(position).getId()))
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, mDialogVideoMenuAdapter.getItemData(position).getMimeType())
                .putExtra(PlayerActivity.PROVIDER_EXTRA,"0");
        mVideoSelected.onVideoSelected(intent);
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoaderManager = getActivity().getSupportLoaderManager();
        mLoaderManager.initLoader(DIALOG_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case DIALOG_LOADER:
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
            case DIALOG_LOADER:
                mDialogVideoMenuAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case DIALOG_LOADER:
                mDialogVideoMenuAdapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }
}
