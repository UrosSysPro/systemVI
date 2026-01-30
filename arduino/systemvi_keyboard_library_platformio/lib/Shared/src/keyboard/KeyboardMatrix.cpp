#include "SystemVIKeyboard.h"
#include <cstring>
#include "Arduino.h"
#include "keycaps/Keycap.h"

void SystemVIKeyboard::update() {
    this->processSerialCommands();
    this->updateKeyState();
    this->executeKeyboardEvents();
}

void SystemVIKeyboard::updateKeyState() {
    for (int j = 0; j < this->rows; j++) {
        int rowPin = this->rowPins[j];
        digitalWrite(rowPin, 0);
        delayMicroseconds(1000);
        for (int i = 0; i < this->columns; i++) {
            int columnPin = columnPins[i];
            bool newState = digitalRead(columnPin) == 0;
            if (newState != keys[i][j]->pressed) {
                keys[i][j]->justChanged = true;
                keys[i][j]->pressed = newState;
            }
        }
        digitalWrite(rowPin, 1);
    }
}

int max(int a, int b) {
    if (a > b) return a;
    else return b;
}

void SystemVIKeyboard::executeKeyboardEvents() {
    int layer = 0;
    //process which layer is on
    for (int i = 0; i < this->layerKeyPositionCount; i++) {
        LayerKeyPosition p = this->layerKeyPositions[i];
        if (this->keys[p.column][p.row]->pressed) {
            layer = max(layer, p.layer);
        }
    }

    for (int i = 0; i < this->columns; i++) {
        for (int j = 0; j < this->rows; j++) {
            Keycap* key = this->keys[i][j];
            if (key->justChanged) {
                key->justChanged = false;
                if (key->pressed) {
                    for (int k = 0; k < this->snapTapPairCount; k++) {
                        SnapTapPair p = this->snapTapPairs[k];
                        Keycap* second = this->keys[p.second.column][p.second.row];
                        if (p.first.column == i && p.first.row == j && second->pressed) {
                            second->onRelease(layer);
                            break;
                        }
                    }
                    key->onPress(layer);
                    if (this->printKeyEventsToSerial)printKeyPressToSerial(i, j);
                    if (this->debugPrint)
                        Serial.printf("mDebug press column: %d row: %d columnPin: %d rowPin: %d\n@", i,
                                      j, this->columnPins[i], this->rowPins[j]);
                }
                else {
                    for (int k = 0; k < this->snapTapPairCount; k++) {
                        SnapTapPair p = this->snapTapPairs[k];
                        Keycap* second = this->keys[p.second.column][p.second.row];
                        if (p.first.column == i && p.first.row == j && second->pressed) {
                            second->onPress(layer);
                            break;
                        }
                    }
                    key->onRelease(layer);
                    if (this->printKeyEventsToSerial)printKeyReleaseToSerial(i, j);
                    if (this->debugPrint)
                        Serial.printf("mDebug release column: %d row: %d columnPin: %d rowPin: %d\n@",
                                      i, j, this->columnPins[i], this->rowPins[j]);
                }
            }
        }
    }
}

void SystemVIKeyboard::forEachJustPressedKey(std::function<void(int column, int row, int currentLayer, Keycap* keycap)> callback) {
    int layer = 0;
    //process which layer is on
    for (int i = 0; i < this->layerKeyPositionCount; i++) {
        LayerKeyPosition p = this->layerKeyPositions[i];
        if (this->keys[p.column][p.row]->pressed) {
            layer = max(layer, p.layer);
        }
    }

    for (int i = 0; i < this->columns; i++) {
        for (int j = 0; j < this->rows; j++) {
            bool isLayer = false;
            for (int i = 0; i < this->layerKeyPositionCount; i++) {
                LayerKeyPosition p = this->layerKeyPositions[i];
                if (p.column == i && p.row == j) {
                    isLayer = true;
                }
            }

            if (isLayer) continue;

            Keycap* key = this->keys[i][j];
            if (key->justChanged) {
                callback(i, j, layer, key);
            }
        }
    }
}

void SystemVIKeyboard::clearJustPressedKeyState() {
    for (int i = 0; i < this->columns; i++) {
        for (int j = 0; j < this->rows; j++) {
            this->keys[i][j]->justChanged = false;
        }
    }
}
