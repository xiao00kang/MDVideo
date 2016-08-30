package com.studyjams.mdvideo.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyjams.mdvideo.Bean.VideoBean;
import com.studyjams.mdvideo.DatabaseHelper.Tables;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.Util.Tools;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewCursorAdapter;
import com.studyjams.mdvideo.View.ProRecyclerView.RecyclerViewCursorViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by syamiadmin on 2016/7/12.
 */
public class VideoLocalCursorAdapter extends RecyclerViewCursorAdapter<VideoLocalCursorAdapter.VideoViewHolder> {

    private static final String TAG = "LocalVideoCursorAdapter";
    private List<VideoBean> mVideoData;
    private SimpleDateFormat mDateFormat;
    /**
     * Constructor.
     * @param context The Context the Adapter is displayed in.
     */
    public VideoLocalCursorAdapter(Context context) {
        super(context);

        setupCursorAdapter(null, 0, R.layout.video_local_list_item, false);
        mVideoData = new ArrayList<>();

        /**Format time**/
        mDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
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
    public VideoBean getItemData(int position){
        return mVideoData.get(position);
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

        public VideoViewHolder(View view) {
            super(view);

            mThumbnail = (ImageView) view.findViewById(R.id.local_list_item_image);
            mTitle = (TextView) view.findViewById(R.id.local_list_item_title);
            mInfo = (TextView) view.findViewById(R.id.local_list_item_info);
            mSize = (TextView) view.findViewById(R.id.local_list_item_size);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            VideoBean video = new VideoBean();

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Tables.Video_id));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_title));
            String album = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_album));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_artist));
            String displayName = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_displayName));
            String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_mimeType));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_path));
            long playDuration = cursor.getLong(cursor.getColumnIndexOrThrow(Tables.Video_playDuration));
            long duration = cursor.getInt(cursor.getColumnIndexOrThrow(Tables.Video_duration));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(Tables.Video_size));
            String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_createdDate));

//            Log.d(TAG, "bindCursor: " + "id=" + id + "\ntitle=" + title + "\nalbum=" + album
//            + "\nartist=" + artist + "\ndisplayName=" + displayName + "\nmimeType=" + mimeType
//            + "\npath=" + path + "\nduration=" + duration + "\nsize=" + size);

            video.setId(id);
            video.setTitle(title);
            video.setAlbum(album);
            video.setArtist(artist);
            video.setDisplayName(displayName);
            video.setMimeType(mimeType);
            video.setPath(path);
            video.setDuration(duration);
            video.setSize(size);
            video.setPlayDuration(playDuration);
            video.setCreatedDate(createdDate);

            /**save data for click event**/
            mVideoData.add(getAdapterPosition(),video);

            mTitle.setText(title);
            mInfo.setText(mDateFormat.format(duration));
            mSize.setText(Formatter.formatFileSize(mContext,size));
            Tools.LoadNormalImage(mContext,path,mThumbnail);
        }
    }
}

