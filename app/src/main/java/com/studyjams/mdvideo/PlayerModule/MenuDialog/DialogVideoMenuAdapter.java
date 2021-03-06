package com.studyjams.mdvideo.PlayerModule.MenuDialog;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studyjams.mdvideo.Data.bean.Subtitle;
import com.studyjams.mdvideo.Data.bean.Video;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewCursorAdapter;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewCursorViewHolder;

import java.util.ArrayList;

/**
 * Created by syamiadmin on 2016/8/31.
 */
public class DialogVideoMenuAdapter extends RecyclerViewCursorAdapter<DialogVideoMenuAdapter.VideoViewHolder> {

    private static final String TAG = "DialogVideoMenuAdapter";
    private ArrayList<Video> mVideoData;
    private ArrayList<Subtitle> mSubtitleData;
    private String selected;

    private int mType;

    /**
     * Constructor.
     * @param context The Context the Adapter is displayed in.
     */
    public DialogVideoMenuAdapter(Context context, String defaultId,int type) {
        super(context);
        selected = defaultId;
        setupCursorAdapter(null, 0, R.layout.dialogfragment_video_list_item, false);
        mVideoData = new ArrayList<>();
        mSubtitleData = new ArrayList<>();
        mType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 返回单个item的数据
     * @param position
     * @return
     */
    public Video getVideoItemData(int position){
        return mVideoData.get(position);
    }

    /**
     * 返回单个item的数据
     * @param position
     * @return
     */
    public Subtitle getSubtitleItemData(int position){
        return mSubtitleData.get(position);
    }

    /**
     * Returns the ViewHolder to use for this adapter.
     */
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }

    /**
     * Moves the Cursor of the CursorAdapter to the appropriate position and binds the view for
     * that item.
     */
    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

        // Move cursor to this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    /**
     * ViewHolder used to display a movie name.
     */
    public class VideoViewHolder extends RecyclerViewCursorViewHolder {

        public final TextView mTitle;
        public VideoViewHolder(View view) {
            super(view);

            mTitle = (TextView) view.findViewById(R.id.dialog_list_item_title);
        }

        @Override
        public void bindCursor(Cursor cursor) {

            if (mType == VideoMenuDialog.VIDEO) {
                Video video = Video.from(cursor);

                if (selected.equals(video.getId())) {
                    mTitle.setTextColor(mContext.getResources().getColor(R.color.accent));
                } else {
                    mTitle.setTextColor(mContext.getResources().getColor(R.color.primary));
                }

                /**save data for click event**/
                mVideoData.add(getAdapterPosition(), video);
                mTitle.setText(video.getTitle());
            } else if (mType == VideoMenuDialog.SUBTITLE) {
                Subtitle subtitle = Subtitle.from(cursor);

                if (selected.equals(subtitle.getPath())) {
                    mTitle.setTextColor(mContext.getResources().getColor(R.color.accent));
                } else {
                    mTitle.setTextColor(mContext.getResources().getColor(R.color.primary));
                }

                /**save data for click event**/
                mSubtitleData.add(getAdapterPosition(), subtitle);
                mTitle.setText(subtitle.getTitle());
            }
        }
    }
}
