package com.studyjams.mdvideo.LocalVideo;

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
public class LocalVideoCursorAdapter extends RecyclerViewCursorAdapter<LocalVideoCursorAdapter.VideoViewHolder> {

    private static final String TAG = "LocalVideoCursorAdapter";
    private SimpleDateFormat mDateFormat;
    private LocalVideoListFragment.VideoItemListener mVideoItemListener;
    /**
     * Constructor.
     * @param context The Context the Adapter is displayed in.
     */
    public LocalVideoCursorAdapter(Context context, LocalVideoListFragment.VideoItemListener videoItemListener) {
        super(context);

        setupCursorAdapter(null, 0, R.layout.video_local_list_item, false);

        /**Format time**/
        mDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        mVideoItemListener = videoItemListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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
     class VideoViewHolder extends RecyclerViewCursorViewHolder {

        private final ImageView mThumbnail;
        private final TextView mTitle;
        private final TextView mInfo;
        private final TextView mSize;
        private final View mParent;

        private VideoViewHolder(View view) {
            super(view);

            mParent = view;
            mThumbnail = (ImageView) view.findViewById(R.id.local_list_item_image);
            mTitle = (TextView) view.findViewById(R.id.local_list_item_title);
            mInfo = (TextView) view.findViewById(R.id.local_list_item_info);
            mSize = (TextView) view.findViewById(R.id.local_list_item_size);
        }

        @Override
        public void bindCursor(Cursor cursor) {

            final Video video = Video.from(cursor);

            mParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoItemListener.OnClick(video);
                }
            });
            mParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mVideoItemListener.OnLongClick(video);
                    return true;
                }
            });

            mTitle.setText(video.getTitle());
            mInfo.setText(mDateFormat.format(video.getDuration()));
            mSize.setText(Formatter.formatFileSize(mContext,video.getSize()));
            Tools.LoadNormalImage(mContext,video.getPath(),mThumbnail);
        }
    }
}

