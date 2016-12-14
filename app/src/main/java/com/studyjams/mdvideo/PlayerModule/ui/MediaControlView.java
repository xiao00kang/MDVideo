/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.studyjams.mdvideo.PlayerModule.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Util;
import com.studyjams.mdvideo.PlayerModule.EventBusMessage.ControllerMessage;
import com.studyjams.mdvideo.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Formatter;
import java.util.Locale;

/**
 * A view to control video playback of an {@link ExoPlayer}.
 */
public class MediaControlView extends FrameLayout implements View.OnTouchListener{

  private static final String TAG = "MediaControlView";

  /**
   * Listener to be notified about changes of the visibility of the UI control.
   */
  public interface VisibilityListener {
    /**
     * Called when the visibility changes.
     *
     * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
     */
    void onVisibilityChange(int visibility);
  }

  public static final int DEFAULT_FAST_FORWARD_MS = 15000;
  public static final int DEFAULT_REWIND_MS = 5000;
  public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;

  private static final int PROGRESS_BAR_MAX = 1000;
  private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

  private final ComponentListener componentListener;
  private final View previousButton;
  private final View nextButton;
  private final ImageButton playButton;
  private final TextView time;
  private final TextView timeCurrent;
  private final SeekBar progressBar;
  private final View fastForwardButton;
  private final View rewindButton;
  private final StringBuilder formatBuilder;
  private final Formatter formatter;
  private final Timeline.Window currentWindow;

  private final View topBar;
  private final View bottomBar;

  private final View backButton;
  private final TextView titleView;
  private final View subtitleView;
  private final View menuView;

  private final AnimatedVectorDrawableCompat compatPauseToPlay;
  private final AnimatedVectorDrawableCompat compatPlayToPause;

  //手势检测器
  private GestureDetector mDetector;


  private ExoPlayer player;
  private VisibilityListener visibilityListener;

  private boolean isAttachedToWindow;
  private boolean dragging;
  private int rewindMs;
  private int fastForwardMs;
  private int showTimeoutMs;
  private long hideAtMs;

  private final Runnable updateProgressAction = new Runnable() {
    @Override
    public void run() {
      updateProgress();
    }
  };

  private final Runnable hideAction = new Runnable() {
    @Override
    public void run() {
      hide();
    }
  };

  public MediaControlView(Context context) {
    this(context, null);
  }

