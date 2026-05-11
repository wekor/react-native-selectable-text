#include "SelectableTextShadowNode.h"

#include <react/renderer/attributedstring/AttributedStringBox.h>
#include <react/renderer/attributedstring/ParagraphAttributes.h>
#include <react/renderer/attributedstring/TextAttributes.h>
#include <react/renderer/textlayoutmanager/TextLayoutContext.h>
#include <react/renderer/textlayoutmanager/TextLayoutManager.h>

namespace facebook::react {

namespace {

FontWeight resolveFontWeight(const std::string& weight) {
  if (weight == "bold" || weight == "700") {
    return FontWeight::Bold;
  }
  if (weight == "100") {
    return FontWeight::UltraLight;
  }
  if (weight == "200") {
    return FontWeight::Thin;
  }
  if (weight == "300") {
    return FontWeight::Light;
  }
  if (weight == "500") {
    return FontWeight::Medium;
  }
  if (weight == "600") {
    return FontWeight::Semibold;
  }
  if (weight == "800") {
    return FontWeight::Heavy;
  }
  if (weight == "900") {
    return FontWeight::Black;
  }
  return FontWeight::Regular;
}

FontStyle resolveFontStyle(const std::string& style) {
  if (style == "italic") {
    return FontStyle::Italic;
  }
  return FontStyle::Normal;
}

TextAlignment resolveTextAlignment(const std::string& align) {
  if (align == "center") {
    return TextAlignment::Center;
  }
  if (align == "right") {
    return TextAlignment::Right;
  }
  if (align == "left") {
    return TextAlignment::Left;
  }
  if (align == "justify") {
    return TextAlignment::Justified;
  }
  return TextAlignment::Natural;
}

} // namespace

Size SelectableTextShadowNode::measureContent(
    const LayoutContext& layoutContext,
    const LayoutConstraints& layoutConstraints) const {
  const auto& props = getConcreteProps();

  auto paragraphAttributes = ParagraphAttributes{};
  paragraphAttributes.maximumNumberOfLines = props.numberOfLines;
  paragraphAttributes.ellipsizeMode = EllipsizeMode::Tail;

  auto textAttributes = TextAttributes::defaultTextAttributes();
  textAttributes.allowFontScaling = true;
  const Float fontSizeMultiplier = layoutContext.fontSizeMultiplier;
  textAttributes.fontSize =
      (props.fontSize > 0 ? props.fontSize : 14.0) * fontSizeMultiplier;
  if (props.lineHeight > 0) {
    textAttributes.lineHeight = props.lineHeight * fontSizeMultiplier;
  }
  if (props.letterSpacing != 0) {
    textAttributes.letterSpacing = props.letterSpacing;
  }
  if (props.textColor) {
    textAttributes.foregroundColor = props.textColor;
  }
  if (!props.fontFamily.empty()) {
    textAttributes.fontFamily = props.fontFamily;
  }
  textAttributes.fontWeight = resolveFontWeight(props.fontWeight);
  textAttributes.fontStyle = resolveFontStyle(props.fontStyle);
  textAttributes.alignment = resolveTextAlignment(props.textAlign);

  auto fragment = AttributedString::Fragment{};
  fragment.string = props.value;
  fragment.textAttributes = textAttributes;

  auto attributedString = AttributedString{};
  attributedString.appendFragment(std::move(fragment));

  attributedString_ = attributedString;

  TextLayoutContext textLayoutContext{};
  textLayoutContext.pointScaleFactor = layoutContext.pointScaleFactor;

  return textLayoutManager_
      ->measure(
          AttributedStringBox{attributedString},
          paragraphAttributes,
          textLayoutContext,
          layoutConstraints)
      .size;
}

void SelectableTextShadowNode::layout(LayoutContext /*layoutContext*/) {
  ensureUnsealed();
  setStateData(SelectableTextStateData{attributedString_});
}

} // namespace facebook::react
