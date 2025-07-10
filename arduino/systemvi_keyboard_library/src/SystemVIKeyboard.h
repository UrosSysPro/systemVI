#ifndef __SYSTEMVI_KEYBOARD__
#define __SYSTEMVI_KEYBOARD__
#include "Key.h"

class SystemVIKeyboard{
private:
	int *columnPins,*rowPins;
	int columns,rows;
	bool printKeyEventsToSerial;
	char* name;
	Key ***keys;
public:
	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins);
	void updateKeyState();
	void reportLayout();
	void setNormalKey();
	void setNormalKeyLayer();
	void processSerialCommands();
	void executeKeyboardEvents();
	void printKeyPressToSerial(int column,int row);
	void printKeyReleaseToSerial(int column,int row);
	void printName();
};

#endif
