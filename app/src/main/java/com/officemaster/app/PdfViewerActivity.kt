package com.officemaster.app

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class PdfViewerActivity : Activity() {
    private var descriptor: ParcelFileDescriptor? = null
    private var renderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private lateinit var pageImage: ImageView
    private lateinit var pageLabel: TextView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private var pageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 22, 31)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(18, 22, 31))
            setPadding(16, 16, 16, 12)
        }
        root.addView(TextView(this).apply {
            text = "Просмотр PDF"
            textSize = 22f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, 8, 0, 16)
        })
        pageImage = ImageView(this).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setBackgroundColor(Color.rgb(38, 43, 53))
        }
        root.addView(pageImage, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))
        pageLabel = TextView(this).apply {
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.LTGRAY)
            setPadding(0, 12, 0, 12)
        }
        root.addView(pageLabel)
        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        previousButton = Button(this).apply {
            text = "Назад"
            isAllCaps = false
            setOnClickListener { showPage(pageIndex - 1) }
        }
        nextButton = Button(this).apply {
            text = "Вперёд"
            isAllCaps = false
            setOnClickListener { showPage(pageIndex + 1) }
        }
        controls.addView(previousButton, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        controls.addView(nextButton, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(controls)
        setContentView(root)

        val uri = intent.getParcelableExtra<Uri>(EXTRA_URI)
        if (uri == null) {
            Toast.makeText(this, "Не удалось получить PDF-файл", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        try {
            descriptor = contentResolver.openFileDescriptor(uri, "r")
            val fd = descriptor ?: throw IllegalStateException("Файл недоступен")
            renderer = PdfRenderer(fd)
            if (renderer?.pageCount == 0) throw IllegalStateException("PDF не содержит страниц")
            showPage(0)
        } catch (error: Exception) {
            Toast.makeText(this, "Не удалось открыть PDF: ${error.localizedMessage ?: "ошибка файла"}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showPage(index: Int) {
        val pdf = renderer ?: return
        if (index !in 0 until pdf.pageCount) return
        currentPage?.close()
        pageIndex = index
        val page = pdf.openPage(index)
        currentPage = page
        val width = (resources.displayMetrics.widthPixels - 32).coerceAtLeast(1)
        val scale = width.toFloat() / page.width.toFloat()
        val height = (page.height * scale).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        pageImage.setImageBitmap(bitmap)
        pageLabel.text = "Страница ${index + 1} из ${pdf.pageCount}"
        previousButton.isEnabled = index > 0
        nextButton.isEnabled = index < pdf.pageCount - 1
    }

    override fun onDestroy() {
        currentPage?.close()
        renderer?.close()
        descriptor?.close()
        super.onDestroy()
    }

    companion object { const val EXTRA_URI = "pdf_uri" }
}
