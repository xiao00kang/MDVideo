package com.studyjams.mdvideo.PlayerModule.MediaController;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.studyjams.mdvideo.PlayerModule.EventBusMessage.ControllerMessage;
import com.studyjams.mdvideo.R;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by syamiadmin on 2016/8/3.
 */
public class ExtractorMediaController extends AbstractMediaController implements View.OnClickListener{
    private static final String TAG = "MediaController";

    /**播放控制，从播放器传入**/
    private MediaPlayerControl mPlayer;
    private final Context mContext;

    /**从外部传入的视图，可能是播放页面的父布局，也可能只是播放器部分的布局**/
    private View mAnchor;

    /**底部控制的悬浮窗 播放、暂停等**/
    private PopupWindow mBottomControlView;

    /**顶部交互的悬浮窗 返回等**/
    private PopupWindow mTopControlView;

    /**进度条**/
    private SeekBar mProgress;

    /**已播放时长，**/
    private TextView mEndTime, mCurrentTime;

    /**Controller的显示状态**/
    private boolean mShowing;
    /**seekBar是否在拖动状态的标志**/
    private boolean mDragging;

    /**播放状态**/
    private int lastStatus = -1;

    /**Controller的默认显示时长**/
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private ImageView mPauseButton;
    private TextView mVideoTitle;

    /**辅助功能管理**/
    private final AccessibilityManager mAccessibilityManager;

    /**获取系统音频管理**/
//    private AudioManager mAudioManager;

    /**实例化一个Handler用于进度条更新**/
    private final Handler mHandler = new MyHandler(this);

    public ExtractorMediaController(Context context) {
        mContext = context;
        initFloatingWindow();
        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
//        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    /**通过弱引用来使用Handler**/
    private static class MyHandler extends Handler {
        private WeakReference<ExtractorMediaController> controllerWeakReference;
        public MyHandler(ExtractorMediaController controller) {
            controllerWeakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            ExtractorMediaController controller = controllerWeakReference.get();
            if (controller != null) {
                controller.handleMessage(msg);
            }
        }
    }

    /**处理Message**/
    private void handleMessage(Message msg) {
        int pos;
        switch (msg.what) {
            case FADE_OUT:
                hide();
                break;
            case SHOW_PROGRESS:
                pos = setProgress();
                if (!mDragging && mShowing && mPlayer.isPlaying()) {
                    msg = mHandler.obtainMessage(SHOW_PROGRESS);
                    mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                }
                break;
            default:break;
        }
    }

    /**初始化悬浮窗**/
    private void initFloatingWindow() {
        /**底部悬浮窗**/
        mBottomControlView = new PopupWindow(mContext);
        mBottomControlView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mBottomControlView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置PopupWindow要显示的内容
        mBottomControlView.setContentView(makeBottomControllerView());
        mBottomControlView.setBackgroundDrawable(null);
        //设置显示和消失的动画
        mBottomControlView.setAnimationStyle(R.style.BottomPopupAnimation);

        /**顶部悬浮窗**/
        mTopControlView = new PopupWindow(mContext);
        mTopControlView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mTopControlView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置PopupWindow要显示的内容
        mTopControlView.setContentView(makeTopControllerView());
        mTopControlView.setBackgroundDrawable(null);
        //设置显示和消失的动画
        mTopControlView.setAnimationStyle(R.style.TopPopupAnimation);
    }

    @Override
    public void setTitle(String videoName) {
        mVideoTitle.setText(videoName);
    }

    /**播放控制器 初始化时添加**/
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * When VideoView calls this method, it will use the VideoView's parent
     * as the anchor.
     * 将播放界面显示的view传进来，用于事件监听、布局变化监听、确定弹窗位置等（个人是这个理解的）
     * @param view The view to which to anchor the controller when it is visible.
     */
    @Override
    public void setAnchorView(View view) {
        mAnchor = view;
        if (mAnchor == null) {
            throw new NullPointerException("the anchor view can not be null");
        }
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     * 生成控制的界面
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    private View makeBottomControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mBottomControl = inflate.inflate(R.layout.player_media_controller_bottom, null);
        mPauseButton = (ImageView)mBottomControl.findViewById(R.id.player_play);
        mPauseButton.setOnClickListener(this);

        mProgress = (SeekBar)mBottomControl.findViewById(R.id.player_progress);
        mProgress.setMax(1000);
        mProgress.setOnSeekBarChangeListener(mSeekListener);

        mEndTime = (TextView) mBottomControl.findViewById(R.id.player_time);
        mCurrentTime = (TextView) mBottomControl.findViewById(R.id.player_time_current);

        return mBottomControl;
    }

    private View makeTopControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mTopControl = inflate.inflate(R.layout.player_media_controller_top, null);
        ImageView backView = (ImageView)mTopControl.findViewById(R.id.player_exit);
        ImageView subtitleView  = (ImageView)mTopControl.findViewById(R.id.player_subtitle);
        ImageView moreView  = (ImageView)mTopControl.findViewById(R.id.player_more);
        mVideoTitle = (TextView)mTopControl.findViewById(R.id.player_title);
        backView.setOnClickListener(this);
        subtitleView.setOnClickListener(this);
        moreView.setOnClickListener(this);
        return mTopControl;
    }

