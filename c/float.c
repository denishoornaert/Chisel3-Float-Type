#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define INF  0
#define NINF 1
#define aN   2

unsigned identify(unsigned f) {
    unsigned sign = f>>31;
    unsigned exponent = (f<<1)>>24;
    unsigned mantissa = ((f<<9)>>9);
    if(exponent == 0xff) {
        if(mantissa == 0) {
            if(sign == 0) {
                return INF;
            }
            else {
                return NINF;
            }
        }
        else {
            return aN;
        }
    }
    else {
        return aN;
    }
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
    // SETUP
    unsigned res;
    // SPLIT
    unsigned Asign = a>>31;
    unsigned Aexponent = (a<<1)>>24;
    unsigned Amantissa = ((a<<9)>>9)|0x00800000;
    unsigned Bsign = b>>31;
    unsigned Bexponent = (b<<1)>>24;
    unsigned Bmantissa = ((b<<9)>>9)|0x00800000;
    unsigned Aidentity = identify(a);
    unsigned Bidentity = identify(b);
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

unsigned char assert(float a, float b) {
    //printf("--------------------------------------\n");
    unsigned char assertionResult = 0;
    float r = a+b;
    float t = add(*(unsigned*)&a, *(unsigned*)&b);
    if((r == t)||((*(unsigned*)&r) == (*(unsigned*)&t))) {
        assertionResult = 0;
    }
    else {
        printf("Error: %f+%f should be %f(0x%x) but %f(0x%x) found.\n", a, b, r, *(unsigned*)&r, t, *(unsigned*)&t);
        assertionResult = 1;
    }
    return assertionResult;
}

int main(int argc, char const *argv[]) {
    unsigned char count = 0;
    // Static tests part (corner cases)
    count += assert(2.25, 134.0625);
    count += assert(134.0625, 2.25);
    count += assert(-2.25, 134.0625);
    count += assert(134.0625, -2.25);
    count += assert(2.25, -134.0625);
    count += assert(-134.0625, 2.25);
    count += assert(-2.25, -134.0625);
    count += assert(-134.0625, -2.25);
    count += assert(3.14, -3.14);
    count += assert(-3.14, 3.14);
    count += assert(0.0, 0.0);
    count += assert(-0.0, -0.0);
    count += assert(NAN, 45.0);
    count += assert(NAN, -45.0);
    count += assert(-NAN, 45.0);
    count += assert(-NAN, -45.0);
    count += assert(INFINITY, 0.1);
    count += assert(INFINITY, -0.1);
    count += assert(-INFINITY, 0.1);
    count += assert(-INFINITY, -0.1);
    count += assert(INFINITY, INFINITY);
    count += assert(INFINITY, -INFINITY);
    // Random tests
    for (size_t i = 0; i < 100; i++) {
        float f1 = (float)rand()/RAND_MAX;
        float f2 = (float)rand()/RAND_MAX;
        count += assert(f1, f2);
    }
    printf("%u tests failed\n", count);
    return 0;
}
