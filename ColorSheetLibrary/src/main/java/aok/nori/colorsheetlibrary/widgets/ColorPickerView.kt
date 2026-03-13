package aok.nori.colorsheetlibrary.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aok.nori.colorsheetlibrary.utils.isColorDark
import kotlin.math.*

/**
 * 色相環（リング）と彩度・明度（スクエア）を組み合わせて色を選択するカスタムビュー
 * HSVカラーモデルを基に描画および計算
 */
class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 色相環（リング）用の描画設定
    private val huePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    
    // 選択位置を示すインジケーター（白/黒の丸枠）用の描画設定
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    // HSVモデルの各値
    private var hue = 0f        // 色相：リング(0-360度)
    private var saturation = 1f // 彩度：スクエア(0.0-1.0)
    private var value = 1f      // 明度：スクエア(0.0-1.0)

    // 描画計算用の座標およびサイズ情報
    private var centerX = 0f     // ビューの中心X座標
    private var centerY = 0f     // ビューの中心Y座標
    private var radius = 0f      // 外側の半径
    private var innerRadius = 0f // 色相環（リング）の中心線の半径
    private var squareSize = 0f  // スクエアの一辺の長さ
    private val squareRect = RectF() // スクエアの描画範囲

    // 操作中の対象判定フラグ
    private var isInteractingRing = false
    private var isInteractingSquare = false

    // 色が変更された際に通知するリスナー
    private var onColorChanged: ((Int) -> Unit)? = null

    /**
     * 色変更リスナーを設定
     */
    fun setOnColorChangedListener(listener: (Int) -> Unit) {
        onColorChanged = listener
    }

    /**
     * 現在の選択色を設定
     */
    fun setColor(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
        invalidate()
    }

    /**
     * 現在の選択色をInt形式で取得
     */
    fun getColor(): Int {
        return Color.HSVToColor(floatArrayOf(hue, saturation, value))
    }

    /**
     * ビューのサイズが変更された際に、描画用の各座標やシェーダーを再計算
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        
        // ビューの大きさに合わせて半径を決定（95%サイズに調整）
        radius = min(w, h) / 2f * 0.95f
        val strokeWidth = radius * 0.15f // リングの太さ
        huePaint.strokeWidth = strokeWidth
        innerRadius = radius - strokeWidth / 2f
        
        // 色相環（リング）の内側に収まる彩度・明度選択用スクエアのサイズを計算
        val innerCircleRadius = innerRadius - strokeWidth / 2f
        squareSize = innerCircleRadius * sqrt(2f) * 0.95f

        // スクエアの描画範囲を確定
        squareRect.set(
            centerX - squareSize / 2f,
            centerY - squareSize / 2f,
            centerX + squareSize / 2f,
            centerY + squareSize / 2f
        )

        // 色相環（リング）用の360度スイープグラデーションを作成
        val colors = IntArray(361)
        for (i in 0..360) {
            colors[i] = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
        }
        huePaint.shader = SweepGradient(centerX, centerY, colors, null)
    }

    /**
     * ビュー描画処理
     */
    override fun onDraw(canvas: Canvas) {
        // 色相環（ヒューリング）を描画
        canvas.drawCircle(centerX, centerY, innerRadius, huePaint)

        // 中央の彩度・明度スクエア（サチュレーション・バリュースクエア）を描画
        drawSaturationValueSquare(canvas)

        // 選択されている色相（Hue）のインジケーターを描画
        val hueAngle = Math.toRadians(hue.toDouble())
        val hx = centerX + innerRadius * cos(hueAngle).toFloat()
        val hy = centerY + innerRadius * sin(hueAngle).toFloat()
        // 下地の色が暗い場合は白、明るい場合は黒の枠を表示して視認性を確保
        indicatorPaint.color = if (Color.HSVToColor(floatArrayOf(hue, 1f, 1f)).isColorDark()) Color.WHITE else Color.BLACK
        canvas.drawCircle(hx, hy, huePaint.strokeWidth / 2f + 2f, indicatorPaint)

        // 現在選択されている彩度・明度（Sat/Val）のインジケーターを描画
        val sx = squareRect.left + saturation * squareSize
        val sy = squareRect.top + (1f - value) * squareSize
        // 選択中の色が暗い場合は白、明るい場合は黒の枠を表示して視認性を確保
        indicatorPaint.color = if (getColor().isColorDark()) Color.WHITE else Color.BLACK
        canvas.drawCircle(sx, sy, 12f, indicatorPaint)
    }

    /**
     * 彩度と明度を表現するグラデーションスクエアを描画
     * 二つの線形グラデーションを重ね合わせることでHSVの平面を表現
     */
    private fun drawSaturationValueSquare(canvas: Canvas) {
        val hueColor = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        
        // 水平方向：左(白)から右(現在の色相)へのグラデーション（彩度）
        val satShader = LinearGradient(
            squareRect.left, squareRect.top, squareRect.right, squareRect.top,
            Color.WHITE, hueColor, Shader.TileMode.CLAMP
        )
        
        // 垂直方向：上(透明)から下(黒)へのグラデーション（明度）
        val valShader = LinearGradient(
            squareRect.left, squareRect.top, squareRect.left, squareRect.bottom,
            Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP
        )
        
        val paint = Paint()
        paint.shader = satShader
        canvas.drawRect(squareRect, paint)
        
        // 明度（Value）を重ねる（LinearGradientの合成によりHSV平面が完成）
        paint.shader = valShader
        canvas.drawRect(squareRect, paint)
    }

    /**
     * タッチイベント処理
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x - centerX
        val y = event.y - centerY
        val d = sqrt(x * x + y * y) // 中心からの距離

        when (event.action) {
            // タッチ開始
            MotionEvent.ACTION_DOWN -> {
                // リングとスクエアのどちらを操作するか決定
                isInteractingRing = d > innerRadius - huePaint.strokeWidth && d < innerRadius + huePaint.strokeWidth
                isInteractingSquare = squareRect.contains(event.x, event.y)

                // 操作中は親（BottomSheet 等）がイベントを奪わないように設定
                if (isInteractingRing || isInteractingSquare) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    updateValues(event.x, event.y, x, y)
                    return true
                }
            }
            // 移動
            MotionEvent.ACTION_MOVE -> {
                if (isInteractingRing || isInteractingSquare) {
                    // タッチ座標に基づきHue、Saturation、Valueの値を更新
                    updateValues(event.x, event.y, x, y)
                    return true
                }
            }
            // タッチ終了
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // フラグリセット
                isInteractingRing = false
                isInteractingSquare = false
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * タッチ座標に基づきHue、Saturation、Valueの値を更新
     * 操作開始時のターゲット（リングかスクエアか）を維持
     */
    private fun updateValues(eventX: Float, eventY: Float, relX: Float, relY: Float) {
        // 色相環（リング）操作
        if (isInteractingRing) {
            // 角度からHueを計算
            hue = (Math.toDegrees(atan2(relY.toDouble(), relX.toDouble())).toFloat() + 360) % 360
        // スクエア操作
        } else if (isInteractingSquare) {
            // 座標比率からSaturation(彩度)とValue(明度)を計算
            // 指がスクエアの外に出ても範囲内に収まるようクランプ（制限）をかける
            val constrainedX = eventX.coerceIn(squareRect.left, squareRect.right)
            val constrainedY = eventY.coerceIn(squareRect.top, squareRect.bottom)
            
            saturation = (constrainedX - squareRect.left) / squareSize
            value = 1f - (constrainedY - squareRect.top) / squareSize
        }
        
        if (isInteractingRing || isInteractingSquare) {
            invalidate()
            onColorChanged?.invoke(getColor())
        }
    }
}