  public MediaControlView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MediaControlView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    rewindMs = DEFAULT_REWIND_MS;
    fastForwardMs = DEFAULT_FAST_FORWARD_MS;
    showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MediaControlView, 0, 0);
      try {
        rewindMs = a.getInt(R.styleable.MediaControlView_rewind_increment, rewindMs);
        fastForwardMs = a.getInt(R.styleable.MediaControlView_fastforward_increment, fastForwardMs);
        showTimeoutMs = a.getInt(R.styleable.MediaControlView_show_timeout, showTimeoutMs);
      } finally {
        a.recycle();
      }
    }

    currentWindow = new Timeline.Window();
    formatBuilder = new StringBuilder();
    formatter = new Formatter(formatBuilder, Locale.getDefault());
    componentListener = new ComponentListener();

    LayoutInflater.from(context).inflate(R.layout.media_control_view, this);
    time = (TextView) findViewById(R.id.player_time);
    timeCurrent = (TextView) findViewById(R.id.player_time_current);
    progressBar = (SeekBar) findViewById(R.id.player_progress);
    progressBar.setOnSeekBarChangeListener(componentListener);
    progressBar.setMax(PROGRESS_BAR_MAX);
    playButton = (ImageButton) findViewById(R.id.player_play);
    playButton.setOnClickListener(componentListener);
    previousButton = findViewById(R.id.player_prev);
    previousButton.setOnClickListener(componentListener);
    nextButton = findViewById(R.id.player_next);
    nextButton.setOnClickListener(componentListener);
    rewindButton = findViewById(R.id.player_rew);
    rewindButton.setOnClickListener(componentListener);
    fastForwardButton = findViewById(R.id.player_ffwd);
    fastForwardButton.setOnClickListener(componentListener);

    topBar = findViewById(R.id.control_top);
    bottomBar = findViewById(R.id.control_bottom);
    mDetector = new GestureDetector(context, mGestureListener);
    this.setOnTouchListener(this);
    this.setLongClickable(true);

    backButton = findViewById(R.id.player_exit);
    backButton.setOnClickListener(componentListener);
    titleView = (TextView)findViewById(R.id.player_title);
    subtitleView = findViewById(R.id.player_subtitle);
    subtitleView.setOnClickListener(componentListener);
    menuView = findViewById(R.id.player_more);
    menuView.setOnClickListener(componentListener);

    compatPauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_pause_to_play);
    compatPlayToPause = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_play_to_pause);
  }

  /**
   * Returns the player currently being controlled by this view, or null if no player is set.
   */
  public ExoPlayer getPlayer() {
    return player;
  }

  /**
   * Sets the {@link ExoPlayer} to control.
   *
   * @param player the {@code ExoPlayer} to control.
   */
  public void setPlayer(ExoPlayer player) {
    if (this.player == player) {
      return;
    }
    if (this.player != null) {
      this.player.removeListener(componentListener);
    }
    this.player = player;
    if (player != null) {
      player.addListener(componentListener);
    }
    updateAll();
  }

  /**
   * Sets the {@link VisibilityListener}.
   *
   * @param listener The listener to be notified about visibility changes.
   */
  public void setVisibilityListener(VisibilityListener listener) {
    this.visibilityListener = listener;
  }

  /**
   * Sets the rewind increment in milliseconds.
   *
   * @param rewindMs The rewind increment in milliseconds.
   */
  public void setRewindIncrementMs(int rewindMs) {
    this.rewindMs = rewindMs;
    updateNavigation();
  }

  /**
   * Sets the fast forward increment in milliseconds.
   *
   * @param fastForwardMs The fast forward increment in milliseconds.
   */
  public void setFastForwardIncrementMs(int fastForwardMs) {
    this.fastForwardMs = fastForwardMs;
    updateNavigation();
  }

  /**
   * Returns the playback controls timeout. The playback controls are automatically hidden after
   * this duration of time has elapsed without user input.
   *
   * @return The duration in milliseconds. A non-positive value indicates that the controls will
   *     remain visible indefinitely.
   */
  public int getShowTimeoutMs() {
    return showTimeoutMs;
  }

  /**
   * Sets the playback controls timeout. The playback controls are automatically hidden after this
   * duration of time has elapsed without user input.
   *
   * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
   *     to remain visible indefinitely.
   */
  public void setShowTimeoutMs(int showTimeoutMs) {
    this.showTimeoutMs = showTimeoutMs;
  }

  /**
   * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
   * be automatically hidden after this duration of time has elapsed without user input.
   */
//  public void show() {
//    if (!isVisible()) {
//      setVisibility(VISIBLE);
//      if (visibilityListener != null) {
//        visibilityListener.onVisibilityChange(getVisibility());
//      }
//      updateAll();
//    }
//    // Call hideAfterTimeout even if already visible to reset the timeout.
//    hideAfterTimeout();
//  }


  public void show(){

    if (topBar.getVisibility() == INVISIBLE) {

      topBar.setVisibility(VISIBLE);
      bottomBar.setVisibility(VISIBLE);

      if (visibilityListener != null) {
        visibilityListener.onVisibilityChange(topBar.getVisibility());
      }

      Animation animTopIn = AnimationUtils.loadAnimation(getContext(), R.anim.popup_top_in);
      animTopIn.setFillAfter(true);
      Animation animBottomIn = AnimationUtils.loadAnimation(getContext(), R.anim.popup_bottom_in);
      animBottomIn.setFillAfter(true);
      topBar.setAnimation(animTopIn);
      bottomBar.setAnimation(animBottomIn);
      animTopIn.start();
      animBottomIn.start();

      updateAll();
    }

    // Call hideAfterTimeout even if already visible to reset the timeout.
    hideAfterTimeout();
  }

  public void hide() {
    if (topBar.getVisibility() == VISIBLE) {

      Animation animTopOut = AnimationUtils.loadAnimation(getContext(), R.anim.popup_top_out);
      Animation animBottomOut = AnimationUtils.loadAnimation(getContext(), R.anim.popup_bottom_out);
//      animTopOut.setFillAfter(true);
//      animBottomOut.setFillAfter(true);
      topBar.setAnimation(animTopOut);
      bottomBar.setAnimation(animBottomOut);
      animTopOut.start();
      animBottomOut.start();

      topBar.setVisibility(INVISIBLE);
      bottomBar.setVisibility(INVISIBLE);
      if (visibilityListener != null) {
        visibilityListener.onVisibilityChange(topBar.getVisibility());
      }

      removeCallbacks(updateProgressAction);
      removeCallbacks(hideAction);
      hideAtMs = C.TIME_UNSET;
    }
  }

  /**
   * 设置标题
   * @param title
     */
  public void setTitle(String title){
     titleView.setText(title);
  }

  /**
   * Hides the controller.
   */
