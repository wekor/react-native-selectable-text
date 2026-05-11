package com.selectabletext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Selection
import android.text.Spannable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ActionMode
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper

class SelectableTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  private var menuOptions: Array<String> = emptyArray()
  private var selectionColor: Int? = null
  private val internalTextView = TextView(context)
  private var textView: TextView? = internalTextView
  private var defaultTextColor: Int = internalTextView.currentTextColor
  private var defaultTextSize: Float = internalTextView.textSize
  private var fontFamily: String? = null
  private var fontWeight: String? = null
  private var fontStyle: String? = null
  private var lineHeight: Float = 0f

  companion object {
    private const val MENU_ID_COPY = 10000
    private const val MENU_ID_SELECT_ALL = 10001
  }

  init {
    internalTextView.includeFontPadding = true
    internalTextView.setTextIsSelectable(true)
    addView(
      internalTextView,
      LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    )
    setupSelectionCallback(internalTextView)
  }

  fun setMenuOptions(options: Array<String>) {
    this.menuOptions = options
    setupTextView()
  }

  fun setSelectionColor(color: Int?) {
    textView?.highlightColor = color ?: 0x6633B5E5.toInt()
    selectionColor = color
  }

  fun setValue(value: String?) {
    internalTextView.text = value ?: ""
  }

  fun setTextColor(color: Int?) {
    internalTextView.setTextColor(color ?: defaultTextColor)
  }

  fun setFontSize(fontSize: Float) {
    if (fontSize > 0f) {
      internalTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    } else {
      internalTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize)
    }
    updateLineHeight()
  }

  fun setLineHeight(lineHeight: Float) {
    this.lineHeight = lineHeight
    updateLineHeight()
  }

  fun setLetterSpacing(letterSpacing: Float) {
    val textSize = internalTextView.textSize
    internalTextView.letterSpacing = if (textSize > 0f) {
      letterSpacing / textSize
    } else {
      0f
    }
  }

  fun setFontFamily(fontFamily: String?) {
    this.fontFamily = fontFamily
    updateTypeface()
  }

  fun setFontWeight(fontWeight: String?) {
    this.fontWeight = fontWeight
    updateTypeface()
  }

  fun setFontStyle(fontStyle: String?) {
    this.fontStyle = fontStyle
    updateTypeface()
  }

  fun setTextAlign(textAlign: String?) {
    val verticalGravity = internalTextView.gravity and Gravity.VERTICAL_GRAVITY_MASK
    val horizontalGravity = when (textAlign) {
      "center" -> Gravity.CENTER_HORIZONTAL
      "right" -> Gravity.RIGHT
      "auto",
      "left",
      null -> Gravity.LEFT
      else -> Gravity.LEFT
    }
    internalTextView.gravity = verticalGravity or horizontalGravity
  }

  fun setNumberOfLines(numberOfLines: Int) {
    if (numberOfLines > 0) {
      internalTextView.maxLines = numberOfLines
    } else {
      internalTextView.maxLines = Int.MAX_VALUE
    }
  }

  private fun setupTextView() {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      if (child is TextView) {
        textView = child
        setupSelectionCallback(child)
        break
      }
    }
  }

  private fun updateLineHeight() {
    if (lineHeight <= 0f) {
      internalTextView.setLineSpacing(0f, 1f)
      return
    }

    val lineHeightPx = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      lineHeight,
      resources.displayMetrics
    )
    val fontMetrics = internalTextView.paint.fontMetricsInt
    val fontHeight = fontMetrics.descent - fontMetrics.ascent
    internalTextView.setLineSpacing(lineHeightPx - fontHeight, 1f)
  }

  private fun updateTypeface() {
    val isItalic = fontStyle == "italic"
    val weight = parseFontWeight(fontWeight)
    val fallbackStyle = when {
      weight >= 600 && isItalic -> Typeface.BOLD_ITALIC
      weight >= 600 -> Typeface.BOLD
      isItalic -> Typeface.ITALIC
      else -> Typeface.NORMAL
    }
    val baseTypeface = if (fontFamily.isNullOrBlank()) {
      Typeface.DEFAULT
    } else {
      Typeface.create(fontFamily, Typeface.NORMAL)
    }

    internalTextView.typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      Typeface.create(baseTypeface, weight, isItalic)
    } else {
      Typeface.create(baseTypeface, fallbackStyle)
    }
  }

  private fun parseFontWeight(fontWeight: String?): Int {
    return when (fontWeight) {
      "bold" -> 700
      "normal",
      null -> 400
      else -> fontWeight.toIntOrNull() ?: 400
    }
  }

  private fun setupSelectionCallback(textView: TextView) {
    textView.setTextIsSelectable(true)
    selectionColor?.let { textView.highlightColor = it }
    textView.customSelectionActionModeCallback = object : ActionMode.Callback {
      override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
      }

      override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menu?.clear()
        menu?.add(0, MENU_ID_COPY, 0, context.getString(android.R.string.copy))
        menu?.add(0, MENU_ID_SELECT_ALL, 0, context.getString(android.R.string.selectAll))
        menuOptions.forEachIndexed { index, option ->
          menu?.add(0, index, 0, option)
        }
        return true
      }

      override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
          MENU_ID_COPY -> {
            val selectionStart = textView.selectionStart
            val selectionEnd = textView.selectionEnd
            val selectedText = textView.text.toString().substring(selectionStart, selectionEnd)
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(null, selectedText))
            mode?.finish()
          }
          MENU_ID_SELECT_ALL -> {
            Selection.selectAll(textView.text as Spannable)
          }
          else -> {
            val selectionStart = textView.selectionStart
            val selectionEnd = textView.selectionEnd
            val selectedText = textView.text.toString().substring(selectionStart, selectionEnd)
            val chosenOption = menuOptions[item?.itemId ?: 0]
            onSelectionEvent(chosenOption, selectedText)
            mode?.finish()
          }
        }
        return true
      }

      override fun onDestroyActionMode(mode: ActionMode?) {}
    }
  }

  private fun onSelectionEvent(chosenOption: String, highlightedText: String) {
    val reactContext = context as ReactContext
    val surfaceId = UIManagerHelper.getSurfaceId(this)
    val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)
    eventDispatcher?.dispatchEvent(OnSelectionEvent(surfaceId, id, chosenOption, highlightedText))
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    when (ev.actionMasked) {
      MotionEvent.ACTION_DOWN -> parent?.requestDisallowInterceptTouchEvent(true)
      MotionEvent.ACTION_UP,
      MotionEvent.ACTION_CANCEL -> parent?.requestDisallowInterceptTouchEvent(false)
    }
    return super.dispatchTouchEvent(ev)
  }

  override fun onViewAdded(child: View?) {
    super.onViewAdded(child)
    if (child is TextView) {
      textView = child
      setupSelectionCallback(child)
    }
  }

  override fun onViewRemoved(child: View?) {
    super.onViewRemoved(child)
    if (child === textView) {
      textView = null
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (textView == null) {
      setupTextView()
    }
  }
}
