package com.android.amit.instaclone.util

import java.util.*

/**
 * ================================================
 * Property of of Ubii , LLC
 * ================================================
 * Author: Amit Kumar Sahoo
 * Created On: June/03/2020
 * Description:
 */
object StringUtils {
    @ExperimentalStdlibApi
    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }
}