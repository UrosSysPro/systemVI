#ifndef SNAP_TAP_KEY
#define SNAP_TAP_KEY

class SnapTapKey {
public:
    int column,row;
    SnapTapKey() {
        column = row = -1;
    };
    SnapTapKey(int column,int row) {
        this->column = column;
        this->row = row;
    }
};

#endif