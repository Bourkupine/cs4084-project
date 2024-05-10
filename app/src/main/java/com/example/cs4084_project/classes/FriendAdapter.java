package com.example.cs4084_project.classes;

import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs4084_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

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

        this.setListeners(view, friend);


        return view;
    }

    private void setListeners(View view, Friend friend) {
        ImageView remove = view.findViewById(R.id.remove_friend);

        remove.setOnClickListener(v -> openFriendListener.removeFriend(friend.getUid()));
    }

    public interface OpenFriend {
        void removeFriend(String friendId);
    }

}
