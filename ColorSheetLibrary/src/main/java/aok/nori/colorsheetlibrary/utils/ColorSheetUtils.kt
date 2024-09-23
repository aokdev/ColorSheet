package aok.nori.colorsheetlibrary.utils

import androidx.annotation.ColorInt

object ColorSheetUtils {

    /**
     * 色の整数を16進文字列に変換
     *
     * @param color: 変換する色の整数
     * @return "#FFFFFF"形式
     */
    fun colorToHex(@ColorInt color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
}
