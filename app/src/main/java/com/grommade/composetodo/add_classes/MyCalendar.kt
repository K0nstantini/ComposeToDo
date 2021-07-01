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


    fun getMinutesOfDay() = hours * MINUTES_IN_HOUR + minutes

//    override operator fun compareTo(other: MyCalendar): Int = when {
//        milli > other.milli -> 1
//        milli < other.milli -> -1
//        else -> 0
//    }

    fun isEqual(other: MyCalendar) =
        compareTo(other) == 0

    override fun compareTo(other: MyCalendar): Int {
        return COMPARATOR.compare(this, other)
    }

//    override fun compareTo(other: MyCalendar): Int = compareValuesBy(this, other, { it._milli })


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

    fun startDay(): MyCalendar {
        val thisDate = this
        return MyCalendar().apply {
            calendar.set(thisDate.year, thisDate.month, thisDate.day, 0, 0, 0)
        }
    }

    fun endDay(): MyCalendar {
        val thisDate = this
        return MyCalendar().apply {
            calendar.set(thisDate.year, thisDate.month, thisDate.day, 23, 59, 59)
        }
    }

    fun addHours(_hours: Int) = MyCalendar(calendar.timeInMillis + _hours.hoursToMilli())
    fun addMinutes(_minutes: Int) = MyCalendar(calendar.timeInMillis + _minutes.minutesToMilli())
    fun addDays(_days: Int) = MyCalendar(calendar.timeInMillis + _days.daysToMilli())

    fun isEmpty() = milli == 0L
    fun isNoEmpty() = milli != 0L

    fun getNumberDayOfWeek() = when (val day = calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> 6
        else -> day - 2
    }

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
        fun today(): MyCalendar {
            val now = now()
            return MyCalendar().apply { calendar.set(now.year, now.month, now.day, 0, 0, 0) }
        }

        fun random(range: ClosedRange<MyCalendar>) =
            MyCalendar((range.start.milli..range.endInclusive.milli).random())

        private val COMPARATOR = Comparator.comparingLong<MyCalendar> { it.milli }
    }

}