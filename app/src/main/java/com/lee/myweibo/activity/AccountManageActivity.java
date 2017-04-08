package com.lee.myweibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.lee.myweibo.adapter.ListViewAdapter;
import com.lee.myweibo.db.DbHelper;
import com.lee.myweibo.utils.SharePreferenceUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2017/2/22.
 */

public class AccountManageActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lv_account)
    ListView listView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Oauth2AccessToken Token;
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    private DbHelper helper;
    private ListViewAdapter adapter;
    private List<Oauth2AccessToken> oauth2AccessTokenList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        ButterKnife.bind(this);
        helper=new DbHelper(this,"auth.db",null,1);
        setupAuth();
        setupToolBar();
        setupFAB();
        setupListView();
//        refreshPage();
    }

    private void setupListView() {
        registerForContextMenu(listView);
        adapter=new ListViewAdapter(this);
        oauth2AccessTokenList=helper.getTokenList();
        adapter.setData(oauth2AccessTokenList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyItemClickListener());
    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("账号管理");
    }

    private void refreshPage(Oauth2AccessToken oauth2AccessToken) {
        if (!helper.isExist(oauth2AccessToken.getUid())){
            oauth2AccessTokenList.add(oauth2AccessToken);
            helper.addToken(oauth2AccessToken);
           adapter.notifyDataSetChanged();
        }else {
            helper.updateToken(oauth2AccessToken);
        }
    }

    private void setupAuth() {
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuth();
            }
        });
    }
    private void getAuth() {
        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Token=Oauth2AccessToken.parseAccessToken(bundle);
                Log.e("test", Token.toString() );
                refreshPage(Token);

            }
            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(AccountManageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("test", e.getMessage() );

            }
            @Override
            public void onCancel() {
                Log.e("test", "???" );

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,0,0,"取消授权");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuinfo= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position=menuinfo.position;
        Oauth2AccessToken logoutToken=oauth2AccessTokenList.get(position);
        LogoutAPI logoutAPI=new LogoutAPI(this,Constants.APP_KEY,logoutToken);
        logoutAPI.logout(new RequestListener() {
            @Override
            public void onComplete(String s) {
                Toast.makeText(AccountManageActivity.this, "取消授权成功", Toast.LENGTH_SHORT).show();
                helper.deleteToken(oauth2AccessTokenList.get(position));
                oauth2AccessTokenList.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
        return super.onContextItemSelected(item);
    }
    public class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Oauth2AccessToken token=helper.getTokenList().get(i);
            Log.e("tokeninfo",token.getToken());
            Log.e("tokeninfo",token.getUid());
            SharePreferenceUtil.writeToken(AccountManageActivity.this,token);
            startActivity(new Intent(AccountManageActivity.this,MainActivity.class));
            finish();
        }
    }
}
