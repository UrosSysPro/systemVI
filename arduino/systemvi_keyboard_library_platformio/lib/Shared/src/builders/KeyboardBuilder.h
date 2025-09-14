#ifndef KEYBOARDBUILDER_H
#define KEYBOARDBUILDER_H
#include "keyboard/SystemVIKeyboard.h"

class KeyboardBuilder {
    int *columnPins,*rowPins;
    int columns,rows,reportedColumns,reportedRows;
    bool debugPrint;
    char *name;
    int sdaPin,sclPin;
    bool reportToI2C,readFromI2C;
    int readI2CColumn,readI2CRow,readI2CWidth,readI2CHeight;
public:
    KeyboardBuilder() {
        this->columnPins = nullptr;
        this->rowPins = nullptr;
        this->columns = -1;
        this->rows = -1;
        this->reportedColumns = -1;
        this->reportedRows = -1;
        this->debugPrint = false;
        this->name = nullptr;
    }

    KeyboardBuilder* setColumns(int columns, int *columnPins) {
        this->columns = columns;
        this->reportedColumns = columns;
        this->columnPins = columnPins;
        return this;
    }
    KeyboardBuilder* setRows(int rows, int *rowPins) {
        this->rows = rows;
        this->reportedRows = rows;
        this->rowPins = rowPins;
        return this;
    }
    KeyboardBuilder* setReportedColumns(int reportedColumns) {
        this->reportedColumns=reportedColumns;
        return this;
    }
    KeyboardBuilder* setReportedRows(int reportedRows) {
        this->reportedRows=reportedRows;
        return this;
    }
    KeyboardBuilder* setDebugPrint(bool debugPrint) {
        this->debugPrint=debugPrint;
        return this;
    }
    KeyboardBuilder* setName(char *name) {
        this->name = name;
        return this;
    }
    KeyboardBuilder* sendsToI2C(int sdaPin,int sclPin) {
        this->sdaPin=sdaPin;
        this->sclPin=sclPin;
        this->reportToI2C=true;
        return this;
    }
    KeyboardBuilder* readsFromI2C(int sdaPin,int sclPin,int column,int row, int width,int height) {
        this->sdaPin=sdaPin;
        this->sclPin=sclPin;
        this->readFromI2C=true;
        this->readI2CRow=row;
        this->readI2CColumn=column;
        this->readI2CWidth=width;
        this->readI2CHeight=height;
        return this;
    }

    SystemVIKeyboard* build() {
        return new SystemVIKeyboard(
            this->name,
            this->columns,
            this->rows,
            this->columnPins,
            this->rowPins,
            this->debugPrint,
            this->reportedColumns,
            this->reportedRows,
            this->sdaPin,
            this->sclPin,
            this->reportToI2C,
            this->readFromI2C,
            this->readI2CColumn,
            this->readI2CRow,
            this->readI2CWidth,
            this->readI2CHeight
        );
    }
};

#endif //KEYBOARDBUILDER_H
