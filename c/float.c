#include <stdio.h>

void split(unsigned f) {
    unsigned sign = f>>31;
    unsigned exponent = (f<<1)>>24;
    unsigned mantissa = ((f<<9)>>9)|0x00800000;
    printf("%x -> %x %x %x\n", f, sign, exponent, mantissa);
}

unsigned absoluteValue(int a) {
    if(a < 0) {
        a = -a;
    }
    return a;
}

int toInt(unsigned a, unsigned s) {
    if(s) {
        return -a;
    }
    else {
        return a;
    }
}

float add(unsigned a, unsigned b) {
    // SPLIT
    unsigned Asign = a>>31;
    unsigned Aexponent = (a<<1)>>24;
    unsigned Amantissa = ((a<<9)>>9)|0x00800000;
    printf("A: %x %x %x\n", Asign, Aexponent, Amantissa);
    unsigned Bsign = b>>31;
    unsigned Bexponent = (b<<1)>>24;
    unsigned Bmantissa = ((b<<9)>>9)|0x00800000;
    printf("B: %x %x %x\n", Bsign, Bexponent, Bmantissa);
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
    printf("toInt(%i) -> %i\n", Amantissa, toInt(Amantissa, Asign));
    printf("toInt(%i) -> %i\n", Bmantissa, toInt(Bmantissa, Bsign));
    if((toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign)) > 0) {
        printf("Asign: %u, Bsign: %u\n", Asign, Bsign);
        printf("a > b --> %i > %i\n", toInt(Amantissa, Asign), toInt(Bmantissa, Bsign));
        printf("%u\n", Asign);
        Rsign = 0;
    }
    else if((toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign)) < 0) {
        printf("Asign: %u, Bsign: %u\n", Asign, Bsign);
        printf("a < b --> %i < %i\n", toInt(Amantissa, Asign), toInt(Bmantissa, Bsign));
        printf("%u\n", Bsign);
        Rsign = 1;
    }
    else {
        printf("HEIN?!\n");
        // TODO
    }
//    printf("A: %x %x %x\n", Asign, Aexponent, Amantissa);
//    printf("B: %x %x %x\n", Bsign, Bexponent, Bmantissa);
    // MANIPULATE
    unsigned Rexponent = Aexponent; // or either Bexponent;
    printf("%i + %i\n", toInt(Amantissa, Asign), toInt(Bmantissa, Bsign));
    unsigned Rmantissa = absoluteValue(toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign));
    printf("%i . %u\n", (toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign)), absoluteValue(toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign)));
    // TRUNCATION
//    printf("%x\n", Rsign);
    Rsign = Rsign&0x00000001;
//    printf("%x\n", Rexponent);
    Rexponent = Rexponent&0x000000ff;
//    printf("%x\n", Rmantissa);
    Rmantissa = Rmantissa&0x007fffff;
    // CONCATENATION
//    printf("imp: %x %x %x\n", Rsign, Rexponent, Rmantissa);
//  printf("Rs : %x -> %x\n", Rsign, (Rsign<<31));
//  printf("Re : %x -> %x\n", Rexponent, (Rexponent<<23));
    unsigned res = (Rsign<<31)|(Rexponent<<23)|(Rmantissa);
//    printf("res: %x %f\n", res, *(float*)&res);
    return *(float*)&res;
}

unsigned char assert(float a, float b) {
    printf("-----------------------------------\n");
    unsigned char assertionResult = 0;
    float r = a+b;
    float t = add(*(unsigned*)&a, *(unsigned*)&b);
    if(r != t) {
        printf("Error: %f+%f should be %f but %f found.\n", a, b, r, t);
        assertionResult = 1;
    }
    else {
        printf("Assertion ok !\n");
        assertionResult = 0;
    }
    return assertionResult;
}

int main(int argc, char const *argv[]) {
    unsigned char count = 0;
    count += assert(2.25, 134.0625);
    count += assert(134.0625, 2.25);
    count += assert(-2.25, 134.0625);
    count += assert(134.0625, -2.25);
    count += assert(2.25, -134.0625);
    count += assert(-134.0625, 2.25);
    count += assert(-2.25, -134.0625);
    count += assert(-134.0625, -2.25);
    printf("%u tests failed\n", count);
    return 0;
}
