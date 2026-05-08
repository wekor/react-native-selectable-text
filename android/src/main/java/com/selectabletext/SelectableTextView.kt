package com.selectabletext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Selection
import android.text.Spannable
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper

class SelectableTextView : FrameLayout {
  private var menuOptions: Array<String> = emptyArray()
  private var selectionColor: Int? = null
  private var textView: TextView? = null

  companion object {
    private const val MENU_ID_COPY = 10000
    private const val MENU_ID_SELECT_ALL = 10001
  }

  constructor(context: Context?) : super(context!!)
  constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context!!,
    attrs,
    defStyleAttr
  )

  fun setMenuOptions(options: Array<String>) {
    this.menuOptions = options
    setupTextView()
  }

  fun setSelectionColor(color: Int?) {
    textView?.highlightColor = color ?: 0x6633B5E5.toInt()
    selectionColor = color
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

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (changed && textView == null) {
      setupTextView()
    }
  }
}
