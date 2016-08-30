package com.studyjams.mdvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.studyjams.mdvideo.DatabaseHelper.SyncService;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SyncService.startActionCheck(this);
        SyncService.startActionTraversal(this);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
