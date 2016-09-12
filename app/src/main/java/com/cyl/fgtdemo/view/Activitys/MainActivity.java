package com.cyl.fgtdemo.view.Activitys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyl.fgtdemo.R;
import com.cyl.fgtdemo.model.app.GlobalData;
import com.cyl.fgtdemo.view.Fragments.LoginDialogFragment;
import com.cyl.fgtdemo.view.Fragments.MainFragment;
import com.cyl.fgtdemo.view.Fragments.RecordFragment;
import com.cyl.fgtdemo.view.Fragments.SecondFragment;
import com.cyl.fgtdemo.view.Fragments.ThridFragment;
import com.cyl.fgtdemo.view.Fragments.UploadFragment;


import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.SelectPicIndexInterface {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int position=0;
    private Toolbar actionBarToolbar;
    private MainFragment mainFragment;
    private SecondFragment secondFragment;
    private ThridFragment thridFragment;
    private RecordFragment recordFragment;
    private UploadFragment repoFragment;
    private LoginDialogFragment dialog;

    private TextView textView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GlobalData.getInstance().SetContext(this);
        GlobalData.getInstance().CreateDir();
     //   GlobalData.getInstance().LoadRecordsList();
        GlobalData.getInstance().LoadConfig();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView=(NavigationView) findViewById(R.id.navigation_view);

        setupToolbar();
       if (null != navigationView) {
            setupDrawerContent(navigationView);
           View headerView = navigationView.getHeaderView(0);
           imageView=(ImageView)headerView.findViewById(R.id.id_image);
           textView=(TextView)headerView.findViewById(R.id.id_username);
           imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (!GlobalData.getInstance().isonline) {
                       openSignInBrowser();
                   } else {
                       Toast.makeText(MainActivity.this,"当前已登录",Toast.LENGTH_SHORT).show();
                   }
               }
           });
        }
        selectFragment(R.id.menu_dataupload,"远程开门");
    }
    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    private void openSignInBrowser() {
        if (null != dialog) {
            dialog.dismiss();
        }
        dialog = LoginDialogFragment.newInstance(MainActivity.this);
        dialog.show(getSupportFragmentManager(), "SignIn");
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
          //      if (GlobalData.getInstance().isonline){
                drawerLayout.closeDrawers();
                position=item.getItemId();
                selectFragment(item.getItemId(),item.getTitle().toString());
/*                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("请先登录，然后查看相关信息")
                            .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openSignInBrowser();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
               *//*     Dialog dialog = new Dialog(MainActivity.this, "温馨提示", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam");
				dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
                        openSignInBrowser();
					//	Toast.makeText(MainActivity.this, "Click accept button", Toast.LENGTH_SHORT).show();
					}
				});
				dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(MainActivity.this, "Click cancel button", Toast.LENGTH_SHORT).show();
					}
				});
				dialog.show();*//*
                //    Snackbar snackbar=new Snackbar()
               //     Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }*/
                return true;
            }
        });
    }

    @Override
    public void onSelectPicIndex() {
        textView.setText(GlobalData.getInstance().DefaultUser);
        imageView.setImageResource(R.drawable.plasmid);

    }
    private void selectFragment(final int fragmentId,final String item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (fragmentId != R.id.menu_about) {
            hideAllFragment(transaction);
        }
        switch (fragmentId) {
            case R.id.menu_dataupload:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    // todo diff with transaction.replace() ?
                    transaction.add(R.id.id_main_frame_container, mainFragment, "mian");
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case R.id.menu_repo:
                if (null == repoFragment) {
                    repoFragment = new UploadFragment();
                    transaction.add(R.id.id_main_frame_container, repoFragment, "dataupload");
                } else {
                    transaction.show(repoFragment);
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
                    transaction.add(R.id.id_main_frame_container, recordFragment, "netting");
                } else {
                    transaction.show(recordFragment);
                }
                break;
            case R.id.menu_about:
                startActivity(StackActivity.getStartIntent(this));
                break;
        }

        transaction.commit();
        if (fragmentId == R.id.menu_about){
        }else{
        setTitle(item);
        }
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

        if (null != repoFragment) {
            transaction.hide(repoFragment);
        }

        if (null != recordFragment) {
            transaction.hide(recordFragment);
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
           selectFragment(R.id.menu_dataupload,"远程开门");
        }else {
           GlobalData.getInstance().isonline=false;
           GlobalData.getInstance().recordInfoList.clear();
           GlobalData.getInstance().userInfoID.clear();
           GlobalData.getInstance().userInfoList.clear();
           super.onBackPressed();
        }

    }
}
