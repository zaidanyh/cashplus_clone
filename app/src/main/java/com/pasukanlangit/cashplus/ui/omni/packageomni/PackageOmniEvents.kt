package com.pasukanlangit.cashplus.ui.omni.packageomni

sealed class PackageOmniEvents {
    data class FetchMenu(
        val uuid: String, val dest: String, val accessToken: String
    ): PackageOmniEvents()
    object RemoveFetchMenuError: PackageOmniEvents()
    data class FetchPackage(
        val uuid: String, val dest: String, val mlId: String, val accessToken: String
    ): PackageOmniEvents()
    object RemovePackageMessage: PackageOmniEvents()
    object RemoveBalanceMessage: PackageOmniEvents()
    data class PackageOrder(
        val uuid: String, val dest: String, val packageId: String, val accessToken: String
    ): PackageOmniEvents()
    object RemovePackageOrderMessage: PackageOmniEvents()
    data class PackageTransaction(
        val uuid: String, val codeProduct: String, val dest: String, val pin: String, val accessToken: String
    ): PackageOmniEvents()
    object RemovePackageTransactionMessage: PackageOmniEvents()
}