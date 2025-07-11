#ifndef __SYSTEMVI_KEYBOARD__
#define __SYSTEMVI_KEYBOARD__
#include "Key.h"
#include "Keycap.h"
#include "MacroKey.h"
#include "NormalKey.h"

class SystemVIKeyboard{
private:
	int *columnPins,*rowPins;
	int columns,rows;
	bool printKeyEventsToSerial;
	char* name;
	Keycap ***keys;
public:
	void updateKeyState();
	void reportLayout();
	void setNormalKeycap(int column,int row,char* values);
	void setNormalKeycap(int column,int row, int layer, char value);
	void setLayers(int column,int row,Key* values);
	void setLayer(int column,int row,int layer,Key* values);
	void processSerialCommands();
	void executeKeyboardEvents();
	void printKeyPressToSerial(int column,int row);
	void printKeyReleaseToSerial(int column,int row);
	void printName();
	void update();
	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins);
	~SystemVIKeyboard();
};

#endif
