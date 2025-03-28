idf_component_register(
	SRCS 
	"main.cpp"
	"launcher.cpp"
	"lib/esp_hidd_prf_api.c"
	"lib/hid_dev.c"
	"lib/hid_device_le_prf.c"
	"lib/systemvi/Arduino.cpp"
	"lib/systemvi/Keyboard.cpp"
	"lib/systemvi/Bluetooth.cpp"
	"CustomProfile.cpp"
	INCLUDE_DIRS "." "lib/systemvi"
)

target_compile_options(${COMPONENT_LIB} PRIVATE -Wno-unused-const-variable)
