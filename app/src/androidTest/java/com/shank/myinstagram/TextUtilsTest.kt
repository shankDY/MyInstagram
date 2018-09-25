package com.shank.myinstagram

import android.support.test.runner.AndroidJUnit4
import com.shank.myinstagram.common.formatRelativeTimestamp

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.*


@RunWith(AndroidJUnit4::class)
class TextUtilsTest {
    private val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2018)
        set(Calendar.MONTH, 0)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    @Test
    fun shouldFormatRelativeTime() {
        val time = calendar.time
        val time10Sec = calendar.change { add(Calendar.SECOND, 10) }.time
        val time10Min = calendar.change { add(Calendar.MINUTE, 10) }.time
        val time1Hour = calendar.change { add(Calendar.HOUR_OF_DAY, 10) }.time
        val time1Day = calendar.change { add(Calendar.DAY_OF_MONTH, 1) }.time
        assertEquals("10 сек", formatRelativeTimestamp(time, time10Sec))
        assertEquals("10 мин", formatRelativeTimestamp(time, time10Min))
        assertEquals("10 ч", formatRelativeTimestamp(time, time1Hour))
        assertEquals("Вчера", formatRelativeTimestamp(time, time1Day))
    }

    private fun Calendar.change(f: Calendar.() -> Unit): Calendar {
        val newCalendar = clone() as Calendar
        f(newCalendar)
        return newCalendar
    }
}