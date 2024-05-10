package com.example.cs4084_project.classes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs4084_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends BaseAdapter {
    private final List<Comment> comments;
    private final Context context;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.listitem_comment, null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Comment comment = comments.get(position);

        TextView message = view.findViewById(R.id.comment_message);
        message.setText(comment.getMessage());

        TextView date = view.findViewById(R.id.comment_date);
        Date d = comment.getDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.UK);
        date.setText(sdf.format(d));

        ImageView profilePicture = view.findViewById(R.id.commenter_profile_picture);
        db.collection("users").document(comment.getCommenterId()).get().addOnSuccessListener(documentSnapshot -> {
            String profilePic = documentSnapshot.getString("profilePicture");
            if (!TextUtils.isEmpty(profilePic)) {
                Picasso.get().load(profilePic).into(profilePicture);
            }
        });

        return view;
    }
}
