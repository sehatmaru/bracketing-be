package xcode.bracketing.utils

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import java.util.*
import java.util.concurrent.TimeUnit

object CommonUtil {

    private const val EXPIRE_DURATION: Long = (24 * 60 * 60 * 1000).toLong()

    fun generateSecureId(): String {
        return UUID.randomUUID().toString()
    }

    fun encryptor(value: String?, isEncrypt: Boolean): String {
        val jasypt = StandardPBEStringEncryptor()

        jasypt.setPassword("xcode")

        return if (isEncrypt) jasypt.encrypt(value) else jasypt.decrypt(value)
    }

    fun getDifferenceDays(date1: Date, date2: Date): Long {
        val diff = date2.time - date1.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun setDateTime(date: Date?, hour: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = hour

        if (hour == 7) {
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
        } else {
            cal[Calendar.MINUTE] = 58
            cal[Calendar.SECOND] = 59
            cal[Calendar.MILLISECOND] = 999
        }

        return cal.time
    }

    fun stringToArray(requests: String): Array<String> {
        return requests.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    fun getTomorrowDate(): Date {
        return Date(System.currentTimeMillis() + EXPIRE_DURATION)
    }

}