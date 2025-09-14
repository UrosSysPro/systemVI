#ifndef KEYBOARDBUILDER_H
#define KEYBOARDBUILDER_H
#include "keyboard/SystemVIKeyboard.h"

class KeyboardBuilder {
    int *columnPins,*rowPins;
    int columns,rows,reportedColumns,reportedRows;
    bool debugPrint;
    char *name;
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
    }
    KeyboardBuilder* setRows(int rows, int *rowPins) {
        this->rows = rows;
        this->reportedRows = rows;
        this->rowPins = rowPins;
    }
    KeyboardBuilder* setReportedColumns(int reportedColumns) {
        this->reportedColumns=reportedColumns;
    }
    KeyboardBuilder* setReportedRows(int reportedRows) {
        this->reportedRows=reportedRows;
    }
    KeyboardBuilder* setDebugPrint(bool debugPrint) {
        this->debugPrint=debugPrint;
    }
    KeyboardBuilder* setName(char *name) {
        this->name = name;
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
            this->reportedRows
        );
    }
};

#endif //KEYBOARDBUILDER_H
