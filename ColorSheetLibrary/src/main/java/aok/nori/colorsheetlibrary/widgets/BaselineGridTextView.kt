package aok.nori.colorsheetlibrary.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.AppCompatTextView
import aok.nori.colorsheetlibrary.R

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
open class BaselineGridTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val fourDip: Float

    private var lineHeightMultiplierHint = 1f
    private var lineHeightHint = 0f
    private var maxLinesByHeight = false
    private var extraTopPadding = 0
    private var extraBottomPadding = 0

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BaselineGridTextView, defStyleAttr, 0
        )

        lineHeightMultiplierHint =
            a.getFloat(R.styleable.BaselineGridTextView_lineHeightMultiplierHint, 1f)
        lineHeightHint =
            a.getDimensionPixelSize(R.styleable.BaselineGridTextView_lineHeightHint, 0).toFloat()
        maxLinesByHeight = a.getBoolean(R.styleable.BaselineGridTextView_maxLinesByHeight, false)

        a.recycle()

        fourDip = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics
        )
        computeLineHeight()
    }

    // 最初の行をグリッド上に配置するためにパディングを追加
    override fun getCompoundPaddingTop(): Int =
        super.getCompoundPaddingTop() + extraTopPadding

    // 高さを 4dp の倍数にするためにパディングを追加
    override fun getCompoundPaddingBottom(): Int =
        super.getCompoundPaddingBottom() + extraBottomPadding

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        extraTopPadding = 0
        extraBottomPadding = 0

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var height = measuredHeight
        height += ensureBaselineOnGrid()
        height += ensureHeightGridAligned(height)
        setMeasuredDimension(measuredWidth, height)
        checkMaxLines(height, MeasureSpec.getMode(heightMeasureSpec))
    }

    /**
     * 行の高さを 4dp の倍数に設定
     */
    private fun computeLineHeight() {
        val fm = paint.fontMetricsInt
        val fontHeight = Math.abs(fm.ascent - fm.descent) + fm.leading
        val desiredLineHeight = if (lineHeightHint > 0)
            lineHeightHint
        else
            lineHeightMultiplierHint * fontHeight

        val baselineAlignedLineHeight =
            (fourDip * Math.ceil((desiredLineHeight / fourDip).toDouble()).toFloat()).toInt()

        setLineSpacing((baselineAlignedLineHeight - fontHeight).toFloat(), 1f)
    }

    /**
     * テキストの最初の行がグリッドから 4dp の倍数の位置に配置されるために必要なパディングを返す
     */
    private fun ensureBaselineOnGrid(): Int {
        val baseline = baseline.toFloat()
        val gridAlign = baseline % fourDip

        if (gridAlign != 0f) {
            extraTopPadding = (fourDip - Math.ceil(gridAlign.toDouble())).toInt()
        }

        return extraTopPadding
    }

    /**
     * 高さが 4dp の倍数であるために必要なパディングを返す
     */
    private fun ensureHeightGridAligned(height: Int): Int {
        val gridOverhang = height % fourDip

        if (gridOverhang != 0f) {
            extraBottomPadding = (fourDip - Math.ceil(gridOverhang.toDouble())).toInt()
        }

        return extraBottomPadding
    }

    /**
     * 正確な高さで測定するとテキストが行の途中で垂直に切り取られることがある。
     * これを防ぐため、使用可能なスペースに基づいて `maxLines` プロパティを設定する。
     */
    private fun checkMaxLines(height: Int, heightMode: Int) {
        if (!maxLinesByHeight || heightMode != MeasureSpec.EXACTLY) return

        val textHeight = height - compoundPaddingTop - compoundPaddingBottom
        val completeLines = Math.floor((textHeight / lineHeight).toDouble()).toInt()
        maxLines = completeLines
    }

    fun setLineHeightHint(lineHeightHint: Float) {
        this.lineHeightHint = lineHeightHint
        computeLineHeight()
    }
}
