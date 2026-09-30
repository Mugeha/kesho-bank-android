package com.keshobank.mobile.util

import java.util.Locale

fun formatKes(cents: Long): String {
    val amount = cents / 100.0
    return String.format(Locale.US, "KES %,.2f", amount)
}
