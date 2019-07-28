package float

import chisel3._
import chisel3.util._

object Float {

    def init(input: UInt): Float = {
        val res = Wire(new Float())
        res.sign := input(31)
        res.exponent := input(30, 23)
        res.mantissa := input(22, 0)
        return res
    }

    def add(Float a, Float b): Float = {
        // SETUP
        val res = Wire(new Float())
        val Aidentity = identify(a)
        val Bidentity = identify(b)
        if((Aidentity == INF & Bidentity == NINF)|(Bidentity == INF & Aidentity == NINF)) {
            res = 0xffc00000;
        }
        else if(Aidentity == INF | Aidentity == NINF) {
            res = a;
        }
        else if(Bidentity == INF | Bidentity == NINF) {
            res = b;
        }
        else {
            // SHIFT EXPONENT
            int difference = Aexponent-Bexponent;
            unsigned Rsign = 0;
            // if Aexp>Bexp
            if(Aexponent > Bexponent) {
                Bexponent = Aexponent;
                Bmantissa = Bmantissa>>difference;
            }
            // if Bexp>Aexp
            else if(Aexponent < Bexponent) {
                Aexponent = Bexponent;
                Amantissa = Amantissa>>absoluteValue(difference);
            }
            unsigned Rexponent;
            if((toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign)) > 0) {
                Rsign = 0;
                Rexponent = Aexponent; // or either Bexponent;
            }
            else if((toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign)) < 0) {
                Rsign = 1;
                Rexponent = Aexponent; // or either Bexponent;
            }
            else {
                Rsign = 0;
                Rexponent = 0; // or either Bexponent;
            }
            // MANIPULATE
            unsigned Rmantissa = absoluteValue(toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign));
            if((Rmantissa&0x01000000) == 0x01000000) {
                Rexponent = Rexponent+1;
                if((Rmantissa&0x00000001) == 0x00000001) {
                    Rmantissa = Rmantissa+1;
                }
                Rmantissa = Rmantissa>>1;
            }
            // TRUNCATION
            Rsign = Rsign&0x00000001;
            Rexponent = Rexponent&0x000000ff;
            Rmantissa = Rmantissa&0x007fffff;
            // CONCATENATION
            res = (Rsign<<31)|(Rexponent<<23)|(Rmantissa);
        }
        return *(float*)&res;
    }

}

class Float() extends Bundle {

    val sign = Bool()
    val exponent = UInt(8.W)
    val mantissa = UInt(23.W)

}
