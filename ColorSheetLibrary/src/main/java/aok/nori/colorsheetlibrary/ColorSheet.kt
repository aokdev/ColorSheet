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

/**
 * ColorSheet Listener
 *
 * 選択された色を返却する。
 * noColorOption が有効でユーザーがオプションを選択した場合は [ColorSheet.NO_COLOR] を返却する。
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
    ): View {
        _binding = ColorSheetBinding.inflate(inflater, container, false)

        if (savedInstanceState != null) dismiss()

        return binding.root
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
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

        if (colorAdapter != null) {
            binding.colorSheetList.adapter = colorAdapter
        }

        binding.colorSheetClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        colorAdapter = null
        _binding = null
    }

    /**
     * シート上部の左隅と右上隅の角の半径を設定
     *
     * @param radius: float value
     */
    fun cornerRadius(radius: Float): ColorSheet {
        this.sheetCorners = radius

        return this
    }

    /**
     * シート上部の左隅と右上隅の角の半径を設定
     *
     * @param radius: float value
     */
    fun cornerRadius(radius: Int): ColorSheet {
        return cornerRadius(radius.toFloat())
    }

    /**
     * ColorSheet の設定
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

    /**
     * カラーシートを表示
     */
    fun show(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }
}
