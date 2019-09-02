#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define INF  0
#define NINF 1
#define aN   2
#define NaN  3

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
            return NaN;
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
        else{
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
            Rmantissa = (Rmantissa+1)>>1;
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

unsigned char assertAdd(float a, float b) {
    //printf("--------------------------------------\n");
    unsigned char assertionResult = 0;
    float r = a+b;
    float t = add(*(unsigned*)&a, *(unsigned*)&b);
    if((r == t)||((*(unsigned*)&r) == (*(unsigned*)&t))) {
        //printf("Passed: %f(%08x)+%f(%x) should be %f(%08x) but %f(%08x) found.\n", a, *(unsigned*)&a, b, *(unsigned*)&b, r, *(unsigned*)&r, t, *(unsigned*)&t);
        assertionResult = 0;
    }
    else {
        printf("Error : %f(%08x)+%f(%08x) should be %f(%08x) but %f(%08x) found.\n", a, *(unsigned*)&a, b, *(unsigned*)&b, r, *(unsigned*)&r, t, *(unsigned*)&t);
        assertionResult = 1;
    }
    return assertionResult;
}

void testAdd() {
    unsigned char count = 0;
    // Static tests part (corner cases)
    count += assertAdd(2.25, 134.0625);
    count += assertAdd(134.0625, 2.25);
    count += assertAdd(-2.25, 134.0625);
    count += assertAdd(134.0625, -2.25);
    count += assertAdd(2.25, -134.0625);
    count += assertAdd(-134.0625, 2.25);
    count += assertAdd(-2.25, -134.0625);
    count += assertAdd(-134.0625, -2.25);
    count += assertAdd(3.14, -3.14);
    count += assertAdd(-3.14, 3.14);
    count += assertAdd(0.0, 0.0);
    count += assertAdd(-0.0, -0.0);
    count += assertAdd(NAN, 45.0);
    count += assertAdd(NAN, -45.0);
    count += assertAdd(-NAN, 45.0);
    count += assertAdd(-NAN, -45.0);
    count += assertAdd(INFINITY, 0.1);
    count += assertAdd(INFINITY, -0.1);
    count += assertAdd(-INFINITY, 0.1);
    count += assertAdd(-INFINITY, -0.1);
    count += assertAdd(INFINITY, INFINITY);
    count += assertAdd(INFINITY, -INFINITY);
    // Random tests
    for (size_t i = 0; i < 100; i++) {
        float f1 = (float)rand()/RAND_MAX;
        float f2 = (float)rand()/RAND_MAX;
        count += assertAdd(f1, f2);
    }
    printf("%u tests failed\n", count);
}

float mul(unsigned a, unsigned b) {
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
    if(Aidentity == INF | Aidentity == NINF | Bidentity == INF | Bidentity == NINF) {
        res = 0x7f800000|((Asign^Bsign)<<31);
    }
    else if(Aidentity == NaN) {
        res = 0x7fc00000|(Asign<<31);
    }
    else if(Bidentity == NaN) {
        res = 0x7fc00000|(Bsign<<31);
    }
    else {
        // Sign
        unsigned Rsign = Asign^Bsign;
        // Exponent
        unsigned Rexponent = (Aexponent+Bexponent)? (Aexponent+Bexponent-127):0;
        // Mantissa
        unsigned Rmantissa = (((unsigned long)Amantissa*(unsigned long)Bmantissa)>>23);
        // UPDATE WHEN OVERFLOW
        if((Rmantissa&0x01000000) == 0x01000000) {
            Rexponent = Rexponent+1;
            Rmantissa = (Rmantissa+1)>>1;
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

unsigned char assertMul(float a, float b) {
    //printf("--------------------------------------\n");
    unsigned char assertionResult = 0;
    float r = a*b;
    float t = mul(*(unsigned*)&a, *(unsigned*)&b);
    if((r == t)||((*(unsigned*)&r) == (*(unsigned*)&t))) {
        assertionResult = 0;
    }
    else {
        printf("Error : %f*%f should be %f(%08x) but %f(%08x) found.\n", a, b, r, *(unsigned*)&r, t, *(unsigned*)&t);
        assertionResult = 1;
    }
    return assertionResult;
}

void testMul() {
    unsigned char count = 0;
    // Static tests part (corner cases)
    count += assertMul(2.25, 134.0625);
    count += assertMul(134.0625, 2.25);
    count += assertMul(-2.25, 134.0625);
    count += assertMul(134.0625, -2.25);
    count += assertMul(2.25, -134.0625);
    count += assertMul(-134.0625, 2.25);
    count += assertMul(-2.25, -134.0625);
    count += assertMul(-134.0625, -2.25);
    count += assertMul(3.14, -3.14);
    count += assertMul(-3.14, 3.14);
    count += assertMul(0.0, 0.0);
    count += assertMul(-0.0, -0.0);
    count += assertMul(NAN, 45.0);
    count += assertMul(NAN, -45.0);
    count += assertMul(-NAN, 45.0);
    count += assertMul(-NAN, -45.0);
    count += assertMul(INFINITY, 0.1);
    count += assertMul(INFINITY, -0.1);
    count += assertMul(-INFINITY, 0.1);
    count += assertMul(-INFINITY, -0.1);
    count += assertMul(INFINITY, INFINITY);
    count += assertMul(INFINITY, -INFINITY);
    // Random tests
    for (size_t i = 0; i < 100; i++) {
        float f1 = (float)rand()/RAND_MAX;
        float f2 = (float)rand()/RAND_MAX;
        count += assertMul(f1, f2);
    }
    printf("%u tests failed\n", count);
}

float uint_to_float(unsigned int significand) {
    if (significand == 0 || significand >= 1 << 24)
        return -1.0;
    int shifts = 0;
    while ((significand & (1 << 23)) == 0) {
        significand <<= 1;
        shifts++;
    }
    unsigned int exponent = 127 - shifts + 23;
    unsigned int merged = (exponent << 23) | (significand & 0x7FFFFF);
    return *(float*)&merged;
}

float int_to_float(int significand) {
    unsigned char sign = significand < 0;
    if(sign) {
        significand = ~(significand-1);
    }
    float res = uint_to_float(significand);
    unsigned tmp = *(unsigned*)&res;
    tmp = tmp|(sign<<31);
    return *(float*)&tmp;
}

unsigned char assertEqualFloat(float a, float b) {
    if(a != b) {
        printf("test failed: %f != %f\n", a, b);
    }
    return (a != b);
}
unsigned char assertEqualUnsigned(unsigned a, unsigned b) {
    if(a != b) {
        printf("test failed: %u != %u\n", a, b);
    }
    return (a != b);
}

unsigned char assertEqualInt(int a, int b) {
    if(a != b) {
        printf("test failed: %i != %i\n", a, b);
    }
    return (a != b);
}

void testUintAndIntToFloat() {
    unsigned char count = 0;
    count += assertEqualFloat((float)3, uint_to_float(3));
    count += assertEqualFloat((float)3, int_to_float(3));
    count += assertEqualFloat((float)-3, int_to_float(-3));
    printf("%u tests failed\n", count);
}

unsigned float_to_uint(float significand) {
    unsigned value = *(unsigned*)&significand;
    unsigned sign = value>>31;
    unsigned exponent = (value<<1)>>24;
    unsigned mantissa = ((value<<9)>>9)|0x00800000;
    int difference = 127-exponent;
    mantissa = mantissa >> (23+difference);
    // fill mantissa with zeros on the left
    return mantissa;
}

void testFloatToUint() {
    unsigned char count = 0;
    float t[5] = {2.0f, 5.0f, 0.5f, 0.75f, 1.5f};
    for (size_t i = 0; i < 5; i++) {
        count += assertEqualUnsigned((unsigned)t[i], float_to_uint(t[i]));
    }
    printf("%u tests failed\n", count);
}

int float_to_int(float significand) {
    unsigned char sign = significand < 0;
    unsigned res = float_to_uint(significand);
    if(sign) {
        res = (~res)+1;
    }
    return *(int*)&res;
}

void testFloatToInt() {
    unsigned char count = 0;
    float t[10] = {2.0f, 5.0f, 0.5f, 0.75f, 1.5f, -2.0f, -5.0f, -0.5f, -0.75f, -1.5f};
    for (size_t i = 0; i < 10; i++) {
        count += assertEqualInt((int)t[i], float_to_int(t[i]));
    }
    printf("%u tests failed\n", count);
}

int main(int argc, char const *argv[]) {
    //testAdd();
    //testMul();
    testUintAndIntToFloat();
    testFloatToUint();
    testFloatToInt();
    return 0;
}
