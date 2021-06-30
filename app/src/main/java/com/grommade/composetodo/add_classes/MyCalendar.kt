package com.grommade.composetodo.add_classes

import android.os.Parcel
import android.os.Parcelable
import com.grommade.composetodo.util.*
import java.util.*

class MyCalendar(private val _milli: Long = 0L) : Parcelable, Comparable<MyCalendar> {
    private val calendar = Calendar.getInstance().also { it.timeInMillis = _milli }

    val milli: Long
        get() = calendar.timeInMillis

    private val year: Int
        get() = calendar.get(Calendar.YEAR)

    private val month: Int
        get() = calendar.get(Calendar.MONTH)

    private val day: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)

    val hours: Int
        get() = calendar.get(Calendar.HOUR_OF_DAY)

    private val minutes: Int
        get() = calendar.get(Calendar.MINUTE)

    private val time: String
        get() = (hours * MINUTES_IN_HOUR + minutes).toStrTime()

//    operator fun rangeTo(other: MyCalendar): Int {
//        return when {
//            this.milli > other.milli -> 1
//            this.milli < other.milli -> -1
//            else -> 0
//        }
//    }

    override operator fun compareTo(other: MyCalendar) = when {
        milli > other.milli -> 1
        milli < other.milli -> -1
        else -> 0
    }

//    override fun iterator(): Iterator<Long> {
//        return object : Iterator<Long> {
//
//            override fun hasNext(): Boolean = milli < Long.MAX_VALUE
//
//            override fun next(): Long = milli + 1
//        }
//    }

    override fun toString(): String {
        val y = year.toString().padStart(4, '0')
        val m = (month + 1).toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')

        return "$d.$m.$y $time"
    }

    fun toString(showTime: Boolean): String {
        return if (showTime) toString() else toString().dropLast(6)
    }

    fun set(y: Int = 0, m: Int = 0, d: Int = 0, h: Int = 0, min: Int = 0, s: Int = 0) =
        this.also { calendar.set(y, m, d, h, min, s) }

    fun addHours(_hours: Int) = MyCalendar(calendar.timeInMillis + _hours.hoursToMilli())
    fun addMinutes(_minutes: Int) = MyCalendar(calendar.timeInMillis + _minutes.minutesToMilli())
    fun addDays(_days: Int) = MyCalendar(calendar.timeInMillis + _days.daysToMilli())

    fun isEmpty() = milli == 0L
    fun isNoEmpty() = milli != 0L

    fun getNumberDayOfWeek() =
        calendar.get(Calendar.DAY_OF_WEEK) - 2

    constructor(parcel: Parcel) : this(parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_milli)
    }

    operator fun minus(other: MyCalendar) = MyCalendar(milli - other.milli)
    operator fun plus(other: MyCalendar) = MyCalendar(milli + other.milli)

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MyCalendar> {
        override fun createFromParcel(parcel: Parcel): MyCalendar {
            return MyCalendar(parcel)
        }

        override fun newArray(size: Int): Array<MyCalendar?> {
            return arrayOfNulls(size)
        }

        fun now() = MyCalendar(System.currentTimeMillis())
        fun today() = MyCalendar(System.currentTimeMillis())
            .apply { calendar.set(year, month, day, 0, 0, 0) }

        fun random(range: ClosedRange<MyCalendar>) =
            MyCalendar((range.start.milli..range.endInclusive.milli).random())
    }

}