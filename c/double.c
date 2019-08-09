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
        //printf("Passed: %f(%08lx)+%f(%x) should be %f(%08lx) but %f(%08lx) found.\n", a, *(long unsigned*)&a, b, *(long unsigned*)&b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
        assertionResult = 0;
    }
    else {
        printf("Error : %f(%08lx)+%f(%08lx) should be %f(%08lx) but %f(%08lx) found.\n", a, *(long unsigned*)&a, b, *(long unsigned*)&b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
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
    }
    else {
        printf("Error : %f*%f should be %f(%08lx) but %f(%08lx) found.\n", a, b, r, *(long unsigned*)&r, t, *(long unsigned*)&t);
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

int main(int argc, char const *argv[]) {
    //testAdd();
    testMul();
    return 0;
}
