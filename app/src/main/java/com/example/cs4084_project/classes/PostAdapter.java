package com.example.cs4084_project.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cs4084_project.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends BaseAdapter {
    private final List<Post> posts;
    private final Context context;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.listitem_post, null);
        Post post = posts.get(position);

        if (post.getProfilePicturePath() != null) {
            ImageView profilePicture = view.findViewById(R.id.poster_profile_picture);
            Picasso.get().load(post.getProfilePicturePath()).into(profilePicture);
        }

        TextView username = view.findViewById(R.id.poster_username);
        username.setText(post.getUsername());

        TextView title = view.findViewById(R.id.post_title);
        title.setText(post.getTitle());

        TextView date = view.findViewById(R.id.post_date);
        Date d = post.getDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YY hh:mm");
        date.setText(sdf.format(d));

        TextView description = view.findViewById(R.id.post_description);
        description.setText(post.getDescription());

        ImageView image = view.findViewById(R.id.post_image);
        if (post.getImagePath() == null || post.getImagePath().isEmpty()) {
            image.setVisibility(View.GONE);
        } else {
            Picasso.get().load(post.getImagePath()).into(image);
        }

        LinearLayout cafe = view.findViewById(R.id.post_cafe);
        if (post.getCafe() == null) {
            cafe.setVisibility(View.GONE);
        } else {
            TextView cafeName = view.findViewById(R.id.post_cafe_name);
            cafeName.setText(String.format("@ %s,", post.getCafe().getName()));
            TextView cafeLocation = view.findViewById(R.id.post_cafe_location);
            cafeLocation.setText(post.getCafe().getLocation());
            TextView cafeRating = view.findViewById(R.id.post_cafe_rating);
            cafeRating.setText(String.format(Locale.ENGLISH, "%d stars", post.getRating()));
        }

        return view;
    }
}
