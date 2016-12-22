package com.studyjams.mdvideo.PlayerModule.MenuDialog;

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
import android.widget.TextView;

import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.PlayerModule.ExoPlayerV2.PlayerActivityV2;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.Util.D;
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
    private static final int DIALOG_LOADER_SUBTITLE = 123;
    private RecyclerView mRecyclerView;
    private DialogVideoMenuAdapter mDialogVideoMenuAdapter;
    /**播放视频的ID**/
    private String contentId = "";

    public static final int VIDEO = 0;
    public static final int SUBTITLE = 1;
    private static int mType;

    public VideoMenuDialog(){}

    public static VideoMenuDialog newInstance(int type){
        mType = type;
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
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        TextView title = (TextView)parent.findViewById(R.id.dialog_menu_title);
        if(mType == VIDEO){
            title.setText(getString(R.string.player_menu_title));
        }else if(mType == SUBTITLE){
            title.setText(getString(R.string.player_menu_title_subtitle));
        }

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
        mDialogVideoMenuAdapter = new DialogVideoMenuAdapter(getActivity(), contentId, mType);
        mRecyclerView.setAdapter(mDialogVideoMenuAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), this));

        return parent;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(),PlayerActivityV2.class);
        intent.setAction(PlayerActivityV2.ACTION_VIEW);
        intent.putExtra(PlayerActivityV2.CONTENT_POSITION_EXTRA, 0L);
        if(mType == VIDEO) {
            intent.putExtra(PlayerActivityV2.CONTENT_TYPE_INTENT, D.TYPE_VIDEO);
            intent.setData(Uri.parse(mDialogVideoMenuAdapter.getVideoItemData(position).getPath()));
            intent.putExtra(PlayerActivityV2.CONTENT_ID_EXTRA, String.valueOf(mDialogVideoMenuAdapter.getVideoItemData(position).getId()));
            intent.putExtra(PlayerActivityV2.CONTENT_TYPE_EXTRA, mDialogVideoMenuAdapter.getVideoItemData(position).getMimeType());
            intent.putExtra(PlayerActivityV2.CONTENT_SUBTITLE_EXTRA,mDialogVideoMenuAdapter.getVideoItemData(position).getSubtitlePath());
        }else if(mType == SUBTITLE){
            intent.putExtra(PlayerActivityV2.CONTENT_TYPE_INTENT, D.TYPE_SUBTITLE);
            intent.setData(Uri.parse(mDialogVideoMenuAdapter.getSubtitleItemData(position).getPath()));
            intent.putExtra(PlayerActivityV2.CONTENT_ID_EXTRA, String.valueOf(mDialogVideoMenuAdapter.getSubtitleItemData(position).getId()));
            intent.putExtra(PlayerActivityV2.CONTENT_TYPE_EXTRA, mDialogVideoMenuAdapter.getSubtitleItemData(position).getMimeType());
        }
        mVideoSelected.onVideoSelected(intent);
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoaderManager = getActivity().getSupportLoaderManager();
        if(mType == VIDEO){
            mLoaderManager.initLoader(DIALOG_LOADER, null, this);
        }else if(mType == SUBTITLE){
            mLoaderManager.initLoader(DIALOG_LOADER_SUBTITLE, null, this);
        }

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
            case DIALOG_LOADER_SUBTITLE:
                return new CursorLoader(
                        getActivity(),
                        SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(),
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
            case DIALOG_LOADER_SUBTITLE:
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
            case DIALOG_LOADER_SUBTITLE:
                mDialogVideoMenuAdapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }
}
