package com.studyjams.mdvideo.FileChooserModule.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyjams.mdvideo.FileChooserModule.FileChooserItem;
import com.studyjams.mdvideo.FileChooserModule.FileChooserProperties;
import com.studyjams.mdvideo.FileChooserModule.FileMarkedList;
import com.studyjams.mdvideo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by syamiadmin on 2016/8/11.
 */
public class FileChooserAdapter extends RecyclerView.Adapter{
    private static final String TAG = "FileChooserAdapter";
    private Context mContext;
    private ArrayList<FileChooserItem> mData;
    private FileChooserProperties mFileProperties;
    private NotifyItemChecked notifyItemChecked;

    public FileChooserAdapter(Context context, ArrayList<FileChooserItem> arrayList, FileChooserProperties properties) {
        mContext = context;
        mData = arrayList;
        mFileProperties = properties;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FileChooserViewHolder fileViewHolder = (FileChooserViewHolder)holder;
//        Tools.LoadNormalImage(mContext,mData[position].uri,videoViewHolder.imageView);
        final FileChooserItem item = mData.get(position);
        if (item.isDirectory()) {
            fileViewHolder.imageView.setImageResource(R.drawable.file_chooser_folder);
            if (mFileProperties.selection_type == FileChooserProperties.FILE_SELECT) {
                fileViewHolder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                fileViewHolder.checkBox.setVisibility(View.VISIBLE);
            }
        } else {
            fileViewHolder.imageView.setImageResource(R.drawable.file_chooser_file);
            if (mFileProperties.selection_type == FileChooserProperties.DIR_SELECT) {
                fileViewHolder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                fileViewHolder.checkBox.setVisibility(View.VISIBLE);
            }
        }
        fileViewHolder.imageView.setContentDescription(item.getFileName());
        fileViewHolder.textViewName.setText(item.getFileName());
        SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat stime = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = new Date(item.getTime());
        if (position == 0 && item.getFileName().startsWith("...")) {
            fileViewHolder.textViewType.setText("Parent Directory");
        } else {
            fileViewHolder.textViewType.setText("Last edited: " + sdate.format(date) + ", " + stime.format(date));
        }
        if (fileViewHolder.checkBox.getVisibility() == View.VISIBLE) {
            if (position == 0 && item.getFileName().startsWith("...")) {
                fileViewHolder.checkBox.setVisibility(View.INVISIBLE);
            }
            if (FileMarkedList.hasItem(item.getLocation())) {
                fileViewHolder.checkBox.setChecked(true);
            } else {
                fileViewHolder.checkBox.setChecked(false);
            }
        }
        fileViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileProperties.selection_mode == FileChooserProperties.MULTI_MODE) {
                    item.setMarked(!item.isMarked());
                    if (item.isMarked()) {
                        FileMarkedList.addSelectedItem(item);
                    } else {
                        FileMarkedList.removeSelectedItem(item.getLocation());
                    }
                } else {
                    item.setMarked(!item.isMarked());
                    if (item.isMarked()) {
                        FileMarkedList.addSingleFile(item);
                    } else {
                        FileMarkedList.removeSelectedItem(item.getLocation());
                    }
                }
                notifyItemChecked.notifyCheckBoxIsClicked();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileChooserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.file_chooser_item,parent,false));
    }

    class FileChooserViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewName;
        public TextView textViewType;
        public CheckBox checkBox;

        public FileChooserViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.file_chooser_image_type);
            textViewName = (TextView) view.findViewById(R.id.file_chooser_file_name);
            textViewType = (TextView) view.findViewById(R.id.file_chooser_type);
            checkBox = (CheckBox) view.findViewById(R.id.file_chooser_mark);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    onSampleSelected(mData[getAdapterPosition()]);
                }
            });
        }
    }

    public interface NotifyItemChecked {
        void notifyCheckBoxIsClicked();
    }

    public void setNotifyItemCheckedListener(NotifyItemChecked notifyItemChecked) {
        this.notifyItemChecked = notifyItemChecked;
    }
}
