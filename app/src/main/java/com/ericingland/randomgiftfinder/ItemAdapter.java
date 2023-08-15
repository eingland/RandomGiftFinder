package com.ericingland.randomgiftfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<Item> itemList;

    private final Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        final Item item = itemList.get(i);
        ImageLoader mImageLoader = HttpRequestHandler.getInstance(itemViewHolder.vTitle.getContext()).getImageLoader();
        itemViewHolder.vTitle.setText(item.getTitle());
        itemViewHolder.vImageUrl.setImageUrl(item.getImageUrl(), mImageLoader);

        itemViewHolder.vBrowseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(item.getUrl()));
                context.startActivity(i);
            }

        });

        itemViewHolder.vShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, item.getUrl());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.share)));
            }

        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_item, viewGroup, false);

        return new ItemViewHolder(itemView);
    }

    public Item getItem(int position) {
        return itemList.get(position);
    }

    public void removeItem(int position) {
        itemList.remove(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView vTitle;
        final NetworkImageView vImageUrl;
        final Button vBrowseButton;
        final Button vShareButton;

        public ItemViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.title);
            vImageUrl = (NetworkImageView) v.findViewById(R.id.image);
            vBrowseButton = (Button) v.findViewById(R.id.btnCardBrowser);
            vShareButton = (Button) v.findViewById(R.id.btnCardShare);
        }
    }
}
