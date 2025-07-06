#ifndef __SYSTEMVI_KEY__
#define __SYSTEMVI_KEY__

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
	void onPress();
	void onRelease();
};

class MacroAction{
public: 

};

class MacroKey:public Key{
public:
	MacroKey();
	~MacroKey();
	void onPress();
	void onRelease();
};

#endif
