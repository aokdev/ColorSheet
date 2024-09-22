package aok.nori.colorsheetlibrary

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import aok.nori.colorsheetlibrary.utils.isColorDark
import aok.nori.colorsheetlibrary.utils.resolveColor
import aok.nori.colorsheetlibrary.utils.resolveColorAttr

internal class ColorAdapter(
    private val dialog: ColorSheet?,
    private var colors: IntArray,
    private val selectedColor: Int?,
    private val noColorOption: Boolean,
    private val listener: ColorPickerListener
) : RecyclerView.Adapter<ColorAdapter.ColorItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val color = inflater.inflate(R.layout.color_item, parent, false)

        return ColorItemViewHolder(color)
    }

    override fun getItemCount(): Int {
        return if (noColorOption) {
            colors.size + 1
        } else {
            colors.size
        }
    }

    override fun onBindViewHolder(holder: ColorItemViewHolder, position: Int) {
        holder.bindView()
    }

    inner class ColorItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val view: View = itemView

        private val circle by lazy {
            ContextCompat.getDrawable(itemView.context, R.drawable.ic_circle)
        }
        private val check by lazy {
            ContextCompat.getDrawable(itemView.context, R.drawable.ic_check)
        }
        private val noColor by lazy {
            ContextCompat.getDrawable(itemView.context, R.drawable.ic_no_color)
        }

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView() {
            if (noColorOption) {
                if (adapterPosition != 0) {
                    val color = colors[adapterPosition - 1]
                    bindColorView(color)
                } else {
                    bindNoColorView()
                }
            } else {
                val color = colors[adapterPosition]
                bindColorView(color)
            }
        }

        private fun bindColorView(@ColorInt color: Int) {
            val colorSelectedView = view.findViewById<ImageView>(R.id.colorSelected)
            val colorSelectedCircleView = view.findViewById<ImageView>(R.id.colorSelectedCircle)

            colorSelectedView.isVisible = selectedColor != null && selectedColor == color
            colorSelectedView.setImageResource(R.drawable.ic_check)

            if (color.isColorDark()) {
                colorSelectedView.imageTintList =
                    ColorStateList.valueOf(resolveColor(itemView.context, android.R.color.white))
            } else {
                colorSelectedView.imageTintList =
                    ColorStateList.valueOf(resolveColor(itemView.context, android.R.color.black))
            }

            colorSelectedCircleView.imageTintList = ColorStateList.valueOf(color)
        }

        private fun bindNoColorView() {
            val colorSelectedView = view.findViewById<ImageView>(R.id.colorSelected)
            val colorSelectedCircleView = view.findViewById<ImageView>(R.id.colorSelectedCircle)

            if (selectedColor != null && selectedColor == ColorSheet.NO_COLOR) {
                colorSelectedView.isVisible = true
                colorSelectedView.setImageDrawable(check)
            } else {
                colorSelectedView.isVisible = true
                colorSelectedView.setImageDrawable(noColor)
            }

            colorSelectedCircleView.background = circle
            colorSelectedCircleView.imageTintList = ColorStateList.valueOf(
                resolveColorAttr(itemView.context, attrRes = R.attr.dialogPrimaryVariant)
            )
        }

        override fun onClick(v: View?) {
            if (noColorOption) {
                if (adapterPosition == 0) {
                    listener?.invoke(ColorSheet.NO_COLOR)
                } else {
                    listener?.invoke(colors[adapterPosition - 1])
                }
            } else {
                listener?.invoke(colors[adapterPosition])
            }
            dialog?.dismiss()
        }
    }
}