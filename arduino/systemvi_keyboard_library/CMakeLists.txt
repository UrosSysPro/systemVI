cmake_minimum_required(VERSION 3.10)
project(Keyboards CXX)

add_library(Keyboards STATIC
    src/*.cpp
    src/*.h
)

# Arduino core location (set this to your actual path!)
set(ARDUINO_SDK_PATH "/home/uros/.arduino15/packages/arduino/hardware/avr/1.8.6/cores/arduino")
set(ARDUINO_VARIANT_PATH "/home/uros/.arduino15/packages/arduino/hardware/avr/1.8.6/variants/standard")
set(ARDUINO_LIBS_PATH "/home/uros/.arduino15/libraries")

# Set include paths
target_include_directories(Keyboards PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/src
    ${ARDUINO_SDK_PATH}
    ${ARDUINO_VARIANT_PATH}
    ${ARDUINO_LIBS_PATH}/Keyboard/src
)
