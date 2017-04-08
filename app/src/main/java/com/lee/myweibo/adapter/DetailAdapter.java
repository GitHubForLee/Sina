package com.lee.myweibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.ninegridimageview.NineGridImageView;
import com.lee.myweibo.R;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/2/26.
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Status status;
    private List<Comment> comments;
    private Context context;
    private LayoutInflater inflater;
    private static final int TYPE_WEIBO=0;
    private int TYPE_COMMENT=1;
    private NineGridAdapter adapter;
    public DetailAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
        comments=new ArrayList<>();
        adapter=new NineGridAdapter();
    }
    public void clear(){
        comments.clear();
    }

    public void setWeiBo(Status status){
        this.status=status;
    }
    public void addData(List<Comment> comments){
        if (comments != null) {
            this.comments.addAll(comments);
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemViewType(int position) {

        return position==0?TYPE_WEIBO:TYPE_COMMENT;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType==TYPE_WEIBO){
            View view=inflater.inflate(R.layout.weibo_item2,parent,false);
            holder=new UserHolder(view);
        }else {
            View view=inflater.inflate(R.layout.layout_comment,parent,false);
            holder=new CommentHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        if (mholder instanceof UserHolder){
            UserHolder holder= (UserHolder) mholder;
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
                holder.tv_retweetedName.setText(status.retweeted_status.user.screen_name);
                holder.tv_retweetedContent.setText(status.retweeted_status.text);
                holder.iv_retweetedNineGrid.setAdapter(adapter);
                holder.iv_retweetedNineGrid.setImagesData(status.retweeted_status.pic_urls);
            }
            else{
                holder.iv_retweetedNineGrid.setVisibility(View.GONE);
                holder.ll_retweetedContainer.setVisibility(View.VISIBLE);
                if (status.retweeted_status.user!=null){
                    holder.tv_retweetedName.setText(status.retweeted_status.user.screen_name);
                }
                holder.tv_retweetedContent.setText(status.retweeted_status.text);
            }
        }else {
            Comment comment=comments.get(position-1);
            CommentHolder holder= (CommentHolder) mholder;
            Picasso.with(context).load(comment.user.profile_image_url).into(holder.iv_userIcon);
            holder.tv_content.setText(comment.text);
            holder.tv_userName.setText(comment.user.screen_name);
            holder.tv_timeAndFrom.setText(comment.created_at);
        }


    }

    @Override
    public int getItemCount() {
        return comments.size()+1;
    }


    public class UserHolder extends RecyclerView.ViewHolder{

        private ImageView iv_userIcon;
        private TextView tv_userName;
        private TextView tv_timeAndFrom;
        private TextView tv_user_content;
        private TextView tv_retweetedName;
        private TextView tv_retweetedContent;
        private LinearLayout ll_retweetedContainer;
        private NineGridImageView iv_userNineGrid;
        private NineGridImageView iv_retweetedNineGrid;
        public UserHolder(View itemView) {
            super(itemView);
            iv_userIcon= (ImageView) itemView.findViewById(R.id.iv_userIcon);
            tv_userName= (TextView) itemView.findViewById(R.id.tv_userName);
            tv_user_content= (TextView) itemView.findViewById(R.id.tv_userContent);
            tv_timeAndFrom= (TextView) itemView.findViewById(R.id.tv_timeAndFrom);
            tv_retweetedName= (TextView) itemView.findViewById(R.id.tv_retweetedName);
            tv_retweetedContent= (TextView) itemView.findViewById(R.id.tv_retweetedContent);
            ll_retweetedContainer= (LinearLayout) itemView.findViewById(R.id.ll_retweetedContainer);
            iv_retweetedNineGrid= (NineGridImageView) itemView.findViewById(R.id.iv_retweetedNineGrid);
            iv_userNineGrid= (NineGridImageView) itemView.findViewById(R.id.iv_userNineGrid);
        }
    }
    public class CommentHolder extends RecyclerView.ViewHolder{
        private ImageView iv_userIcon;
        private TextView tv_userName;
        private TextView tv_timeAndFrom;
        private TextView tv_content;
        public CommentHolder(View itemView) {
            super(itemView);
            iv_userIcon= (ImageView) itemView.findViewById(R.id.iv_userIcon);
            tv_userName= (TextView) itemView.findViewById(R.id.tv_userName);
            tv_timeAndFrom= (TextView) itemView.findViewById(R.id.tv_timeAndFrom);
            tv_content= (TextView) itemView.findViewById(R.id.tv_content);

        }
    }

}
