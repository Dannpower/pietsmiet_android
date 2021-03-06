package de.pscom.pietsmiet.adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.TimeUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {

    private static final int LAYOUT_THUMBNAIL = 0;
    private static final int LAYOUT_WIDE_IMAGE = 1;

    private final List<Post> items;
    private final Context context;

    public CardViewAdapter(List<Post> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public CardViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == LAYOUT_THUMBNAIL) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_thumbnail, parent, false);
            return new ThumbnailCardViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_wide_image, parent, false);
            return new CardViewHolder(v);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CardViewAdapter.CardViewHolder holder, int position) {
        Post currentItem = items.get(position);

        if (holder.getItemViewType() == LAYOUT_THUMBNAIL) {
            ThumbnailCardViewHolder videoHolder = (ThumbnailCardViewHolder) holder;
            if (currentItem.getPostType() != VIDEO) {
                videoHolder.durationIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watch_later_black_24dp));
                videoHolder.btnExpand.setVisibility(VISIBLE);
                videoHolder.btnExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
                videoHolder.btnExpand.setOnClickListener(view -> {
                    if (videoHolder.descriptionContainer.getVisibility() == GONE) {
                        videoHolder.descriptionContainer.setVisibility(VISIBLE);
                        view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_less_black_24dp));
                    } else {
                        videoHolder.descriptionContainer.setVisibility(GONE);
                        view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
                    }
                });
            } else {
                videoHolder.btnExpand.setVisibility(GONE);
                videoHolder.descriptionContainer.setVisibility(GONE);

            }
        }

        if (currentItem.getPostType() == UPLOADPLAN) {
            holder.time_container.setVisibility(GONE);
        } else {
            holder.time_container.setVisibility(VISIBLE);
            holder.timedate.setVisibility(VISIBLE);
            holder.timedate.setText(TimeUtils.getTimeSince(currentItem.getDate(), context));
        }

        Drawable thumbnail = currentItem.getThumbnail();
        if (thumbnail != null) {
            holder.thumbnail.setImageDrawable(thumbnail);
            holder.thumbnail.setVisibility(VISIBLE);
        } else if (currentItem.getPostType() == PIETCAST) {
            holder.thumbnail.setImageResource(R.drawable.pietcast_placeholder);
            holder.thumbnail.setVisibility(VISIBLE);
        } else {
            holder.thumbnail.setVisibility(GONE);
        }

        holder.title.setText(currentItem.getTitle());
        if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
            //noinspection deprecation
            holder.description.setText(Html.fromHtml(currentItem.getDescription()));
        }

        holder.itemView.setOnClickListener(view -> {
            try {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getUrl()));
                context.startActivity(browserIntent);
            } catch (ActivityNotFoundException | NullPointerException e) {
                PsLog.w("Cannot open browser intent. Url was: " + currentItem.getUrl());
                //Error Toast Notification todo evtl eigene Funktion in seperater Klasse ?
                CharSequence errMsg = "Cannot open browser. Retry";
                Toast errToast = Toast.makeText(context, errMsg, Toast.LENGTH_SHORT);
                errToast.show();
            }
        });

        holder.cv.setCardBackgroundColor(currentItem.getBackgroundColor());
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isThumbnailView()) return LAYOUT_THUMBNAIL;
        else return LAYOUT_WIDE_IMAGE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        final CardView cv;
        final TextView title;
        final TextView description;
        final TextView timedate;
        final ImageView thumbnail;
        final RelativeLayout time_container;

        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            timedate = (TextView) itemView.findViewById(R.id.tvDateTime);
            thumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            time_container = (RelativeLayout) itemView.findViewById(R.id.rlTimeContainer);
        }
    }

    private static class ThumbnailCardViewHolder extends CardViewAdapter.CardViewHolder {
        final RelativeLayout descriptionContainer;
        final ImageView durationIcon;
        final Button btnExpand;

        ThumbnailCardViewHolder(View itemView) {
            super(itemView);
            durationIcon = (ImageView) itemView.findViewById(R.id.ivDuration);
            btnExpand = (Button) itemView.findViewById(R.id.btnExpand);
            descriptionContainer = (RelativeLayout) itemView.findViewById(R.id.rlDescriptionContainer);
        }

    }
}
