package com.main.sankalan.data

/**
 * Data Class For LoggedINUserData.
 */

data class LoggedInUserView(
    val name: String = "",
    val mobile: String = "",
    var email: String = "",
    val institute: String = "",
    var isVerified: Boolean = false
) {
    var uid: String = ""
}
