package com.android.amit.instaclone.util

import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.android.amit.instaclone.R
import com.android.amit.instaclone.util.StringUtils.capitalizeWords
import com.squareup.picasso.Picasso

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : Custom Adapters
 */

/**
 * Set name with Caps word
 * @param view: TextView
 * @param text: String text value
 */
@ExperimentalStdlibApi
@BindingAdapter("android:set_name")
fun setName(view: TextView, text: String?) {
    if (text != null)
        view.text = text.capitalizeWords()
}

/**
 * Load image into image view
 * @param view: ImageView object
 * @param imageUri: image Uri object
 */
@BindingAdapter("load_image")
fun loadImage(view: ImageView?, imageUri: Uri?) {
    if (imageUri != null && imageUri != Uri.EMPTY) {
        Picasso.get()
            .load(imageUri)
            .into(view)
    }
}

/**
 * Load image into image view
 * @param view: ImageView object
 * @param imageUrlString: image file path
 */
@BindingAdapter("load_image")
fun loadImage(view: ImageView?, imageUrlString: String?) {
    if (imageUrlString != null && !TextUtils.isEmpty(imageUrlString)) {
        val imageUri = Uri.parse(imageUrlString)
        Picasso.get()
            .load(imageUri)
            .into(view)
    }
}

/**
 * set like/un-like image on clicking like button
 * @param imageView: image view
 * @param isLike: old status true/false
 */
@BindingAdapter("set_like")
fun isLiked(imageView: ImageView, isLike: Boolean) {
    if (isLike) {
        imageView.setImageResource(R.drawable.heart_clicked)
    } else {
        imageView.setImageResource(R.drawable.heart_not_clicked)
    }
}

/**
 * set saved images according to status
 * @param imageView: ImageView object
 * @param isSaved: old tstaus boolean value
 */
@BindingAdapter("set_saved_status")
fun isSaved(imageView: ImageView, isSaved: Boolean) {
    if (isSaved) {
        imageView.setImageResource(R.drawable.save_large_icon)
    } else {
        imageView.setImageResource(R.drawable.save_unfilled_large_icon)
    }
}