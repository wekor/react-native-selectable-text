cmake_minimum_required(VERSION 3.13)
set(CMAKE_VERBOSE_MAKEFILE ON)

# Naming the target react_codegen_<SpecName> intentionally takes over the
# build target that RN autolinking would otherwise wire to the codegen-default
# ConcreteViewShadowNode. With our custom CMakeLists declared via
# `react-native.config.js` (cmakeListsPath), the App's autolinking add_subdirectory()
# enters here and uses our SHARED library — which provides our custom
# SelectableTextShadowNode + the codegen-generated Props/EventEmitters/etc.
set(LIB_LITERAL SelectableTextViewSpec)
set(LIB_TARGET_NAME react_codegen_${LIB_LITERAL})

set(LIB_ANDROID_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../..)
set(LIB_ANDROID_GENERATED_JNI_DIR ${LIB_ANDROID_DIR}/build/generated/source/codegen/jni)
set(LIB_ANDROID_GENERATED_COMPONENTS_DIR ${LIB_ANDROID_GENERATED_JNI_DIR}/react/renderer/components/${LIB_LITERAL})

set(CPP_ROOT ${LIB_ANDROID_DIR}/../cpp)

file(GLOB LIB_CODEGEN_SRCS CONFIGURE_DEPENDS
  ${LIB_ANDROID_GENERATED_JNI_DIR}/*.cpp
  ${LIB_ANDROID_GENERATED_COMPONENTS_DIR}/*.cpp
)

file(GLOB LIB_CUSTOM_CPP_SRCS CONFIGURE_DEPENDS ${CPP_ROOT}/*.cpp)

add_library(
  ${LIB_TARGET_NAME}
  SHARED
  ${LIB_CODEGEN_SRCS}
  ${LIB_CUSTOM_CPP_SRCS}
)

target_include_directories(
  ${LIB_TARGET_NAME}
  PUBLIC
  # Our override of react/renderer/components/<SpecName>/ComponentDescriptors.h
  # has to be visible to the App's auto-generated OnLoad.cpp so it links against
  # our custom SelectableTextComponentDescriptor (not the codegen default).
  ${CMAKE_CURRENT_SOURCE_DIR}
  # Cross-platform shared C++ (SelectableTextShadowNode.h etc.)
  ${CPP_ROOT}
  # Codegen-generated headers (Props.h / EventEmitters.h / States.h / ShadowNodes.h).
  ${LIB_ANDROID_GENERATED_JNI_DIR}
)

find_package(fbjni REQUIRED CONFIG)
find_package(ReactAndroid REQUIRED CONFIG)

target_link_libraries(
  ${LIB_TARGET_NAME}
  fbjni::fbjni
  ReactAndroid::jsi
  ReactAndroid::reactnative
)

target_compile_reactnative_options(${LIB_TARGET_NAME} PRIVATE)
