package com.officemaster.app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.OpenableColumns
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var selectedLabel: TextView
    private var selectedUri: Uri? = null

    companion object { private const val REQUEST_OPEN_DOCUMENT = 1001 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderHome()
    }

    private fun renderHome() {
        val background = Color.rgb(18, 22, 31)
        val panel = Color.rgb(29, 36, 49)
        val accent = Color.rgb(58, 119, 230)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 64, 28, 28)
            setBackgroundColor(background)
        }
        root.addView(TextView(this).apply {
            text = "Office Mastermy"
            textSize = 30f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        root.addView(TextView(this).apply {
            text = "Документы и таблицы в одном месте"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            setPadding(0, 8, 0, 28)
        })
        root.addView(sectionTitle("Мои документы"))
        root.addView(actionButton("Открыть документ", accent) { chooseDocument() })
        root.addView(actionButton("Поделиться выбранным файлом", panel) { shareDocument() })
        root.addView(actionButton("Печать выбранного файла", panel) { printDocument() })
        selectedLabel = TextView(this).apply {
            text = "Файл не выбран"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 20, 0, 20)
        }
        root.addView(selectedLabel)
        root.addView(sectionTitle("Поддерживаемые форматы"))
        root.addView(TextView(this).apply {
            text = "Word: DOC, DOCX\nExcel: XLS, XLSX, CSV\nPowerPoint: PPT, PPTX\nPDF и текстовые файлы"
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
            setBackgroundColor(panel)
        })
        root.addView(sectionTitle("Режим работы"))
        root.addView(TextView(this).apply {
            text = "Файлы выбираются на устройстве. Для просмотра документ передаётся установленному совместимому приложению. Онлайн-редактор ONLYOFFICE подключается отдельным этапом после настройки Document Server и защищённого серверного обмена файлами."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 8, 0, 0)
        })
        root.addView(TextView(this).apply {
            text = "Версия 1.0.0 • Первый этап разработки"
            textSize = 12f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        })
        setContentView(root)
    }

    private fun sectionTitle(value: String) = TextView(this).apply {
        text = value
        textSize = 19f
        setTextColor(Color.WHITE)
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        setPadding(0, 22, 0, 12)
    }

    private fun actionButton(label: String, color: Int, action: () -> Unit) = Button(this).apply {
        text = label
        setTextColor(Color.WHITE)
        setBackgroundColor(color)
        isAllCaps = false
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 10 }
    }

    private fun chooseDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "text/plain", "text/csv"
            ))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_OPEN_DOCUMENT)
    }

    @Deprecated("Android document picker callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            selectedUri = uri
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { }
            selectedLabel.text = "Выбран файл: ${getDisplayName(uri)}"
            openSelectedDocument()
        }
    }

    private fun getDisplayName(uri: Uri): String {
        var name = "документ"
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = cursor.getString(index) ?: name
            }
        }
        return name
    }

    private fun openSelectedDocument() {
        val uri = selectedUri ?: return showMessage("Сначала выбери документ")
        val mimeType = contentResolver.getType(uri) ?: "*/*"
        if (mimeType == "application/pdf" || getDisplayName(uri).endsWith(".pdf", ignoreCase = true)) {
            startActivity(Intent(this, PdfViewerActivity::class.java).apply {
                putExtra(PdfViewerActivity.EXTRA_URI, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            })
            return
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, contentResolver.getType(uri) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(Intent.createChooser(intent, "Открыть документ"))
        } catch (_: ActivityNotFoundException) {
            showMessage("Установи совместимое приложение для просмотра этого формата")
        }
    }

    private fun shareDocument() {
        val uri = selectedUri ?: return showMessage("Сначала выбери документ")
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = contentResolver.getType(uri) ?: "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Поделиться документом"))
    }

    private fun printDocument() {
        val uri = selectedUri ?: return showMessage("Сначала выбери документ")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, contentResolver.getType(uri) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(Intent.createChooser(intent, "Открыть для печати"))
            showMessage("Печать выполняется через совместимое приложение")
        } catch (_: ActivityNotFoundException) {
            showMessage("Нет приложения, поддерживающего этот документ и печать")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
