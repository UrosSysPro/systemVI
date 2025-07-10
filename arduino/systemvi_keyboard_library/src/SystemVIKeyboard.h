#ifndef __SYSTEMVI_KEYBOARD__
#define __SYSTEMVI_KEYBOARD__
#include "Key.h"

class SystemVIKeyboard{
private:
	int *columns,*rows;
	int numColumns,numRows;
	bool printKeyEventsToSerial;
	char* name;
	Key ***keys;
public:
	SystemVIKeyboard();
	void setKey();
	void setKeyOnLayer();
	void printKeyPressToSerial();
	void printKeyReleaseToSerial();
	void printName();
};

#endif
