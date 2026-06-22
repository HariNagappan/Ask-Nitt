package com.example.asknitt.data.functions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun GetUtcInLocalTime(utc_time:String):String{
    val formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val utcDateTime = LocalDateTime.parse(utc_time, formatter).atZone(ZoneOffset.UTC)
    val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
    val displayFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
    return localDateTime.format(displayFormat)
}

@RequiresApi(Build.VERSION_CODES.O)
fun GetLocalInUTC(local: String,start_of_day:Boolean): String {
    val systemZone = ZoneId.systemDefault()
    val formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val return_format= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localdate=LocalDate.parse(local,formatter)
    val localdatetime=if(start_of_day) localdate.atStartOfDay() else localdate.plusDays(1).atStartOfDay()
    val localzone = localdatetime.atZone(systemZone)
    val utczoned =localzone.withZoneSameInstant(ZoneOffset.UTC)
    return utczoned.format(return_format)

}