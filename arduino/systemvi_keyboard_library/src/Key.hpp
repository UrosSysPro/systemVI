#ifndef __SYSTEMVI_KEY__
#define __SYSTEMVI_KEY__

#include"Keyboard.h"

class Key{
public:
	Key();
	~Key();
	virtual void onPress();
	virtual void onRelease();
};

class NormalKey:public Key{
public:
	NormalKey();
	~NormalKey();
	void onPress() override;
	void onRelease() override;
};

class MacroAction{
public: 
	int value;

	MacroAction();
};

class MacroKey:public Key{
public:
	MacroKey();
	~MacroKey();
	void onPress();
	void onRelease();
};
void f(){
		
}

#endif
