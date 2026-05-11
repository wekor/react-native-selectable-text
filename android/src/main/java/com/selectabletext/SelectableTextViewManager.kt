package com.selectabletext

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.SelectableTextViewManagerInterface
import com.facebook.react.viewmanagers.SelectableTextViewManagerDelegate

@ReactModule(name = SelectableTextViewManager.NAME)
class SelectableTextViewManager : ViewGroupManager<SelectableTextView>(),
  SelectableTextViewManagerInterface<SelectableTextView> {
  private val mDelegate: ViewManagerDelegate<SelectableTextView>

  init {
    mDelegate = SelectableTextViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<SelectableTextView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): SelectableTextView {
    return SelectableTextView(context)
  }

  @ReactProp(name = "value")
  override fun setValue(view: SelectableTextView, value: String?) {
    view.setValue(value)
  }

  @ReactProp(name = "menuOptions")
  override fun setMenuOptions(view: SelectableTextView, menuOptions: ReadableArray?) {
    val options = if (menuOptions != null) {
      Array(menuOptions.size()) { i ->
        menuOptions.getString(i) ?: ""
      }
    } else {
      emptyArray()
    }
    view.setMenuOptions(options)
  }

  @ReactProp(name = "selectionColor", customType = "Color")
  override fun setSelectionColor(view: SelectableTextView, selectionColor: Int?) {
    view.setSelectionColor(selectionColor)
  }

  @ReactProp(name = "textColor", customType = "Color")
  override fun setTextColor(view: SelectableTextView, textColor: Int?) {
    view.setTextColor(textColor)
  }

  @ReactProp(name = "fontSize", defaultFloat = 0f)
  override fun setFontSize(view: SelectableTextView, fontSize: Float) {
    view.setFontSize(fontSize)
  }

  @ReactProp(name = "lineHeight", defaultFloat = 0f)
  override fun setLineHeight(view: SelectableTextView, lineHeight: Float) {
    view.setLineHeight(lineHeight)
  }

  @ReactProp(name = "letterSpacing", defaultFloat = 0f)
  override fun setLetterSpacing(view: SelectableTextView, letterSpacing: Float) {
    view.setLetterSpacing(letterSpacing)
  }

  @ReactProp(name = "fontFamily")
  override fun setFontFamily(view: SelectableTextView, fontFamily: String?) {
    view.setFontFamily(fontFamily)
  }

  @ReactProp(name = "fontWeight")
  override fun setFontWeight(view: SelectableTextView, fontWeight: String?) {
    view.setFontWeight(fontWeight)
  }

  @ReactProp(name = "fontStyle")
  override fun setFontStyle(view: SelectableTextView, fontStyle: String?) {
    view.setFontStyle(fontStyle)
  }

  @ReactProp(name = "textAlign")
  override fun setTextAlign(view: SelectableTextView, textAlign: String?) {
    view.setTextAlign(textAlign)
  }

  @ReactProp(name = "numberOfLines", defaultInt = 0)
  override fun setNumberOfLines(view: SelectableTextView, numberOfLines: Int) {
    view.setNumberOfLines(numberOfLines)
  }

  override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
    return MapBuilder.builder<String, Any>()
      .put("topSelection", MapBuilder.of("registrationName", "onSelection"))
      .build()
  }

  companion object {
    const val NAME = "SelectableTextView"
  }
}
