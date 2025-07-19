cmake_minimum_required(VERSION 3.10)
project(Keyboards CXX)

#Arduino.h
#set(ARDUINO_SDK_PATH "/home/uros/.arduino15/packages/arduino/hardware/avr/1.8.6/cores/arduino")
#pins_arduino.h
#set(ARDUINO_VARIANT_PATH "/home/uros/.arduino15/packages/arduino/hardware/avr/1.8.6/variants/standard")
#Keyboard.h
#set(ARDUINO_KEYBOARD_LIB "/home/uros/.arduino15/libraries/Keyboard/src")
#set(ARDUINO_HID_LIB "/home/uros/.arduino15/packages/arduino/hardware/avr/1.8.6/libraries/HID/src")

add_executable(Keyboards
		src/NormalKey.cpp
		src/SystemVIKeyboard.h
		src/SystemVIKeyboard.cpp
		src/MacroKey.cpp
		src/Key.h
		#${ARDUINO_SDK_PATH}/Arduino.h
		#${ARDUINO_VARIANT_PATH}/pins_arduino.h
		#${ARDUINO_KEYBOARD_LIB}/Keyboard.h
		#${ARDUINO_HID_LIB}/HID.h
		src/Keycap.h
		src/Keycap.cpp
		src/NormalKey.h
		src/MacroKey.h
		src/SplitMain.h
		src/SplitPeripheral.h
)

#target_include_directories(Keyboards PUBLIC ${ARDUINO_SDK_PATH})
#target_include_directories(Keyboards PUBLIC  ${ARDUINO_VARIANT_PATH})
#target_include_directories(Keyboards PUBLIC ${ARDUINO_KEYBOARD_LIB})
#target_include_directories(Keyboards PUBLIC ${ARDUINO_HID_LIB})
