package com.studyjams.mdvideo.MainFrame;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.exoplayer2.C;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.Data.source.remote.SyncService;
import com.studyjams.mdvideo.PlayerModule.ExoPlayerV2.PlayerActivityV2;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.Setting.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;
    public static final int EXTERNAL_READ_PERMISSION_GRANT = 112;
    private List<String> mData;
    private MainPagerAdapter mainPagerAdapter;

    public static final String PLAY_HISTORY_ACTION = "com.studyjams.mdvideo.HISTORY";
    private MyReceiver mMyReceiver;
    private IntentFilter mIntentFilter;

    private DrawerLayout drawer;

    //定义进程内广播管理，比全局广播更高效
    private LocalBroadcastManager mLocalBroadcastManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();

    }

    private void initData(){
        mData = new ArrayList<>();
        mData.add(getResources().getString(R.string.menu_video_local));
        mData.add(getResources().getString(R.string.menu_video_history));
//        mData.add(getResources().getString(R.string.menu_video_subtitle));


        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mMyReceiver = new MyReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(PLAY_HISTORY_ACTION);
        mLocalBroadcastManager.registerReceiver(mMyReceiver,mIntentFilter);
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.main_view_table);
        ViewPager mViewpager = (ViewPager)findViewById(R.id.main_view_pager);
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),mData);

        mViewpager.setAdapter(mainPagerAdapter);
        tabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(PLAY_HISTORY_ACTION)) {

                SyncService.startActionUpdate(MainActivity.this,
                        intent.getStringExtra(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ENTRY_ID),
                        intent.getStringExtra(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PLAY_DURATION),
                        intent.getStringExtra(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_CREATED_DATE));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mMyReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menu_video_share:
                shareWithFriends();
                break;
            case R.id.menu_video_send:

                sendEmail();
                break;
            case R.id.menu_video_setting:
                openSettingView();
                break;
            default:

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode){
                case REQUEST_CODE:
                    Intent intent = new Intent(this, PlayerActivityV2.class);
                    intent.setData(data.getData());
                    intent.setAction(PlayerActivityV2.ACTION_VIEW);
                    intent.putExtra(PlayerActivityV2.CONTENT_ID_EXTRA, REQUEST_CODE);
                    intent.putExtra(PlayerActivityV2.CONTENT_TYPE_EXTRA, C.TYPE_OTHER);
                    intent.putExtra(PlayerActivityV2.CONTENT_POSITION_EXTRA, 0);
                    startActivity(intent);
                    break;
                default:break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Share with friends
     **/
    private void shareWithFriends() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_share_url));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.send_share_title)));
    }

    /**
     * Feedback to the developers
     */
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(getString(R.string.send_share_email)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * open setting menu
     **/
    private void openSettingView() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
