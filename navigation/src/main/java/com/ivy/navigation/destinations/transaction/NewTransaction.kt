package com.ivy.navigation.destinations.transaction

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ivy.data.transaction.TransactionType
import com.ivy.navigation.DestinationRoute
import com.ivy.navigation.Screen
import com.ivy.navigation.util.arg
import com.ivy.navigation.util.int
import com.ivy.navigation.util.optionalStringArg

object NewTransaction : Screen<NewTransaction.Arg> {
    data class Arg(
        val trnType: TransactionType,
        val categoryId: String?,
        val accountId: String?,
    )

    private const val ARG_TRN_TYPE = "trnType"
    private const val ARG_CATEGORY_ID = "catId"
    private const val ARG_ACCOUNT_ID = "accId"

    override val route: String = "new/transaction?trnType={$ARG_TRN_TYPE}" +
            "&catId={$ARG_CATEGORY_ID}&accId={$ARG_ACCOUNT_ID}"

    override val arguments = listOf(
        navArgument(ARG_TRN_TYPE) {
            type = NavType.IntType
            nullable = false
        },
        navArgument(ARG_CATEGORY_ID) {
            type = NavType.StringType
            nullable = true
        },
        navArgument(ARG_ACCOUNT_ID) {
            type = NavType.StringType
            nullable = true
        }
    )

    override fun destination(arg: Arg): DestinationRoute {
        val route = StringBuilder("new/transaction?$ARG_TRN_TYPE=${arg.trnType.code}")
        if (arg.categoryId != null) {
            route.append("&$ARG_CATEGORY_ID=${arg.categoryId}")
        }
        if (arg.accountId != null) {
            route.append("&$ARG_ACCOUNT_ID=${arg.accountId}")
        }
        return route.toString()
    }

    override fun parse(entry: NavBackStackEntry): Arg = Arg(
        trnType = entry.arg(ARG_TRN_TYPE, int()) {
            TransactionType.fromCode(it) ?: TransactionType.Expense
        },
        categoryId = entry.optionalStringArg(ARG_CATEGORY_ID),
        accountId = entry.optionalStringArg(ARG_ACCOUNT_ID),
    )
}