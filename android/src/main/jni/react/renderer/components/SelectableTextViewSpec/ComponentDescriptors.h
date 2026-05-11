#pragma once

#include "SelectableTextShadowNode.h"

#include <react/renderer/componentregistry/ComponentDescriptorProviderRegistry.h>

namespace facebook::react {

// SelectableTextComponentDescriptor is declared in SelectableTextShadowNode.h
// and is the type that App-side autolinking registers via
// `react-native.config.js` -> componentDescriptors.

// Backwards-compat alias so the codegen-generated ComponentDescriptors.cpp
// (which still references the default name) compiles against our custom
// shadow node. Both names resolve to the same ConcreteComponentDescriptor.
using SelectableTextViewComponentDescriptor =
    SelectableTextComponentDescriptor;

void SelectableTextViewSpec_registerComponentDescriptorsFromCodegen(
    std::shared_ptr<const ComponentDescriptorProviderRegistry> registry);

} // namespace facebook::react
