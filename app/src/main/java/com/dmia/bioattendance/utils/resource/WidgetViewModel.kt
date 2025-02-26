package com.dmia.bioattendance.utils.resource

import androidx.annotation.LayoutRes

interface WidgetViewModel {
    @LayoutRes
    fun layoutId(): Int
}