//  public void hide() {
//    if (isVisible()) {
//      setVisibility(GONE);
//      if (visibilityListener != null) {
//        visibilityListener.onVisibilityChange(getVisibility());
//      }
//      removeCallbacks(updateProgressAction);
//      removeCallbacks(hideAction);
//      hideAtMs = C.TIME_UNSET;
//    }
//  }

  /**
   * Returns whether the controller is currently visible.
   */
  public boolean isVisible() {
//    return getVisibility() == VISIBLE;

    return topBar.getVisibility() == VISIBLE;
  }

  private void hideAfterTimeout() {
    removeCallbacks(hideAction);
    if (showTimeoutMs > 0) {
      hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
      if (isAttachedToWindow) {
        postDelayed(hideAction, showTimeoutMs);
      }
    } else {
      hideAtMs = C.TIME_UNSET;
    }
  }

  private void updateAll() {
    updatePlayPauseButton();
    updateNavigation();
    updateProgress();
  }

  private void updatePlayPauseButton() {
    if (!isVisible() || !isAttachedToWindow) {
      return;
    }
    boolean playing = player != null && player.getPlayWhenReady();
    String contentDescription = getResources().getString(
            playing ? R.string.exo_controls_pause_description : R.string.exo_controls_play_description);
    playButton.setContentDescription(contentDescription);
//    playButton.setImageResource(playing ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play);
//    playButton.setImageResource(!playing ? R.drawable.ic_pause_to_play : R.drawable.ic_play_to_pause);
//    AnimationDrawable animation = (AnimationDrawable)playButton.getDrawable();
//    animation.start();

//    ((AnimatedVectorDrawable) playButton.getDrawable()).start();

    if(playing){
      playButton.setImageDrawable(compatPauseToPlay.getCurrent());
      compatPauseToPlay.start();
    }else{
      playButton.setImageDrawable(compatPlayToPause.getCurrent());
      compatPlayToPause.start();
    }
  }

  private void updateNavigation() {
    if (!isVisible() || !isAttachedToWindow) {
      return;
    }
    Timeline currentTimeline = player != null ? player.getCurrentTimeline() : null;
    boolean haveTimeline = currentTimeline != null;
    boolean isSeekable = false;
    boolean enablePrevious = false;
    boolean enableNext = false;
    if (haveTimeline) {
      int currentWindowIndex = player.getCurrentWindowIndex();
      currentTimeline.getWindow(currentWindowIndex, currentWindow);
      isSeekable = currentWindow.isSeekable;
      enablePrevious = currentWindowIndex > 0 || isSeekable || !currentWindow.isDynamic;
      enableNext = (currentWindowIndex < currentTimeline.getWindowCount() - 1)
          || currentWindow.isDynamic;
    }
    setButtonEnabled(enablePrevious , previousButton);
    setButtonEnabled(enableNext, nextButton);
    setButtonEnabled(fastForwardMs > 0 && isSeekable, fastForwardButton);
    setButtonEnabled(rewindMs > 0 && isSeekable, rewindButton);
    progressBar.setEnabled(isSeekable);
  }

  private void updateProgress() {
    if (!isVisible() || !isAttachedToWindow) {
      return;
    }
    long duration = player == null ? 0 : player.getDuration();
    long position = player == null ? 0 : player.getCurrentPosition();
    time.setText(stringForTime(duration));
    if (!dragging) {
      timeCurrent.setText(stringForTime(position));
    }
    if (!dragging) {
      progressBar.setProgress(progressBarValue(position));
    }
    long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
    progressBar.setSecondaryProgress(progressBarValue(bufferedPosition));
    // Remove scheduled updates.
    removeCallbacks(updateProgressAction);
    // Schedule an update if necessary.
    int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
    if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
      long delayMs;
      if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
        delayMs = 1000 - (position % 1000);
        if (delayMs < 200) {
          delayMs += 1000;
        }
      } else {
        delayMs = 1000;
      }
      postDelayed(updateProgressAction, delayMs);
    }
  }

  private void setButtonEnabled(boolean enabled, View view) {
    view.setEnabled(enabled);
    if (Util.SDK_INT >= 11) {
      setViewAlphaV11(view, enabled ? 1f : 0.3f);
      view.setVisibility(VISIBLE);
    } else {
      view.setVisibility(enabled ? VISIBLE : INVISIBLE);
    }
  }

  @TargetApi(11)
  private void setViewAlphaV11(View view, float alpha) {
    view.setAlpha(alpha);
  }

  private String stringForTime(long timeMs) {
    if (timeMs == C.TIME_UNSET) {
      timeMs = 0;
    }
    long totalSeconds = (timeMs + 500) / 1000;
    long seconds = totalSeconds % 60;
    long minutes = (totalSeconds / 60) % 60;
    long hours = totalSeconds / 3600;
    formatBuilder.setLength(0);
    return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        : formatter.format("%02d:%02d", minutes, seconds).toString();
  }

  private int progressBarValue(long position) {
    long duration = player == null ? C.TIME_UNSET : player.getDuration();
    return duration == C.TIME_UNSET || duration == 0 ? 0
        : (int) ((position * PROGRESS_BAR_MAX) / duration);
  }

  private long positionValue(int progress) {
    long duration = player == null ? C.TIME_UNSET : player.getDuration();
    return duration == C.TIME_UNSET ? 0 : ((duration * progress) / PROGRESS_BAR_MAX);
  }

  private void previous() {
    Timeline currentTimeline = player.getCurrentTimeline();
    if (currentTimeline == null) {
      return;
    }
    int currentWindowIndex = player.getCurrentWindowIndex();
    currentTimeline.getWindow(currentWindowIndex, currentWindow);
    if (currentWindowIndex > 0 && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
        || (currentWindow.isDynamic && !currentWindow.isSeekable))) {
      player.seekToDefaultPosition(currentWindowIndex - 1);
    } else {
      player.seekTo(0);
    }
  }

  private void next() {
    Timeline currentTimeline = player.getCurrentTimeline();
    if (currentTimeline == null) {
      return;
    }
    int currentWindowIndex = player.getCurrentWindowIndex();
    if (currentWindowIndex < currentTimeline.getWindowCount() - 1) {
      player.seekToDefaultPosition(currentWindowIndex + 1);
    } else if (currentTimeline.getWindow(currentWindowIndex, currentWindow, false).isDynamic) {
      player.seekToDefaultPosition();
    }
  }

  private void rewind() {
    if (rewindMs <= 0) {
      return;
    }
    player.seekTo(Math.max(player.getCurrentPosition() - rewindMs, 0));
  }

  private void fastForward() {
    if (fastForwardMs <= 0) {
      return;
    }
    player.seekTo(Math.min(player.getCurrentPosition() + fastForwardMs, player.getDuration()));
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    isAttachedToWindow = true;
    if (hideAtMs != C.TIME_UNSET) {
      long delayMs = hideAtMs - SystemClock.uptimeMillis();
      if (delayMs <= 0) {
        hide();
      } else {
        postDelayed(hideAction, delayMs);
      }
    }
    updateAll();
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    isAttachedToWindow = false;
    removeCallbacks(updateProgressAction);
    removeCallbacks(hideAction);
  }

