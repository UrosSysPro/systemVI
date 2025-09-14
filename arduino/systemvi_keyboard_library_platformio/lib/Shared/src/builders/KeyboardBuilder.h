#ifndef KEYBOARDBUILDER_H
#define KEYBOARDBUILDER_H

class KeyboardBuilder {
private:
    int *columnPins,*rowPins;
    int columns,rows,reportedColumns,reportedRows;
    bool debugPrint;
    char *name;
public:
    KeyboardBuilder() {
        columnPins=nullptr;
        rowPins=nullptr;
        columns=-1;
        rows=-1;
        reportedColumns=-1;
        reportedRows=-1;
        debugPrint = false;
        name=nullptr;
    }
    
};

#endif //KEYBOARDBUILDER_H
