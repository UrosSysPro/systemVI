#ifndef __SYSTEMVI_KEYBOARD__
#define __SYSTEMVI_KEYBOARD__
#include "Key.h"
#include "Keycap.h"
#include "LayerKeyPosition.h"
#include "MacroKey.h"
#include "NormalKey.h"
#include "SnapTapPair.h"

class SystemVIKeyboard{
private:
	int *columnPins,*rowPins;
	int columns,rows,reportedColumns,reportedRows,layerKeyPositionCount,snapTapPairCount;
	bool printKeyEventsToSerial,debugPrint;
	char* name;
	Keycap ***keys;
	LayerKeyPosition* layerKeyPositions;
	SnapTapPair* snapTapPairs;
public:
	void updateKeyState();
	void setNormalKeycap(int column,int row,char* values,int physicalX,int physicalY,int width,int height);
	void setNormalKeycap(int column,int row,char* values,int physicalX,int physicalY);
	void setNormalKeycap(int column,int row,char* values);
	void setNormalKeycap(int column,int row, int layer, char value);
	void setLayers(int column,int row,Key* values);
	void setLayer(int column,int row,int layer,Key* values);
	void executeKeyboardEvents();
	void update();
	//serial api
	void processSerialCommands();
	void serialSetLayers();
	void serialSetLayer();
	void serialSetMacro();
	void serialAddLayerKeyPosition();
	void serialRemoveLayerKeyPosition();
	void serialAddSnapTapKeyPair();
	void serialRemoveSnapTapKeyPair();
	void printName();
	void printKeyPressToSerial(int column,int row);
	void printKeyReleaseToSerial(int column,int row);
	void reportLayout();
	void removeLayout();


	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins);
	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins,bool debugPrint,int reportedColumns,int reportedRows);
	void init(char* name, int columns,int rows, int* columnPins, int* rowPins,bool debugPrint,int reportedColumns,int reportedRows);
	~SystemVIKeyboard();
};

#endif
