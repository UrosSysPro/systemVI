package com.systemvi.examples.datastructures;

public class Sendvic {
    public boolean kecap,salata,senf,kupus,majonez,pavlaka,luk;
    public static class Builder{
        public boolean _kecap=false,_salata=false,_senf=false,_kupus=false,_majonez=false,_pavlaka=false,_luk=false;
        public Builder(){

        }
        public Builder kecap(){
            _kecap=true;
            return this;
        }
        public Builder salata(){
            _salata=true;
            return this;
        }
        public Builder senf(){
            _senf=true;
            return this;
        }
        public Builder kupus(){
            _kupus=true;
            return this;
        }
        public Builder majonez(){
            _majonez=true;
            return this;
        }
        public Builder pavlaka(){
            _pavlaka=true;
            return this;
        }
        public Builder luk(){
            _luk=true;
            return this;
        }
        public Sendvic build(){
            return new Sendvic(_kecap,_salata,_senf,_kupus,_majonez,_pavlaka,_luk);
        }
    }
    public static Builder builder(){
        return  new Builder();
    }
    private Sendvic(
        boolean kecap,
        boolean salata,
        boolean senf,
        boolean kupus,
        boolean majonez,
        boolean pavlaka,
        boolean luk
    ){
        this.kecap=kecap;
        this.salata=salata;
        this.senf=senf;
        this.kupus=kupus;
        this.majonez=majonez;
        this.pavlaka=pavlaka;
        this.luk=luk;
    }
}
