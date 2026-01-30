#ifndef KEYBOARDBUILDER_H
#define KEYBOARDBUILDER_H
#include "keyboard/I2CRegion.h"
#include "keyboard/SystemVIKeyboard.h"


class KeyboardBuilder
{
    int *columnPins, *rowPins;
    int columns, rows, reportedColumns, reportedRows;
    bool debugPrint;
    char* name;
    int sdaPin, sclPin;
    bool reportToI2C, readFromI2C;
    I2CRegion i2cRegion = I2CRegion(-1, -1, 0, 0);

public:
    KeyboardBuilder() :
        columnPins(nullptr),
        rowPins(nullptr),
        columns(-1),
        rows(-1),
        reportedColumns(-1),
        reportedRows(-1),
        debugPrint(false),
        name(nullptr),
        sdaPin(-1),
        sclPin(-1),
        reportToI2C(false),
        readFromI2C(false)
    {
    }

    KeyboardBuilder* setColumns(int columns, int* columnPins)
    {
        this->columns = columns;
        this->reportedColumns = columns;
        this->columnPins = columnPins;
        return this;
    }

    KeyboardBuilder* setRows(int rows, int* rowPins)
    {
        this->rows = rows;
        this->reportedRows = rows;
        this->rowPins = rowPins;
        return this;
    }

    KeyboardBuilder* setReportedColumns(int reportedColumns)
    {
        this->reportedColumns = reportedColumns;
        return this;
    }

    KeyboardBuilder* setReportedRows(int reportedRows)
    {
        this->reportedRows = reportedRows;
        return this;
    }

    KeyboardBuilder* setDebugPrint(bool debugPrint)
    {
        this->debugPrint = debugPrint;
        return this;
    }

    KeyboardBuilder* setName(char* name)
    {
        this->name = name;
        return this;
    }

    KeyboardBuilder* sendsToI2C(int sdaPin, int sclPin)
    {
        this->sdaPin = sdaPin;
        this->sclPin = sclPin;
        this->reportToI2C = true;
        return this;
    }

    KeyboardBuilder* readsFromI2C(int sdaPin, int sclPin, int startColumn, int startRow, int width, int height)
    {
        this->sdaPin = sdaPin;
        this->sclPin = sclPin;
        this->readFromI2C = true;
        this->i2cRegion.column = startColumn;
        this->i2cRegion.row = startRow;
        this->i2cRegion.width = width;
        this->i2cRegion.height = height;
        return this;
    }

    SystemVIKeyboard* build()
    {
        if (
            columns == -1 &&
            rows == -1 &&
            name == nullptr
            )
        {
            return nullptr;
        }
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
            this->i2cRegion
        );
    }
};

#endif //KEYBOARDBUILDER_H
