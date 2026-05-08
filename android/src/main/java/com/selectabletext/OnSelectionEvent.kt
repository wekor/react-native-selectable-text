package com.selectabletext

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class OnSelectionEvent(
  surfaceId: Int,
  viewTag: Int,
  private val chosenOption: String,
  private val highlightedText: String
) : Event<OnSelectionEvent>(surfaceId, viewTag) {

  override fun getEventName(): String = "topSelection"

  override fun getEventData(): WritableMap {
    return Arguments.createMap().apply {
      putString("chosenOption", chosenOption)
      putString("highlightedText", highlightedText)
    }
  }
}
