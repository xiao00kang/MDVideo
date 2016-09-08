package com.studyjams.mdvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.studyjams.mdvideo.Data.source.remote.SyncService;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SyncService.startActionCheck(this);
        SyncService.startActionTraversal(this);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
