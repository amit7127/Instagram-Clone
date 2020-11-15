package com.android.amit.instaclone.util

import java.util.*

/**
 * File created at 27/05/2020
 * Author : Amit Kumar Sahoo
 * email: amit.sahoo@mindfiresolutions.com
 * About file : String utils
 */
object StringUtils {
    @ExperimentalStdlibApi
    fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }
}