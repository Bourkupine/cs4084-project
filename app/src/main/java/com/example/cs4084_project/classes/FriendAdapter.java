package com.example.cs4084_project.classes;

import android.media.Image;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cs4084_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.core.content.ContextCompat;

public class FriendAdapter extends BaseAdapter {

    private final List<Friend> friends;
    private final Context context;
    FirebaseFirestore db;
    FirebaseUser user;
    OpenFriend openFriendListener;

    public FriendAdapter(Context context, List<Friend> friends) {
        this.friends = friends;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public FriendAdapter(Context context, List<Friend> friends, OpenFriend openFriendListener) {
        this(context, friends);
        this.openFriendListener = openFriendListener;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.listitem_friend, null);
        Friend friend = friends.get(position);

        TextView user = view.findViewById(R.id.friend_username);
        user.setText(friend.getUsername());

        ImageView pfp = view.findViewById(R.id.friend_profile_picture);
        if (friend.getProfilePic() == null || friend.getProfilePic().isEmpty()) {
            pfp.setVisibility(View.GONE);
        } else {
            Picasso.get().load(friend.getProfilePic()).into(pfp);
        }

        return view;
    }

    public interface OpenFriend {
        void openFriend(String friendId);
    }

}
