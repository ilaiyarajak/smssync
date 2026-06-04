package com.smssync.util

object BankSmsFilter {
    private val senderKeywords = listOf("AX", "VK", "VM", "JD", "AD")
    private val bodyKeywords = listOf(
        "credited", "debited", "spent", "withdrawn", "purchase",
        "transaction", "upi", "balance", "rs.", "inr", "account"
    )

    fun isBankSms(sender: String, body: String): Boolean {
        val senderMatch = senderKeywords.any { sender.contains(it, ignoreCase = true) }
        if (senderMatch) return true

        return bodyKeywords.any { body.contains(it, ignoreCase = true) }
    }
}
