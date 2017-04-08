package com.lee.myweibo.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.lee.myweibo.fragments.MyFragment;
import com.lee.myweibo.utils.SharePreferenceUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2017/2/23.
 */

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawlayout)
    DrawerLayout drawlay;
    TextView tvUserName;
    ImageView ivUserIcon;
    Fragment lastFragment;
    FragmentManager manager;
    Fragment f1;
    Fragment f2;
    Fragment f3;
    Fragment f4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        setupnavigationView();
        loadUserInfo();
        setupFragment();
        manager=getSupportFragmentManager();
        showFragment(f1);
    }

    private void setupFragment() {
        f1= MyFragment.newInstance(1);
        f2= MyFragment.newInstance(2);
        f3= MyFragment.newInstance(3);
        f4= MyFragment.newInstance(4);
    }
    private void loadUserInfo(){
        Oauth2AccessToken token= SharePreferenceUtil.readToken(this);
        UsersAPI usersAPI=new UsersAPI(this, Constants.APP_KEY,token);
        usersAPI.show(Long.valueOf(token.getUid()), new RequestListener() {
            @Override
            public void onComplete(String s) {
                User userinfo=User.parse(s);
                tvUserName.setText(userinfo.screen_name);
                Picasso.with(MainActivity.this).load(userinfo.profile_image_url).into(ivUserIcon);
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
    }

    private void setupnavigationView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle tg=new ActionBarDrawerToggle(this,drawlay,toolbar,R.string.open,R.string.guang);
        tg.syncState();
        drawlay.addDrawerListener(tg);
        View view=navigationView.getHeaderView(0);
        ivUserIcon= (ImageView) view.findViewById(R.id.iv_userIcon);
        tvUserName= (TextView) view.findViewById(R.id.tv_userName);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.wb_main:
                        showFragment(f1);
                        break;
                    case R.id.wb_hot:
                        showFragment(f2);
                        break;
                    case R.id.wb_save:
                        showFragment(f3);
                        break;
                    case R.id.wb_change:
                        showFragment(f4);
                        break;
                }
                item.setChecked(true);
                drawlay.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    private void showFragment(Fragment fragment){
        List<Fragment> fragments=manager.getFragments();
        FragmentTransaction transaction=manager.beginTransaction();
        if (fragments != null) {
            transaction.hide(lastFragment);
        }
        if(fragments==null||!fragments.contains(fragment)){
            transaction.add(R.id.fragment_container,fragment);
        }else {
            transaction.show(fragment);
        }
        lastFragment=fragment;
        transaction.commit();

    }

}
