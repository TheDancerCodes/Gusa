package com.thedancercodes.gus.presentation.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.thedancercodes.gus.R;
import com.thedancercodes.gus.data.remote.model.User;

import java.util.List;


class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private final Context context;
    private List<User> items;


    UsersAdapter(Context context, List<User> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User item = items.get(position);

        holder.textViewBio.setText(item.getBio());
        if (item.getName() != null) {
            holder.textViewName.setText(item.getLogin() + " - " + item.getName());
        } else {
            holder.textViewName.setText(item.getLogin());
        }
        Picasso.with(context).load(item.getAvatarUrl()).into(holder.imageViewAvatar);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    void setItems(List<User> githubUserList) {
        this.items = githubUserList;
        notifyDataSetChanged();
    }
}
