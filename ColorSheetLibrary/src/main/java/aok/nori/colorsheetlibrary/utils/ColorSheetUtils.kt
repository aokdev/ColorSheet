package aok.nori.colorsheetlibrary.utils

import androidx.annotation.ColorInt

object ColorSheetUtils {

    // TODO: 翻訳コメントを見直し
    /**
     * Converts color int to hex string
     *
     * @param color: Color int to convert
     * @return Hex string in this format "#FFFFFF"
     */
    /**
     * 色の整数を16進文字列に変換します
     *
     * @param color: 変換する色の整数
     * @return "#FFFFFF"形式
     */
    fun colorToHex(@ColorInt color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
}
