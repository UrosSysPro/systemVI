#ifndef __SYSTEMVI_KEYBOARD__
#define __SYSTEMVI_KEYBOARD__
#include "I2CRegion.h"
#include "keys/Key.h"
#include "keycaps/Keycap.h"
#include "special_keys/LayerKeyPosition.h"
#include "special_keys/SnapTapPair.h"

class SystemVIKeyboard{
private:
	int *columnPins,*rowPins;
	int columns,rows,reportedColumns,reportedRows,layerKeyPositionCount,snapTapPairCount;
	bool printKeyEventsToSerial,debugPrint;
	char* name;
	LayerKeyPosition* layerKeyPositions;
	SnapTapPair* snapTapPairs;
public:
	Keycap ***keys;
	void updateKeyState();
	void setNormalKeycap(int column,int row,char* values,int physicalX,int physicalY,int width,int height,int paddingLeft,int paddingBottom);
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
	void serialMessage(char message[]);
	//storage
	void loadFromFlash();
	void saveToFlash();
	void eraceFlash();
	//layer keys
	void addLayerKeyPosition(int x,int y,int layer);
	void removeLayerKeyPosition(int x,int y);
	//snap tap keys
	void addSnapTapKeyPair(int column0,int row0,int column1,int row1);
	void removeSnapTapKeyPair(int column0,int row0);


	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins);
	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins,bool debugPrint,int reportedColumns,int reportedRows);
	SystemVIKeyboard(char* name, int columns,int rows, int* columnPins, int* rowPins,bool debugPrint,int reportedColumns,int reportedRows,int sdaPin,int sclPin, bool reportToI2C,bool readFromI2C,I2CRegion i2cRegion);
	void init(
		char* name,
		int columns,int rows, int* columnPins, int* rowPins,
		bool debugPrint,
		int reportedColumns,int reportedRows,
		int sdaPin,int sclPin, bool reportToI2C,bool readFromI2C,I2CRegion region
	);
	~SystemVIKeyboard();
};

#endif
