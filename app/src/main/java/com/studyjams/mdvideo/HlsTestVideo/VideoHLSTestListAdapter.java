package com.studyjams.mdvideo.HlsTestVideo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyjams.mdvideo.R;

/**
 * Created by syamiadmin on 2016/6/8.
 */
public class VideoHLSTestListAdapter extends RecyclerView.Adapter{

    private static final String TAG = "VideoListAdapter";

    private Context mContext;
//    private Sample[] mData;
    private LayoutInflater mLayoutInflater;
    public VideoHLSTestListAdapter(Context context) {
        mContext = context;
//        mData = Samples.LIVE_DASH;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder videoViewHolder = (VideoViewHolder)holder;
//        Tools.LoadNormalImage(mContext,mData[position].uri,videoViewHolder.imageView);
//        videoViewHolder.textView.setText(mData[position].name);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(mLayoutInflater.inflate(R.layout.video_list_item,parent,false));
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public VideoViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.video_item_preview);
            textView = (TextView) view.findViewById(R.id.video_item_title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    onSampleSelected(mData[getAdapterPosition()]);
                }
            });
        }
    }

//    private void onSampleSelected(Sample sample) {
//        Intent mpdIntent = new Intent(mContext, PlayerActivity.class)
//                .setData(Uri.parse(sample.uri))
//                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, sample.contentId)
//                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, sample.type)
//                .putExtra(PlayerActivity.PROVIDER_EXTRA, sample.provider);
//        mContext.startActivity(mpdIntent);
//    }
}
