package com.cyl.fgtdemo.view.Activitys;

import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.view.Fragments.MainFragment;
import com.cyl.fgtdemo.view.Fragments.RecordFragment;
import com.cyl.fgtdemo.view.Fragments.SecondFragment;
import com.cyl.fgtdemo.view.Fragments.ThridFragment;
import com.cyl.fgtdemo.view.Fragments.UploadFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int position=0;
    private Toolbar actionBarToolbar;
    private MainFragment mainFragment;
    private SecondFragment secondFragment;
    private ThridFragment thridFragment;
    private RecordFragment recordFragment;
    private UploadFragment uploadFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView=(NavigationView) findViewById(R.id.navigation_view);
        setupToolbar();
       if (null != navigationView) {
            setupDrawerContent(navigationView);
        }
        selectFragment(R.id.menu_repo);
    }
    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.id_toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }
    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                position=item.getItemId();
                selectFragment(item.getItemId());
                return true;
            }
        });
    }
    private void selectFragment(final int fragmentId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (fragmentId != R.id.menu_about) {
            hideAllFragment(transaction);
        }
        switch (fragmentId) {
            case R.id.menu_repo:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    // todo diff with transaction.replace() ?
                    transaction.add(R.id.id_main_frame_container, mainFragment, "mian");
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case R.id.menu_users:
                if (null == secondFragment) {
                    secondFragment = new SecondFragment();
                    transaction.add(R.id.id_main_frame_container, secondFragment, "second");
                } else {
                    transaction.show(secondFragment);
                }
                break;
            case R.id.menu_trending:
                if (null == thridFragment) {
                    thridFragment = new ThridFragment();
                    transaction.add(R.id.id_main_frame_container, thridFragment, "thrid");
                } else {
                    transaction.show(thridFragment);
                }
                break;
            case R.id.menu_netting:
                if (null == recordFragment) {
                    recordFragment = new RecordFragment();
                    transaction.add(R.id.id_main_frame_container, recordFragment, "setting");
                } else {
                    transaction.show(recordFragment);
                }
                break;
            case R.id.menu_dataupload:
                if (null == uploadFragment) {
                    uploadFragment = new UploadFragment();
                    transaction.add(R.id.id_main_frame_container, uploadFragment, "setting");
                } else {
                    transaction.show(uploadFragment);
                }
                break;
            case R.id.menu_about:
                startActivity(AboutActivity.getStartIntent(this));
                break;
        }

        transaction.commit();
    }
    private void hideAllFragment(final FragmentTransaction transaction) {
        if (null != mainFragment) {
            transaction.hide(mainFragment);
        }

        if (null != secondFragment) {
            transaction.hide(secondFragment);
        }

        if (null != thridFragment) {
            transaction.hide(thridFragment);
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            getMenuInflater().inflate(R.menu.menu_navigation_view, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            switch (item.getItemId()) {
                case android.R.id.home:
                    openDrawer();
                    return true;
                case R.id.action_settings:
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

    protected void openDrawer() {
        if (drawerLayout == null)
            return;
        drawerLayout.openDrawer(GravityCompat.START);
    }
    /**
     * 返回键的处理
     *
     *
     */
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

       if (position != 0) {
           position=0;
           selectFragment(R.id.menu_repo);
        } else {
            super.onBackPressed();
        }

    }
}
