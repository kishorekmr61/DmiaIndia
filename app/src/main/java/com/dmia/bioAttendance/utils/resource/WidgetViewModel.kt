package com.customer.bms.utils.resource

import androidx.annotation.LayoutRes

interface WidgetViewModel {
    @LayoutRes
    fun layoutId(): Int
}