    /**统一处理点击事件**/
    @Override
    public void onClick(View v) {
        int pos;
        switch (v.getId()){
            case R.id.player_exit:
                //退出
                if (isShowing()) {
                   dismissPopupWindow();
                }

                ((Activity)mContext).finish();
                break;
            case R.id.player_subtitle:
                //字幕加载
                EventBus.getDefault().post(new ControllerMessage(ControllerMessage.SUBTITLE));
                Log.d(TAG, "onClick: subtitle");
                break;
            case R.id.player_more:
                //菜单
                EventBus.getDefault().post(new ControllerMessage(ControllerMessage.MENU));
                Log.d(TAG, "onClick: more");
                break;

            case R.id.player_play:
                //暂停/播放
                doPauseResume();
                show(sDefaultTimeout);
                break;
            default:break;
        }
    }

    /**隐藏悬浮窗**/
    private void dismissPopupWindow(){
        mBottomControlView.dismiss();
        mTopControlView.dismiss();
    }

    /**显示悬浮窗**/
    private void showPopupWindow(){
        mBottomControlView.showAtLocation(mAnchor,Gravity.BOTTOM,0,0);
        mTopControlView.showAtLocation(mAnchor,Gravity.TOP,0,0);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     * 将暂停按钮与进度条设置为不可用，用于适配在线直播的控制
     */
    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && !mPlayer.canPause()) {
                mPauseButton.setEnabled(false);
            }

            if (mProgress != null && !mPlayer.canSeekBackward() && !mPlayer.canSeekForward()) {
                mProgress.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     * 显示悬浮控制窗
     */
    @Override
    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            showPopupWindow();
            mShowing = true;
        }
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0 && !mAccessibilityManager.isTouchExplorationEnabled()) {
            mHandler.removeMessages(FADE_OUT);
            Message msg = mHandler.obtainMessage(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    //返回悬浮窗显示状态
    @Override
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     * 隐藏悬浮窗
     */
    @Override
    public void hide() {
        if (mAnchor == null)
            return;

        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                dismissPopupWindow();
            } catch (IllegalArgumentException ex) {
                Log.w("MediaController", "already removed");
            }
            mShowing = false;
        }
    }

    //格式化时间为分钟、秒钟显示
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**更新播放进度与时间显示**/
    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();//已播放时长
        int duration = mPlayer.getDuration();//总时长
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress( (int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    /**更新暂停、播放按键的状态**/
    private void updatePausePlay() {

        if (lastStatus != (mPlayer.isPlaying() ? 0 : 1)) {
            lastStatus = mPlayer.isPlaying() ? 0 : 1;
            mPauseButton.setImageResource(!mPlayer.isPlaying() ? R.drawable.ic_pause_to_play : R.drawable.ic_play_to_pause);
            ((AnimatedVectorDrawable) mPauseButton.getDrawable()).start();
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
//            } else {
//                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//            }

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mPlayer.seekTo( (int) newPosition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime( (int) newPosition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
//            } else {
//                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//            }
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }

        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
    }
}
