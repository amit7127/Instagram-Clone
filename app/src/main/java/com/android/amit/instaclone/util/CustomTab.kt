package com.android.amit.instaclone.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.android.amit.instaclone.R
import com.android.amit.instaclone.databinding.LayoutCustomTabBinding

/**
 * This class is for profile page post/saved posts tab view
 *
 * Created for better understanding of use of data binding in CustomView
 */


class CustomTab : LinearLayout {

    var selectedTab: Int = Constants.postsTab
    private lateinit var mBinding: LayoutCustomTabBinding
    var listener: CustomTabListener? = null

    constructor(context: Context?) : super(context) {
        if (!isInEditMode)
            init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        if (!isInEditMode)
            init()
    }

    /**
     * initialize view
     */
    private fun init() {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_custom_tab,
            this,
            true
        )
        mBinding.listener = this
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        if (!isInEditMode)
            init()
    }

    /**
     * on tab selected
     */
    fun onTabSelected(id: Int) {
        selectedTab = id
        mBinding.invalidateAll()
        listener?.onTabChanged(id)
    }

    /**
     * tab listener
     */
    interface CustomTabListener {
        //On tab changed
        fun onTabChanged(id: Int)
    }
}