# Chisel3 Float Type

The repository contains the source code implementing in Chisel3 a floating point representation compliant with ieee754. The objective being to use it in the designing of hardware accelerators or co-processors running alongside traditional CPUs (e.g. Xilinx Zynq & UltraScale, Intel Stratix), only the implementation of 32 bit float (```float```), 64 bit float (```double```) and 128 bit float (```long double```) are considered for the time being.

All  the details about how to use the library and its features are available on the project [wiki](https://github.com/denishoornaert/Chisel3-Float-Type/wiki).

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
- [x] Implementing casting methods such as ```toFloat``` and others
- [ ] Implementation of the division operator
- [x] Implementation of ```double```
- [ ] Implementation of ```long double```
- [ ] Addition logic optimisation
- [ ] Fix the rounding/precision issue
