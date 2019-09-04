#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define INF  0
#define NINF 1
#define aN   2
#define NaN  3

long unsigned identify(long unsigned f) {
    long unsigned sign = f>>63;
    long unsigned exponent = (f<<1)>>53;
    long unsigned mantissa = ((f<<12)>>12);
    if(exponent == 0x7ff) {
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

long unsigned absoluteValue(long int a) {
    if(a < 0) {
        a = -a;
    }
    return a;
}

long int toInt(long unsigned a, long unsigned s) {
    if(s) {
        return -a;
    }
    else {
        return a;
    }
}

double add(long unsigned a, long unsigned b) {
    // SETUP
    long unsigned res;
    // SPLIT
    long unsigned Asign = a>>63;
    long unsigned Aexponent = (a<<1)>>53;
    long unsigned Amantissa = ((a<<12)>>12)|0x0010000000000000;
    long unsigned Bsign = b>>63;
    long unsigned Bexponent = (b<<1)>>53;
    long unsigned Bmantissa = ((b<<12)>>12)|0x0010000000000000;
    long unsigned Aidentity = identify(a);
    long unsigned Bidentity = identify(b);
    if((Aidentity == INF & Bidentity == NINF)|(Bidentity == INF & Aidentity == NINF)) {
        res = 0xfff8000000000000;
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
        long unsigned Rsign = 0;
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
        long unsigned Rexponent;
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
        long unsigned Rmantissa = absoluteValue(toInt(Amantissa, Asign)+toInt(Bmantissa, Bsign));
        if((Rmantissa&0x0020000000000000) == 0x0020000000000000) {
            Rexponent = Rexponent+1;
            Rmantissa = (Rmantissa+1)>>1;
        }
        // TRUNCATION
        Rsign = Rsign&0x0000000000000001;
        Rexponent = Rexponent&0x00000000000007ff;
        Rmantissa = Rmantissa&0x000fffffffffffff;
        // CONCATENATION
        res = (Rsign<<63)|(Rexponent<<52)|(Rmantissa);
    }
    return *(double*)&res;
}

unsigned char assertAdd(double a, double b) {
    //printf("--------------------------------------\n");
    unsigned char assertionResult = 0;
    double r = a+b;
    double t = add(*(long unsigned*)&a, *(long unsigned*)&b);
    if((r == t)||((*(long unsigned*)&r) == (*(long unsigned*)&t))) {
        printf("Passed: %f(%016lx)*%f(%016lx) should be %f(%016lx) but %f(%016lx) found.\n", a, *(long unsigned*)&a, b, *(long unsigned*)&b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
        assertionResult = 0;
    }
    else {
        printf("Error : %f(%016lx)+%f(%016lx) should be %f(%016lx) but %f(%016lx) found.\n", a, *(long unsigned*)&a, b, *(long unsigned*)&b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
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
        double f1 = (double)rand()/RAND_MAX;
        double f2 = (double)rand()/RAND_MAX;
        count += assertAdd(f1, f2);
    }
    printf("%u tests failed\n", count);
}

double mul(long unsigned a, long unsigned b) {
    // SETUP
    long unsigned res;
    // SPLIT
    long unsigned Asign = a>>63;
    long unsigned Aexponent = (a<<1)>>53;
    long unsigned Amantissa = ((a<<12)>>12)|0x0010000000000000;
    long unsigned Bsign = b>>63;
    long unsigned Bexponent = (b<<1)>>53;
    long unsigned Bmantissa = ((b<<12)>>12)|0x0010000000000000;
    long unsigned Aidentity = identify(a);
    long unsigned Bidentity = identify(b);
    if(Aidentity == INF | Aidentity == NINF | Bidentity == INF | Bidentity == NINF) {
        res = 0x7ff0000000000000|((Asign^Bsign)<<63);
    }
    else if(Aidentity == NaN) {
        res = 0x7ff8000000000000|(Asign<<63);
    }
    else if(Bidentity == NaN) {
        res = 0x7ff8000000000000|(Bsign<<63);
    }
    else {
        // Sign
        long unsigned Rsign = Asign^Bsign;
        // Exponent
        long unsigned Rexponent = (Aexponent+Bexponent)? (Aexponent+Bexponent-1023):0;
        // Mantissa
        long unsigned Rmantissa = (((__uint128_t)Amantissa*(__uint128_t)Bmantissa)>>52);
        // UPDATE WHEN OVERFLOW
        if((Rmantissa&0x0020000000000000) == 0x0020000000000000) {
            Rexponent = Rexponent+1;
            Rmantissa = (Rmantissa+1)>>1;
        }
        // TRUNCATION
        Rsign = Rsign&0x0000000000000001;
        Rexponent = Rexponent&0x00000000000007ff;
        Rmantissa = Rmantissa&0x000fffffffffffff;
        // CONCATENATION
        res = (Rsign<<63)|(Rexponent<<52)|(Rmantissa);
    }
    return *(double*)&res;
}

unsigned char assertMul(double a, double b) {
    //printf("--------------------------------------\n");
    unsigned char assertionResult = 0;
    double r = a*b;
    double t = mul(*(long unsigned*)&a, *(long unsigned*)&b);
    if((r == t)||((*(long unsigned*)&r) == (*(long unsigned*)&t))) {
        assertionResult = 0;
        printf("Passed: %f(%016lx)*%f(%016lx) should be %f(%016lx) but %f(%016lx) found.\n", a, *(long unsigned*)&a, b, *(long unsigned*)&b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
    }
    else {
        printf("Error : %f(%016lx)*%f(%016lx) should be %f(%016lx) but %f(%016lx) found.\n", a, *(long unsigned*)&a, b, *(long unsigned*)&b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
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
        double f1 = (double)rand()/RAND_MAX;
        double f2 = (double)rand()/RAND_MAX;
        count += assertMul(f1, f2);
    }
    printf("%u tests failed\n", count);
}

double uint_to_double(long unsigned significand) {
    if ((significand >= 0x0020000000000000))
        return -1.0;
    else if(significand == 0)
        return 0.0;
    int shifts = 0;
    while ((significand & 0x0010000000000000) == 0) {
        significand <<= 1;
        shifts++;
    }
    printf("shifts = %i\n", shifts);
    long unsigned exponent = 1023 - shifts + 52;
    long unsigned merged = (exponent << 52) | (significand & 0x000fffffffffffff);
    return *(double*)&merged;
}

double int_to_double(int significand) {
    long unsigned sign = significand < 0;
    if(sign) {
        significand = ~(significand-1);
    }
    double res = uint_to_double(significand);
    long unsigned tmp = *(long unsigned*)&res;
    tmp = tmp|(sign<<63);
    return *(double*)&tmp;
}

unsigned char assertEqualDouble(double a, double b) {
    if(a != b) {
        printf("test failed: %f != %f\n", a, b);
    }
    return (a != b);
}
unsigned char assertEqualUnsigned(long unsigned a, long unsigned b) {
    if(a != b) {
        printf("test failed: %lu != %lu\n", a, b);
    }
    return (a != b);
}

unsigned char assertEqualInt(long int a, long int b) {
    if(a != b) {
        printf("test failed: %li != %li\n", a, b);
    }
    return (a != b);
}

void testUintToDouble() {
    printf("Test UInt -> Double\n");
    unsigned char count = 0;
    long unsigned t[6] = {0x6b8b4567, 0x327b23c6, 0x643c9869, 0x66334873, 0x74b0dc51, 0}; //{3, 0x6b8b4567327b23c6, 0x643c986966334873, 0x74b0dc5119495cff, 0x2ae8944a625558ec, 0x238e1f2946e87ccd};
    for (size_t i = 0; i < 6; i++) {
        double tmp = uint_to_double(t[i]);
        printf("%lx -> %lx\n", t[i], *(long unsigned*)&tmp);
        count += assertEqualDouble((double)t[i], uint_to_double(t[i]));
    }
    printf("%u tests failed\n", count);
}

void testIntToDouble() {
  printf("Test Int -> Double\n");
    unsigned char count = 0;
    long int t[2] = {3, -3};
    for (size_t i = 0; i < 2; i++) {
        double tmp = int_to_double(t[i]);
        printf("%lx -> %lx\n", t[i], *(long unsigned*)&tmp);
        count += assertEqualDouble((double)t[i], int_to_double(t[i]));
    }
    printf("%u tests failed\n", count);
}

long unsigned double_to_uint(double significand) {
    long unsigned value = *(long unsigned*)&significand;
    long unsigned sign = value>>63;
    long unsigned exponent = (value<<1)>>53;
    long unsigned mantissa = ((value<<12)>>12)|0x0010000000000000;
    int difference = 1023-exponent;
    mantissa = mantissa >> (52+difference);
    // fill mantissa with zeros on the left
    return mantissa;
}

void testDoubleToUint() {
    unsigned char count = 0;
    double t[5] = {2.0, 5.0, 0.5, 0.75, 1.5};
    for (size_t i = 0; i < 5; i++) {
        printf("%lx -> %lx\n", *(long unsigned*)&t[i], double_to_uint(t[i]));
        count += assertEqualUnsigned((long unsigned)t[i], double_to_uint(t[i]));
    }
    printf("%u tests failed\n", count);
}

int double_to_int(double significand) {
    unsigned char sign = significand < 0;
    long unsigned res = double_to_uint(significand);
    if(sign) {
        res = (~res)+1;
    }
    return *(int*)&res;
}

void testDoubleToInt() {
    unsigned char count = 0;
    double t[10] = {2.0, 5.0, 0.5, 0.75, 1.5, -2.0, -5.0, -0.5, -0.75, -1.5};
    for (size_t i = 0; i < 10; i++) {
        long int tmp = double_to_int(t[i]);
        printf("%lx -> %lx\n", *(long unsigned*)&t[i], *(long unsigned*)&tmp);
        count += assertEqualInt((long int)t[i], double_to_int(t[i]));
    }
    printf("%u tests failed\n", count);
}

int main(int argc, char const *argv[]) {
    //testAdd();
    //testMul();
    testUintToDouble();
    testIntToDouble();
    testDoubleToUint();
    testDoubleToInt();
    return 0;
}
