#ifndef I2CREGION_H
#define I2CREGION_H

class I2CRegion {
public:
    int column, row, width, height;
    I2CRegion(int column,int row, int width, int height) {
        this->column=column;
        this->row=row;
        this->width=width;
        this->height=height;
    }
};

#endif //I2CREGION_H
