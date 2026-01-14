package com.example.seniorantiscam

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val KEY_LANGUAGE = "app_language" // "en" or "hi"
    }

    private lateinit var prefs: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        val p = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lang = p.getString(KEY_LANGUAGE, "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)

        // Delay adding the toggle until decorView is ready so child setContentView runs first.
        window.decorView.post {
            addLanguageToggle()
        }
    }

    private fun addLanguageToggle() {
        val root = window.decorView as ViewGroup

        // Prevent duplicates
        val existing = root.findViewWithTag<Button>("lang_toggle_button")
        if (existing != null) return

        val lang = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

        val btn = Button(this)
        btn.tag = "lang_toggle_button"
        btn.setBackgroundColor(Color.parseColor("#1976D2"))
        btn.setTextColor(Color.WHITE)
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        btn.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8))
        btn.text = if (lang == "en") "हिंदी" else "English"

        val lp = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 0, dpToPx(16), dpToPx(24))

        // Add to root; attempt to position at bottom-end
        root.addView(btn, lp)
        try {
            // try to set gravity if supported
            val layoutParams = btn.layoutParams
            val field = layoutParams.javaClass.getDeclaredField("gravity")
            field.isAccessible = true
            field.set(layoutParams, Gravity.BOTTOM or Gravity.END)
            btn.layoutParams = layoutParams
        } catch (_: Exception) {
            // ignore if parent doesn't support gravity field
        }

        btn.setOnClickListener {
            val current = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
            val newLang = if (current == "en") "hi" else "en"
            prefs.edit().putString(KEY_LANGUAGE, newLang).apply()
            btn.text = if (newLang == "en") "हिंदी" else "English"
            recreate()
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}