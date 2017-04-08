package com.lee.myweibo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.lee.myweibo.adapter.DetailAdapter;
import com.lee.myweibo.utils.SharePreferenceUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2017/2/26.
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.tl_weiboDetail)
    Toolbar toolbar;
    @BindView(R.id.rv_weiboDetail)
    LRecyclerView recyclerView;
    @BindView(R.id.tv_report)
    TextView tvRepost;
    @BindView(R.id.tv_coment)
    TextView tvConment;
    Status status;
    String weiboId;
    DetailAdapter adapter;
    int page;
    private boolean loading;
    private boolean refreshed;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Long id=Long.valueOf(status.id);
            int viewId=view.getId();
            if (viewId==R.id.tv_report){
                EditAvtivity.startActivity(DetailActivity.this,id,EditAvtivity.TYPE_REPOST);
            }else {
                EditAvtivity.startActivity(DetailActivity.this,id,EditAvtivity.TYPE_COMMENT);
            }
        }
    };
    public static void startActivity(Context context,Status status){
        Intent intent= new Intent(context,DetailActivity.class);
        intent.putExtra("weibo",status);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);
        ButterKnife.bind(this);
        setupToolBar();
        setupRecyclerview();
        serupTextView();
        setupWeiboId();
        adapter.setWeiBo(status);
        loadData();
    }

    private void setupWeiboId() {
        status=(Status) getIntent().getSerializableExtra("weibo");
        weiboId=status.id;
    }

    private void serupTextView() {
        tvRepost.setOnClickListener(listener);
        tvConment.setOnClickListener(listener);

    }

    private void setupRecyclerview() {
//        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter(this);
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);

        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!loading){
                    Log.e("load","1");
                    loading=!loading;
                    loadData();
                }

            }
        });
        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!loading){
                    loading=!loading;
                    page=0;
                    loadData();
                    refreshed=true;
                }

            }
        });

    }

    private void setupToolBar() {

    }
    private void loadData(){
        Oauth2AccessToken token= SharePreferenceUtil.readToken(this);
        final CommentsAPI commentsAPI=new CommentsAPI(this, Constants.APP_KEY,token);
        WeiboParameters parameters=new WeiboParameters(Constants.APP_KEY);
        parameters.put("access_token",token.getToken());
        parameters.put("page",++page);
        Log.e("load","page   "+page);
        parameters.put("id",weiboId);
        parameters.put("count",20);
        commentsAPI.showComments(parameters, new RequestListener() {
            @Override
            public void onComplete(String s) {
                loading=false;
                CommentList commentList=CommentList.parse(s);
                recyclerView.refreshComplete(page);
                Log.e("text",s);
                if (refreshed){
                    adapter.clear();
                    refreshed=false;
                }
                if (commentList!=null&&commentList.total_number!=0){

                    adapter.addData(commentList.commentList);
                }
            }
            @Override
            public void onWeiboException(WeiboException e) {

            }
        });

    }
}
