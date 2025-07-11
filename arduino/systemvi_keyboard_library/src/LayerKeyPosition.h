#ifndef LAYER_KEY_POSITION
#define LAYER_KEY_POSITION

class LayerKeyPosition {
public:
    int column,row,layer;
    LayerKeyPosition(int column, int row, int layer) {
        this->column=column;
        this->row=row;
        this->layer=layer;
    }
    LayerKeyPosition() {
        this->column=-1;
        this->row=-1;
        this->layer=-1;
    }
};

#endif