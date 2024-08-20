package example.neoed.common.util

import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow

private val BUCKET_UNIT = "B"
private val SI_BASE = 1000
private val SI_POSTFIX: List<String> = "kMGTPE".fold(mutableListOf(BUCKET_UNIT)) { list, char -> list += (char + BUCKET_UNIT); list }
private val FMT = DecimalFormat("#.###")

data class ReadableNumber(val number: String, val unit: String) {
    companion object {
        fun from(mB: Int): ReadableNumber {
            if (mB < SI_BASE) {
                return ReadableNumber(mB.toString(), "m${BUCKET_UNIT}")
            }
            val exp = (ln(mB.toDouble()) / ln(SI_BASE.toDouble())).toInt()
            return ReadableNumber(FMT.format(mB / SI_BASE.toDouble().pow(exp.toDouble())), SI_POSTFIX[exp - 1])
        }

        fun format(mB: Int): String {
            if (mB < SI_BASE) {
                return "${mB}m${BUCKET_UNIT}"
            }
            val exp = (ln(mB.toDouble()) / ln(SI_BASE.toDouble())).toInt()
            return FMT.format(mB / SI_BASE.toDouble().pow(exp.toDouble())) + SI_POSTFIX[exp - 1]
        }
    }
}
