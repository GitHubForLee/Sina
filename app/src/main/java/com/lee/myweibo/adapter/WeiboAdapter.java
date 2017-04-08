package com.lee.myweibo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.ninegridimageview.NineGridImageView;
import com.lee.myweibo.Constants;
import com.lee.myweibo.R;
import com.lee.myweibo.activity.DetailActivity;
import com.lee.myweibo.utils.SharePreferenceUtil;
import com.lee.myweibo.utils.SpannableStringUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lee on 2017/2/24.
 */

public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.WeiboHolder> {
    private List<Status> statuses;
    private LayoutInflater inflater;
    private Context context;
    private NineGridAdapter adapter;
    private String uid;
    private Oauth2AccessToken token;
    public WeiboAdapter(Context context){
        inflater=LayoutInflater.from(context);
        this.context=context;
        adapter=new NineGridAdapter();
        token=SharePreferenceUtil.readToken(context);
        uid= token.getUid();
    }
    public void clear(){
        statuses.clear();
    }

    public void setData(List<Status> statuses){
        this.statuses=statuses;
    }
    public void addData(List<Status> statuses){

        if (statuses==null){
            Log.e("nullpointer","2");
            return;
        }
        this.statuses.addAll(statuses);
        notifyDataSetChanged();
    }
    @Override
    public WeiboAdapter.WeiboHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.weibo_item,parent,false);
        return new WeiboHolder(view);
    }

    @Override
    public void onBindViewHolder(WeiboAdapter.WeiboHolder holder, int position) {
        Status status=statuses.get(position);
        holder.status=status;
        holder.position=position;
        Picasso.with(context).load(status.user.profile_image_url).into(holder.iv_userIcon);
        holder.tv_userName.setText(status.user.screen_name);
        holder.tv_user_content.setText(status.text);
        holder.tv_timeAndFrom.setText(status.created_at);
        if (status.pic_urls == null) {
            holder.iv_userNineGrid.setVisibility(View.GONE);
        }else {
            holder.iv_userNineGrid.setVisibility(View.VISIBLE);
            holder.iv_userNineGrid.setAdapter(adapter);
            holder.iv_userNineGrid.setImagesData(status.pic_urls);
        }


        if (status.retweeted_status==null){
            holder.ll_retweetedContainer.setVisibility(View.GONE);

        }else if (status.retweeted_status.pic_urls!=null){
            holder.ll_retweetedContainer.setVisibility(View.VISIBLE);
            holder.iv_retweetedNineGrid.setVisibility(View.VISIBLE);

            holder.tv_retweetedName.setText(SpannableStringUtil.getSpannaleString(status.retweeted_status.user.screen_name));
            holder.tv_retweetedName.append(":"+status.retweeted_status.text);
            holder.iv_retweetedNineGrid.setAdapter(adapter);
            holder.iv_retweetedNineGrid.setImagesData(status.retweeted_status.pic_urls);
        }
        else{
            holder.iv_retweetedNineGrid.setVisibility(View.GONE);
            holder.ll_retweetedContainer.setVisibility(View.VISIBLE);
            if (status.retweeted_status.user!=null){

                holder.tv_retweetedName.setText(SpannableStringUtil.getSpannaleString(status.retweeted_status.user.screen_name));
            }
            holder.tv_retweetedName.append("："+status.retweeted_status.text);
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class WeiboHolder extends RecyclerView.ViewHolder {
        int position;
        Status status;
        private ImageView iv_userIcon;
        private TextView tv_userName;
        private TextView tv_timeAndFrom;
        private TextView tv_user_content;
        private TextView tv_retweetedName;
        private TextView tv_retweetedContent;
        private LinearLayout ll_retweetedContainer;
        private NineGridImageView iv_userNineGrid;
        private NineGridImageView iv_retweetedNineGrid;
        public WeiboHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DetailActivity.startActivity(context,status);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (status.user.id.equals(uid)){
                        showDialog(status.id,position);
                    }
                    return true;
                }
            });
            iv_userIcon= (ImageView) itemView.findViewById(R.id.iv_userIcon);
            tv_userName= (TextView) itemView.findViewById(R.id.tv_userName);
            tv_user_content= (TextView) itemView.findViewById(R.id.tv_userContent);
            tv_timeAndFrom= (TextView) itemView.findViewById(R.id.tv_timeAndFrom);
            tv_retweetedName= (TextView) itemView.findViewById(R.id.tv_retweetedName);
            tv_retweetedContent= (TextView) itemView.findViewById(R.id.tv_retweetedContent);
            ll_retweetedContainer= (LinearLayout) itemView.findViewById(R.id.ll_retweetedContainer);
            iv_retweetedNineGrid= (NineGridImageView) itemView.findViewById(R.id.iv_retweetedNineGrid);
            iv_userNineGrid= (NineGridImageView) itemView.findViewById(R.id.iv_userNineGrid);
            ll_retweetedContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DetailActivity.startActivity(context,status.retweeted_status);

                }
            });
        }
    }

    private void showDialog(final String id,final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("是否删除该微博");
        builder.setIcon(R.drawable.ic_weibo);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StatusesAPI api=new StatusesAPI(context, Constants.APP_KEY,token);
                WeiboParameters parameters=new WeiboParameters(Constants.APP_KEY);
                parameters.put("id",id);
                parameters.put("access_token",token.getToken());
                api.destroy(parameters, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        statuses.remove(position);
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {

                    }
                });
            }
        });
        builder.create().show();
        Log.e("??/","??1");
    }
}
