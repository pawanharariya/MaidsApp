package com.psh.maids.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.psh.maids.R;
import com.psh.maids.models.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class reviewAdapter extends ArrayAdapter<Review> {
    Context context;

    public reviewAdapter(@NonNull Context context, int resource, ArrayList<Review> reviewList) {
        super(context, resource, reviewList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View reviewListView = convertView;
        if (reviewListView == null)
            reviewListView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        Review currentReview = getItem(position);
        TextView name = reviewListView.findViewById(R.id.name);
        TextView city = reviewListView.findViewById(R.id.city);
        TextView date = reviewListView.findViewById(R.id.date);
        TextView comment = reviewListView.findViewById(R.id.comment);
        TextView rating = reviewListView.findViewById(R.id.rating);
        CircleImageView image = reviewListView.findViewById(R.id.image);
        Picasso.with(context).load(currentReview.getImageUrl()).resize(60, 60).placeholder(R.drawable.user).into(image);
        name.setText(currentReview.getName());
        city.setText(currentReview.getCity());
        date.setText("" + currentReview.getDate());
        comment.setText(currentReview.getReviews());
        rating.setText("" + currentReview.getRating());

        return reviewListView;
    }
}
