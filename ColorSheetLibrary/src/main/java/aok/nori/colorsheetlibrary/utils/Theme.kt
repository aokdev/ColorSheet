package aok.nori.colorsheetlibrary.utils

import android.content.Context
import androidx.annotation.StyleRes
import aok.nori.colorsheetlibrary.R

internal enum class Theme(@StyleRes val styleRes: Int) {

    LIGHT(R.style.BaseTheme_ColorSheet_Light),
    DARK(R.style.BaseTheme_ColorSheet_Dark);

    companion object {
        fun inferTheme(context: Context): Theme {
            val isPrimaryDark = resolveColorAttr(
                context = context,
                attrRes = android.R.attr.textColorPrimary
            ).isColorDark()

            return if (isPrimaryDark) LIGHT else DARK
        }
    }
}