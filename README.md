# Chisel3 Float Type

The repository contains the source code implementing in Chisel3 a floating point representation compliant with ieee754. The objective being to use it in the designing of hardware accelerators or co-processors running alongside traditional CPUs (e.g. Xilinx Zynq & UltraScale, Intel Stratix), only the implementation of 32 bit float (```float```), 64 bit float (```double```) and 128 bit float (```long double```) are considered for the time being.

## Utilisation

One can import the library easily as follows.
```scala
import float._
```
One as the choice of either instantiating a ```Wire``` or a ```Reg``` similarly to what is possible with built-in Chisel3 data-types.

```scala
val temp = Wire(new Float)
val result = Reg(new Float)
```
The initialisation of a float type is as follows.
```scala
val myFloat = Wire(new Float)
myFloat.sign := input(31)         // or directly a UInt(e.g. 1.U)
myFloat.exponent := input(30, 23) // or directly a UInt(e.g. 156.U)
myFloat.mantissa := input(22, 0)  // or directly a UInt(e.g. 2831.U)
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

Hereunder, one can find an example demonstrating the use of the current state of the project. Briefly, the example is an implementation of a simple *Float Processing Unit* (```FPU```) where the input ```io.operator``` specifies whether ```io.result``` will output the outcome of the multiplication or the addition of ```io.operand1``` and ```io.operand2```.
```scala
import float._

class FPU extends Module {
    val io = IO(new Bundle {
        val operator  = Input(UInt(1.W)) // 0: mul & 1: add
        val operand1 = Input(UInt(32.W))
        val operand2 = Input(UInt(32.W))
        val result   = Output(UInt(32.W))
    })

    val op1 = (io.operand1).asTypeOf(new Float)
    val op2 = (io.operand2).asTypeOf(new Float)

    val res = Wire(new Float)
    when(io.operator.asBool) {
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

### Launching tests

#### C
The ```C``` implementations are separated in two different files so one must each time compile both of them and then run them.
```bash
cd c/
gcc float.c -o testFloat.out
./testFloat.out
gcc double.c -o testDouble.out
./testDouble.out
```

#### Chisel
All the types and operations are included in the same testing process. Consequently, running the following line is sufficient.
```bash
cd chisel/
sbt "test:runMain fpu.FPUMain"
```

### Generate the package
The generation of the package can be done as follows:
```bash
cd chisel/
sbt "package"
cp target/scala-2.11/chisel3-float-type_2.11-1.0.jar ../
mv ../chisel3-float-type_2.11-1.0.jar ../chisel-float-type.jar
```

### Coming updates

- [x] Make the project an importable library
- [ ] Implementing casting methods such as ```asFloat```
- [ ] Implementation of the division operator
- [x] Implementation of ```double```
- [ ] Implementation of ```long double```
- [ ] Addition logic optimisation
- [ ] Fix the rounding/precision issue
