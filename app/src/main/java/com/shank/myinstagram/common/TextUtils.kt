package com.shank.myinstagram.common

import android.text.format.DateUtils
import java.util.*

//функция возращает дату
fun formatRelativeTimestamp(start: Date, end: Date): CharSequence =
        DateUtils.getRelativeTimeSpanString(start.time, end.time, DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE).replace(Regex("\\. \\w+$"), "")