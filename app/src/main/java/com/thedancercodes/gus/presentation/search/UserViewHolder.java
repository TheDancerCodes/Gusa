package com.thedancercodes.gus.presentation.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thedancercodes.gus.R;


class UserViewHolder extends RecyclerView.ViewHolder {

    final TextView textViewBio;
    final TextView textViewName;
    final ImageView imageViewAvatar;


    UserViewHolder(View view) {
        super(view);
        imageViewAvatar = view.findViewById(R.id.imageview_userprofilepic);
        textViewName = view.findViewById(R.id.textview_username);
        textViewBio = view.findViewById(R.id.textview_user_profile_info);
    }
}
