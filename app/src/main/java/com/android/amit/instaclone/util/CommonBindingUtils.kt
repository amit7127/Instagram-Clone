package com.android.amit.instaclone.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.android.amit.instaclone.util.StringUtils.capitalizeWords


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