//  @Override
//  public boolean onInterceptTouchEvent(MotionEvent ev) {
//    //拦截事件
//    return true;
//  }

  /*package*/ GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {

    @Override
    public boolean onDown(MotionEvent motionEvent) {
      if (topBar.getVisibility() == VISIBLE) {
        hide();
      } else {
        show();
      }

      return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
      return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
      return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {

      float beginX = motionEvent.getX();
      float endX = motionEvent1.getX();
      float beginY = motionEvent.getY();
      float endY = motionEvent1.getY();

      if (Math.abs(velocityX) > Math.abs(velocityY)) {
        if (beginX > endX) {
          Log.d(TAG, "onFling: 左滑");
        } else {
          Log.d(TAG, "onFling: 右滑");
        }
      } else if (Math.abs(velocityY) > Math.abs(velocityX)) {
        if (beginY > endY) {
          Log.d(TAG, "onFling: 上滑");
        } else {
          Log.d(TAG, "onFling: 下滑");
        }
      }
      return false;
    }

  };

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    //消除冲突
//    if (mDetector.onTouchEvent(event)) {
//      event.setAction(MotionEvent.ACTION_CANCEL);
//    }
    return super.dispatchTouchEvent(event);
  }

  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {

    return mDetector.onTouchEvent(motionEvent);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    return super.onTouchEvent(event);
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (player == null || event.getAction() != KeyEvent.ACTION_DOWN) {
      return super.dispatchKeyEvent(event);
    }

    switch (event.getKeyCode()) {
      case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
      case KeyEvent.KEYCODE_DPAD_RIGHT:
        fastForward();
        break;
      case KeyEvent.KEYCODE_MEDIA_REWIND:
      case KeyEvent.KEYCODE_DPAD_LEFT:
        rewind();
        break;
      case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
        player.setPlayWhenReady(!player.getPlayWhenReady());
        break;
      case KeyEvent.KEYCODE_MEDIA_PLAY:
        player.setPlayWhenReady(true);
        break;
      case KeyEvent.KEYCODE_MEDIA_PAUSE:
        player.setPlayWhenReady(false);
        break;
      case KeyEvent.KEYCODE_MEDIA_NEXT:
        next();
        break;
      case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
        previous();
        break;
      default:
        return false;
    }
//    show();
    return true;
  }

  private final class ComponentListener implements ExoPlayer.EventListener,
      SeekBar.OnSeekBarChangeListener, OnClickListener {

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      removeCallbacks(hideAction);
      dragging = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      if (fromUser) {
        timeCurrent.setText(stringForTime(positionValue(progress)));
      }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      dragging = false;
      player.seekTo(positionValue(seekBar.getProgress()));
      hideAfterTimeout();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
      updatePlayPauseButton();
      updateProgress();
    }

    @Override
    public void onPositionDiscontinuity() {
      updateNavigation();
      updateProgress();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
      updateNavigation();
      updateProgress();
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
      // Do nothing.
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
      // Do nothing.
    }

    @Override
    public void onClick(View view) {
      Timeline currentTimeline = player.getCurrentTimeline();
      if (nextButton == view) {
        next();
      } else if (previousButton == view) {
        previous();
      } else if (fastForwardButton == view) {
        fastForward();
      } else if (rewindButton == view && currentTimeline != null) {
        rewind();
      } else if (playButton == view) {
        player.setPlayWhenReady(!player.getPlayWhenReady());
      }else if(backButton == view){
        EventBus.getDefault().post(new ControllerMessage(ControllerMessage.EXIT));
        Log.d(TAG, "onClick: exit");
      }else if(subtitleView == view){
        //字幕加载
        EventBus.getDefault().post(new ControllerMessage(ControllerMessage.SUBTITLE));
        Log.d(TAG, "onClick: subtitle");
      }else if(menuView == view){
        //菜单
        EventBus.getDefault().post(new ControllerMessage(ControllerMessage.MENU));
        Log.d(TAG, "onClick: more");
      }


      hideAfterTimeout();
    }

  }

}
