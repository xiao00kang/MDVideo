package com.studyjams.mdvideo.RecordVideo;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyjams.mdvideo.Data.bean.Video;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.Util.Tools;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewCursorAdapter;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewCursorViewHolder;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by syamiadmin on 2016/7/12.
 */
public class VideoPlayHistoryCursorAdapter extends RecyclerViewCursorAdapter<VideoPlayHistoryCursorAdapter.VideoViewHolder> {

    private static final String TAG = "LocalVideoCursorAdapter";
    private SimpleDateFormat mDateFormat;
    private VideoPlayHistoryFragment.VideoItemListener mVideoItemListener;

    /**
     * Constructor.
     * @param context The Context the Adapter is displayed in.
     */
    public VideoPlayHistoryCursorAdapter(Context context,VideoPlayHistoryFragment.VideoItemListener videoItemListener) {
        super(context);

        setupCursorAdapter(null, 0, R.layout.video_play_history_item, false);
        mVideoItemListener = videoItemListener;

        /**Format time**/
        mDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
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

        public final ImageView mThumbnail;
        public final TextView mTitle;
        public final TextView mInfo;
        public final TextView mSize;
        public final TextView mDate;
        public final View mParent;

        public VideoViewHolder(View view) {
            super(view);
            mParent = view;
            mThumbnail = (ImageView) view.findViewById(R.id.play_history_item_image);
            mTitle = (TextView) view.findViewById(R.id.play_history_item_title);
            mInfo = (TextView) view.findViewById(R.id.play_history_item_info);
            mSize = (TextView) view.findViewById(R.id.play_history_item_size);
            mDate = (TextView) view.findViewById(R.id.play_history_item_time);
        }

        @Override
        public void bindCursor(Cursor cursor) {

            final Video video = Video.from(cursor);

            mParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mVideoItemListener.OnLongClick(video);
                    return false;
                }
            });
            mParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoItemListener.OnClick(video);
                }
            });

            String str;
            if(video.isPlayCompleted()){
                str = mContext.getResources().getString(R.string.player_play_end);
            }else{
                str = mContext.getResources().getString(R.string.player_play_history) + mDateFormat.format(video.getPlayDuration());
            }

            mTitle.setText(video.getTitle());
            mInfo.setText(str);
            mSize.setText(Formatter.formatFileSize(mContext,video.getSize()));
            mDate.setText(video.getCreatedDate());
            Tools.LoadNormalImage(mContext,video.getPath(),mThumbnail);
        }
    }
}

