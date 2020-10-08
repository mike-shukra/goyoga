package ru.yogago.goyoga.data

object AppConstants{
    const val BASE_URL = "https://yoga-go.ru/"
    const val PHOTO_URL = "https://yoga-go.ru/asana/"
    const val LOG_TAG: String = "myLog"
    const val LOG_TAG_BILLING: String = "billingLog"
    const val APP_TOKEN = "9aHKJkgjk9ajhuiK53DF7683hy35sgSjkasd3898jkasd8LHGf9"

    /**
     * P1W equates to one week,
     * P1M equates to one month,
     * P3M equates to three months,
     * P6M equates to six months,
     * P1Y equates to one year
     */
    fun formatPeriod(period: String, isIncludeSingularNumber: Boolean): String {
        if (period.count() < 3) return ""
        val isSingular = period[1] == '1'
        val unit = when (period[2]) {
            'W' -> if (isSingular) "week" else "weeks"
            'M' -> if (isSingular) "month" else "months"
            'Y' -> if (isSingular) "year" else "years"
            else -> ""
        }
        return if (isSingular && !isIncludeSingularNumber) unit else "${period[1]} $unit"
    }

}