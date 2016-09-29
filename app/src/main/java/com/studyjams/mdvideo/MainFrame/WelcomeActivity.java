package com.studyjams.mdvideo.MainFrame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
