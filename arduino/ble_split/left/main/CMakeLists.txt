idf_component_register(
	SRCS 
	"lib/ble_hidd_demo_main.c"
	"main.c"
	"lib/esp_hidd_prf_api.c"
	"lib/hid_dev.c"
	"lib/hid_device_le_prf.c"
	"lib/systemvi/Arduino.cpp"
	"lib/systemvi/Keyboard.cpp"
	INCLUDE_DIRS "."
)

target_compile_options(${COMPONENT_LIB} PRIVATE -Wno-unused-const-variable)
