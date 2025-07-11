#ifndef LAYER_KEY_POSITION
#define LAYER_KEY_POSITION

class LayerKeyPosition {
public:
    int column,row,layer;
    void reportSerial() {
        // byte message[4];
        // message[0]=(byte)'p';
        // message[1]=column;
        // message[2]=row;
        // message[3]='@';
        // Serial.write(message,4);
    }
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