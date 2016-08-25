/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.studyjams.mdvideo.PlayerModule.Renderer.Extractor;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.SingleSampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.text.tx3g.Tx3gParser;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.MimeTypes;
import com.studyjams.mdvideo.PlayerModule.ExoPlayer.DemoPlayer;
import com.studyjams.mdvideo.PlayerModule.ExoPlayer.DemoPlayer.RendererBuilder;

/**
 * A {@link RendererBuilder} for streams that can be read using an {@link Extractor}.
 *
 * 本地视频流的渲染器 支持如下格式：
 * FMP4, MP4, M4A, MKV, WebM, MP3, AAC, MPEG-TS, MPEG-PS, OGG, FLV and WAV.
 */
public class ExtractorRendererBuilder implements RendererBuilder {

  private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
  private static final int BUFFER_SEGMENT_COUNT = 256;

  private final Context context;
  private final String userAgent;
  private final Uri uri;

  public ExtractorRendererBuilder(Context context, String userAgent, Uri uri) {
    this.context = context;
    this.userAgent = userAgent;
    this.uri = uri;
  }

  @Override
  public void buildRenderers(DemoPlayer player) {
    Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
    Handler mainHandler = player.getMainHandler();

    // Build the video and audio renderers.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);

    DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);

    //从uri中获取视频源
    ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
        BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE, mainHandler, player, 0);

    //video轨道
    MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
        sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000,
        mainHandler, player, 50);
    //Audio轨道
    MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
        MediaCodecSelector.DEFAULT, null, true, mainHandler, player,
        AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);


    //实验一下
//    Uri textUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.test);

    Uri textUri = Uri.parse("file:///storage/emulated/0/Download/test.srt");
//
    MediaFormat mediaFormat = MediaFormat.createTextFormat(String.valueOf(MediaFormat.NO_VALUE), MimeTypes.APPLICATION_SUBRIP,
            MediaFormat.NO_VALUE, C.MATCH_LONGEST_US, null);
    DataSource textDataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
    SingleSampleSource textSampleSource = new SingleSampleSource(textUri, textDataSource, mediaFormat);

    TrackRenderer textRenderer = new TextTrackRenderer(textSampleSource, player, mainHandler.getLooper(),new Tx3gParser());

    //文本轨道渲染
//    TrackRenderer textRenderer = new TextTrackRenderer(sampleSource, player, mainHandler.getLooper());

    // Invoke the callback.
    TrackRenderer[] renderers = new TrackRenderer[DemoPlayer.RENDERER_COUNT];
    renderers[DemoPlayer.TYPE_VIDEO] = videoRenderer;
    renderers[DemoPlayer.TYPE_AUDIO] = audioRenderer;
    renderers[DemoPlayer.TYPE_TEXT] = textRenderer;
    player.onRenderers(renderers, bandwidthMeter);
  }

  @Override
  public void cancel() {
    // Do nothing.
  }
}
