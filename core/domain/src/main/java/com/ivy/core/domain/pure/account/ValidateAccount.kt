package com.ivy.core.domain.pure.account

import com.ivy.data.account.Account

fun validate(account: Account): Boolean {
    if (account.name.isBlank()) return false
    return true
}