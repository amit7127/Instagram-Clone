package com.android.amit.instaclone.util

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.android.amit.instaclone.util.StringUtils.capitalizeWords
import com.squareup.picasso.Picasso


/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/03/2020
 * Description:
 */
@ExperimentalStdlibApi
@BindingAdapter("android:set_name")
fun setName(view: TextView, text: String) {
    view.text = text.capitalizeWords()
}

@BindingAdapter("load_image")
fun loadImage(view: ImageView?, imageUri: Uri?) {
    if (imageUri != null && imageUri != Uri.EMPTY) {
        Picasso.get()
            .load(imageUri)
            .into(view);
    }
}