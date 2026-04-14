package org.burgas.bankspring.util

class RegularUtil {

    companion object {
        val PHONE_REGEX: Regex = Regex("^\\+[1-9]\\d{6,14}$")
        val PIN_REGEX: Regex = Regex("""^\d{4}$""")
    }
}