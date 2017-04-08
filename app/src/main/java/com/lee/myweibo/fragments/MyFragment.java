package com.lee.myweibo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.lee.myweibo.activity.EditAvtivity;
import com.lee.myweibo.adapter.WeiboAdapter;
import com.lee.myweibo.utils.SharePreferenceUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2017/2/24.
 */

public class MyFragment extends Fragment {

    private WeiboAdapter adapter;
    private int page=0;

    @BindView(R.id.rv_weiboContainer)
    LRecyclerView recyclerView;
    @BindView(R.id.fab_send)
    FloatingActionButton fabSend;
    private boolean loading;
    private boolean refreshed;

    public static MyFragment newInstance(int i) {
        MyFragment myFragment = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("key",i);
        myFragment.setArguments(bundle);
        return myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecylerView();
        setupFAB();
        loadData();
    }

    private void setupFAB() {
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAvtivity.startActivity(getContext(),0L,EditAvtivity.TYPE_WEIBO);
            }
        });
    }

    private void loadData() {
       Oauth2AccessToken token= SharePreferenceUtil.readToken(getActivity());
        StatusesAPI statusAPI=new StatusesAPI(getActivity(), Constants.APP_KEY,token);
        WeiboParameters parameters=new WeiboParameters(Constants.APP_KEY);
        parameters.put("access_token",token.getToken());
        parameters.put("page",++page);
        Log.e("page",page+"");
        statusAPI.test(parameters, new RequestListener() {
            @Override
            public void onComplete(String s) {
                StatusList statusList=StatusList.parse(s);
                recyclerView.refreshComplete(page);
                loading=false;
                if (statusList == null) {
                    Log.e("nullpointer","?????");
                    return;
                }
                if (refreshed){
                    adapter.clear();
                    refreshed=false;
                }
                adapter.addData(statusList.statusList);
                recyclerView.setPullRefreshEnabled(true);

            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.e("????",e.getMessage());
            }
        });

    }

    private void setupRecylerView() {
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        adapter = new WeiboAdapter(getActivity());
        adapter.setData(new ArrayList<Status>());
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!loading){
                    loading=true;
                    loadData();
                }

            }
        });
        recyclerView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                if(!loading){
                    loading=true;
                    refreshed=true;
                    page=0;
                    loadData();
                }

            }
        });
       recyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
           @Override
           public void onScrollUp() {
               fabGone();
           }

           

           @Override
           public void onScrollDown() {
               fabVisible();

           }

           @Override
           public void onScrolled(int i, int i1) {

           }

           @Override
           public void onScrollStateChanged(int i) {

           }
       });
    }

    private void fabVisible() {
        fabSend.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(200)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        fabSend.setClickable(true);
                    }
                })
                .start();


    }

    private void fabGone() {
        fabSend.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(200)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        fabSend.setClickable(false);
                    }
                })
                .start();

    }


}
