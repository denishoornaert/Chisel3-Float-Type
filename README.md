# Chisel3-Float-Type

The repository contains the source code implementing in Chisel3 a floating point representation compliant with ieee754. The objective being to use it in the designing of hardware accelerators or co-processors running alongside traditional CPUs (e.g. Xilinx Zynq & UltraScale, Intel Stratix), only the implementation of 32 bit float (```float```), 64 bit float (```double```) and 128 bit float (```long double```) are considered for the time being.

## Utilisation

One can import the library easily as follows.
```scala
import float._
```
One as the choice of either instanciating a ```Wire``` or a ```Reg``` similarly to what is possible with built-in Chisel3 data-types.

```scala
val temp = Wire(new Float)
val result = Reg(new Float)
```
Alternatively, one can 'cast' a 32 bit unsigned integer.
```scala
val myFloat = (input).asTypeOf(new Float)
```
In addition, two operators are provided: the addition and the multiplication.
```scala
result := myFloat1+myFloat2
result := myFloat1*myFloat2
```

### Example

Hereunder, one can find an example demonstrating the use of the current state of the project. Briefly, the example is an implementation of a simple *Float Processing Unit* (```FPU```) where the input ```io.operand``` specifies whether ```io.result``` will output the outcome of the multiplication or the addition of ```io.operand1``` and ```io.operand2```.
```scala
import float._

class FPU extends Module {
    val io = IO(new Bundle {
        val operand  = Input(UInt(1.W)) // 0: mul & 1: add
        val operand1 = Input(UInt(32.W))
        val operand2 = Input(UInt(32.W))
        val result   = Output(UInt(32.W))
    })

    val op1 = (io.operand1).asTypeOf(new Float)
    val op2 = (io.operand2).asTypeOf(new Float)

    val res = Wire(new Float)
    when(io.operand.asBool) {
        res := op1+op2
    }
    .otherwise{
        res := op1*op2
    }
    io.result := res.asUInt

}
```

### Precision issue!

One must note that some precision issues have been observed when comparing the output (addition and multiplication) of the ```Chisel``` implementation with the output of the ```C``` version.

More accurately, the rounding error occurs whenever the mantissa overflows. According to the **iee754** logic, when it happens, the exponent is incremented and the mantissa must be shifted one step on the right. Doing so implies that a rounding error can occur in the case where the *Least Significant Bit* (LSB) is set to 1. For instance, take the following case:
```
0.103171*0.126075 = 0.013007
0x3dd34b6a * 0x3e0119e5 =  0x3c551cc0
```
The answer is ```0x3c551cc0```. However, ```0x3c551cbf``` is found. Luckily, in ```C``` it will still be printed ```0.013007```.

The current precision issue will only be fix provided that the rounding logic can be understand and emulated. Fortunately, this issue has minor consequences as it only impacts the value of the LSB, meaning that rounding errors are small.

### Comming updates

- [ ] Upload this library on a ```sbt``` server so that dependencies can be solved easily
- [ ] Implementing a ```asFloat``` casting method
- [ ] Implementation of the division operator
- [ ] Implmentation of ```double```
- [ ] Implmentation of ```long double```
- [ ] Addition logic optimisation
- [ ] Fix the rounding/precision issue
