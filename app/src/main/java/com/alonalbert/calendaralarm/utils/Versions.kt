package com.alonalbert.calendaralarm.utils

import android.os.Build

fun isAtLeast(version: Int) = Build.VERSION.SDK_INT >= version