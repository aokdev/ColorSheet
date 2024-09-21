/*
 * ColorSheet
 * 取得：
 * [https://github.com/msasikanth/ColorSheet]
 */

package aok.nori.colorsheetlibrary

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentManager
import aok.nori.colorsheetlibrary.databinding.ColorSheetBinding
import aok.nori.colorsheetlibrary.utils.Theme
import aok.nori.colorsheetlibrary.utils.resolveColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: 翻訳コメントを見直し
/**
 * Listener for color picker
 *
 * returns color selected from the sheet. If noColorOption is enabled and user selects the option,
 * it will return [ColorSheet.NO_COLOR]
 */
/**
 * ColorSheet Listener
 *
 * 選択された色を返却する。
 * noColorOption が有効でユーザーがオプションを選択した場合は[ColorSheet.NO_COLOR] を返却する。
 */
typealias ColorPickerListener = ((color: Int) -> Unit)?

class ColorSheet : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "ColorSheet"
        const val NO_COLOR = -1
    }

    private var _binding: ColorSheetBinding? = null
    private val binding get() = _binding!!

    private var sheetCorners: Float = 0f
    private var colorAdapter: ColorAdapter? = null

    override fun getTheme(): Int {
        return Theme.inferTheme(requireContext()).styleRes
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) dismiss()

        return inflater.inflate(R.layout.color_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val dialog = dialog as BottomSheetDialog? ?: return
                val behavior = dialog.behavior

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            dismiss()
                        }
                    }
                })
            }
        })

        if (sheetCorners == 0f) {
            sheetCorners = resources.getDimension(R.dimen.default_dialog_radius)
        }

        val gradientDrawable = GradientDrawable().apply {
            if (Theme.inferTheme(requireContext()) == Theme.LIGHT) {
                setColor(resolveColor(requireContext(), colorRes = R.color.dialogPrimary))
            } else {
                setColor(resolveColor(requireContext(), colorRes = R.color.dialogDarkPrimary))
            }

            cornerRadii =
                floatArrayOf(sheetCorners, sheetCorners, sheetCorners, sheetCorners, 0f, 0f, 0f, 0f)
        }
        view.background = gradientDrawable

        // TODO: 本当にこれで良いのか
        if (colorAdapter != null) {
            binding.colorSheetList.adapter = colorAdapter
//            colorSheetList.adapter = colorAdapter
        }

//        colorSheetClose.setOnClickListener {
        binding.colorSheetClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        colorAdapter = null
    }

    // TODO: 翻訳コメントを見直し
    /**
     * Set corner radius of sheet top left and right corners.
     *
     * @param radius: Takes a float value
     */
    /**
     * シート上部の左隅と右上隅の角の半径を設定します。
     *
     * @param radius: float value
     */
    fun cornerRadius(radius: Float): ColorSheet {
        this.sheetCorners = radius

        return this
    }

    // TODO: 翻訳コメントを見直し
    /**
     * Set corner radius of sheet top left and right corners.
     *
     * @param radius: Takes a float value
     */
    /**
     * シート上部の左隅と右上隅の角の半径を設定します。
     *
     * @param radius: float value
     */
    fun cornerRadius(radius: Int): ColorSheet {
        return cornerRadius(radius.toFloat())
    }

    // TODO: 翻訳コメントを見直し
    /**
     * Config color picker
     *
     * @param colors: Array of colors to show in color picker
     * @param selectedColor: Pass in the selected color from colors list, default value is null. You can pass [ColorSheet.NO_COLOR]
     * to select noColorOption in the sheet.
     * @param noColorOption: Gives a option to set the [selectedColor] to [NO_COLOR]
     * @param listener: [ColorPickerListener]
     */
    /**
     * カラーピッカーの設定
     *
     * @param colors: 表示する色の配列。
     * @param selectedColor: 色リストから選択した色を渡します。デフォルト値は null です。
     * シートで noColorOption を選択するには、[ColorSheet.NO_COLOR] を渡すことができます。
     * @param noColorOption: [selectedColor] を [NO_COLOR] に設定するオプションを指定します。
     * @param listener: [ColorPickerListener]
     */
    fun colorPicker(
        colors: IntArray,
        @ColorInt selectedColor: Int? = null,
        noColorOption: Boolean = false,
        listener: ColorPickerListener
    ): ColorSheet {
        colorAdapter = ColorAdapter(this, colors, selectedColor, noColorOption, listener)
        return this
    }

    // TODO: 翻訳コメントを見直し
    /**
     * Shows color sheet
     */
    /**
     * カラーシートを表示
     */
    fun show(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }
}
