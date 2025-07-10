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
	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins);
	void updateKeyState();
	void reportLayout();
	void setNormalKeycap(int column,int row,char* values);
	void setNormalKeycap(int column,int row, int layer, char value);
	void processSerialCommands();
	void executeKeyboardEvents();
	void printKeyPressToSerial(int column,int row);
	void printKeyReleaseToSerial(int column,int row);
	void printName();
};

#endif
