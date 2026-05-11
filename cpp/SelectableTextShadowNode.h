#pragma once

#include <react/renderer/attributedstring/AttributedString.h>
#include <react/renderer/components/SelectableTextViewSpec/EventEmitters.h>
#include <react/renderer/components/SelectableTextViewSpec/Props.h>
#include <react/renderer/components/view/ConcreteViewShadowNode.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>
#include <react/renderer/core/LayoutContext.h>
#include <react/renderer/core/ShadowNode.h>
#include <react/renderer/textlayoutmanager/TextLayoutManager.h>

#include <memory>

#ifdef RN_SERIALIZABLE_STATE
#include <folly/dynamic.h>
#endif

namespace facebook::react {

extern const char SelectableTextViewComponentName[];

class SelectableTextStateData final {
 public:
  AttributedString attributedString;

  SelectableTextStateData() = default;

  explicit SelectableTextStateData(AttributedString attributedString)
      : attributedString(std::move(attributedString)) {}

#ifdef RN_SERIALIZABLE_STATE
  // Android (RN_SERIALIZABLE_STATE) requires state data to be constructible
  // from the previous state plus a folly::dynamic payload, and to expose
  // getDynamic(). Our state is owned entirely by the native side
  // (setStateData inside the ShadowNode), so the JS-driven update path is a
  // no-op — we just keep whatever the native side last produced.
  SelectableTextStateData(
      const SelectableTextStateData& previousState,
      folly::dynamic /*data*/)
      : attributedString(previousState.attributedString) {}

  folly::dynamic getDynamic() const {
    return folly::dynamic::object();
  }
#endif
};

class SelectableTextShadowNode final : public ConcreteViewShadowNode<
                                           SelectableTextViewComponentName,
                                           SelectableTextViewProps,
                                           SelectableTextViewEventEmitter,
                                           SelectableTextStateData> {
 public:
  using ConcreteViewShadowNode::ConcreteViewShadowNode;

  static ShadowNodeTraits BaseTraits() {
    auto traits = ConcreteViewShadowNode::BaseTraits();
    traits.set(ShadowNodeTraits::Trait::LeafYogaNode);
    traits.set(ShadowNodeTraits::Trait::MeasurableYogaNode);
    return traits;
  }

  void layout(LayoutContext layoutContext) override;

  Size measureContent(
      const LayoutContext& layoutContext,
      const LayoutConstraints& layoutConstraints) const override;

  void setTextLayoutManager(
      std::shared_ptr<const TextLayoutManager> textLayoutManager) {
    ensureUnsealed();
    textLayoutManager_ = std::move(textLayoutManager);
  }

 private:
  mutable AttributedString attributedString_;
  std::shared_ptr<const TextLayoutManager> textLayoutManager_;
};

// Owns a single TextLayoutManager per session (its TextMeasureCache /
// LineMeasureCache hugely cuts repeat measurement cost — e.g. FlashList
// recycling the same string at the same width should be a cache hit).
// Hands it to every ShadowNode created from this descriptor via adopt().
class SelectableTextComponentDescriptor final
    : public ConcreteComponentDescriptor<SelectableTextShadowNode> {
 public:
  SelectableTextComponentDescriptor(
      const ComponentDescriptorParameters& parameters)
      : ConcreteComponentDescriptor(parameters),
        textLayoutManager_(
            std::make_shared<const TextLayoutManager>(contextContainer_)) {}

  void adopt(ShadowNode& shadowNode) const override {
    ConcreteComponentDescriptor::adopt(shadowNode);
    auto& node = static_cast<SelectableTextShadowNode&>(shadowNode);
    node.setTextLayoutManager(textLayoutManager_);
  }

 private:
  const std::shared_ptr<const TextLayoutManager> textLayoutManager_;
};

} // namespace facebook::react
