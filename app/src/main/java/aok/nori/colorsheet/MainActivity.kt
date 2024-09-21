/*
 * ColorSheet
 * 取得：
 * [https://github.com/msasikanth/ColorSheet]
 */

package aok.nori.colorsheet

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import aok.nori.colorsheet.databinding.ActivityMainBinding
import aok.nori.colorsheetlibrary.ColorSheet
import aok.nori.colorsheetlibrary.utils.ColorSheetUtils

class MainActivity : AppCompatActivity() {
    companion object {
        private const val COLOR_SELECTED = "selectedColor"
        private const val NO_COLOR_OPTION = "noColorOption"
    }

    private lateinit var binding: ActivityMainBinding

    private var selectedColor: Int = ColorSheet.NO_COLOR
    private var noColorOption = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        val colors = resources.getIntArray(R.array.colors)
        selectedColor = savedInstanceState?.getInt(COLOR_SELECTED) ?: colors.first()
        setColor(selectedColor)

        noColorOption = savedInstanceState?.getBoolean(NO_COLOR_OPTION) ?: false

        binding.colorSheet.setOnClickListener {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
            ColorSheet().cornerRadius(8)
                .colorPicker(
                    colors = colors,
                    noColorOption = noColorOption,
                    selectedColor = selectedColor,
                    listener = { color ->
                        selectedColor = color
                        setColor(selectedColor)
                    })
                .show(supportFragmentManager)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                menu?.findItem(R.id.dark_mode)?.isChecked = false
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                menu?.findItem(R.id.dark_mode)?.isChecked = true
            }
        }
        menu?.findItem(R.id.no_color_option)?.isChecked = noColorOption

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dark_mode -> {
                if (item.isChecked) {
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                }
            }
            R.id.no_color_option -> {
                noColorOption = !noColorOption
                item.isChecked = noColorOption
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(COLOR_SELECTED, selectedColor)
        outState.putBoolean(NO_COLOR_OPTION, noColorOption)
    }

    private fun setColor(@ColorInt color: Int) {
        if (color != ColorSheet.NO_COLOR) {
            binding.colorBackground.setBackgroundColor(color)
            binding.colorSelectedText.text = ColorSheetUtils.colorToHex(color)
        } else {
            val primaryColor = ContextCompat.getColor(this, R.color.colorPrimary)
            binding.colorBackground.setBackgroundColor(primaryColor)
            binding.colorSelectedText.text = getString(R.string.no_color)
        }
    }
}
