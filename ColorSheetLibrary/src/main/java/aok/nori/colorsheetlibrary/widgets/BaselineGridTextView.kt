package aok.nori.colorsheetlibrary.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.AppCompatTextView
import aok.nori.colorsheetlibrary.R

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
open class BaselineGridTextView constructor(
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

    // TODO: 翻訳コメントを見直し
    // include extra padding to place the first line's baseline on the grid
    // 最初の行のベースラインをグリッド上に配置するために余分なパディングを含める
    override fun getCompoundPaddingTop(): Int =
        super.getCompoundPaddingTop() + extraTopPadding

    // TODO: 翻訳コメントを見直し
    // include extra padding to make the height a multiple of 4dp
    // 高さを4dpの倍数にするために余分なパディングを含める
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

    // TODO: 翻訳コメントを見直し
    /**
     * Ensures line height is a multiple of 4dp.
     */
    /**
     * 行の高さが 4dp の倍数であることを保証します。
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

    // TODO: 翻訳コメントを見直し
    /**
     * Ensure that the first line of text sits on the 4dp grid.
     */
    /**
     * テキストの最初の行が 4dp グリッド上に配置されていることを確認します。
     */
    private fun ensureBaselineOnGrid(): Int {
        val baseline = baseline.toFloat()
        val gridAlign = baseline % fourDip

        if (gridAlign != 0f) {
            extraTopPadding = (fourDip - Math.ceil(gridAlign.toDouble())).toInt()
        }

        return extraTopPadding
    }

    // TODO: 翻訳コメントを見直し
    /**
     * Ensure that height is a multiple of 4dp.
     */
    /**
     * 高さが 4dp の倍数であることを確認します。
     */
    private fun ensureHeightGridAligned(height: Int): Int {
        val gridOverhang = height % fourDip

        if (gridOverhang != 0f) {
            extraBottomPadding = (fourDip - Math.ceil(gridOverhang.toDouble())).toInt()
        }

        return extraBottomPadding
    }

    // TODO: 翻訳コメントを見直し
    /**
     * When measured with an exact height, text can be vertically clipped mid-line. Prevent
     * this by setting the `maxLines` property based on the available space.
     */
    /**
     * 正確な高さで測定すると、テキストが行の途中で垂直に切り取られることがあります。
     * これを防ぐには、使用可能なスペースに基づいて `maxLines` プロパティを設定します。
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
