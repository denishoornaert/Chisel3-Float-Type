package fpu

import chisel3._
import chisel3.util._

import float._
import float.Float._
import double._
import double.Double._

class FPU extends Module {
    val io = IO(new Bundle {
        val inputType = Input(UInt(1.W)) // 0: float & double: 1
        val operand   = Input(UInt(3.W)) // 0: mul, 1: add, 2: asUInt, 3: asSInt, 4: toDouble (UInt) & 5: toDouble (SInt)
        val operand1  = Input(UInt(64.W))
        val operand2  = Input(UInt(64.W))
        val result    = Output(UInt(64.W))
    })

    when(io.inputType.asBool) {
        val op1 = (io.operand1).asTypeOf(new Double)
        val op2 = (io.operand2).asTypeOf(new Double)

        val res = Wire(new Double)
        res := op1 // default value if no match
        switch(io.operand) {
            is(0.U) { res := op1+op2 }
            is(1.U) { res := op1*op2 }
            is(2.U) { res := (op1.toUInt).asTypeOf(new Double) }
            is(3.U) { res := (op1.toSInt).asTypeOf(new Double) }
            is(4.U) { res := (op1.asUInt).toDouble }
            is(5.U) { res := (op1.asUInt.asSInt).toDouble }
        }
        io.result := res.asUInt
    }
    .otherwise {
        val op1 = (io.operand1).asTypeOf(new Float)
        val op2 = (io.operand2).asTypeOf(new Float)

        val res = Wire(new Float)
        res := op1 // default value if no match
        switch(io.operand) {
            is(0.U) { res := op1+op2 }
            is(1.U) { res := op1*op2 }
            is(2.U) { res := (op1.toUInt).asTypeOf(new Float) }
            is(3.U) { res := (op1.toSInt).asTypeOf(new Float) }
            is(4.U) { res := (op1.asUInt).toFloat }
            is(5.U) { res := (op1.asUInt.asSInt).toFloat }
        }
        io.result := Cat(0.U(32.W), res.asUInt)
    }

}

/*
 * Enable the generation of the FIRRTL and Verilog equivalents once called via :
 * sbt "runMain fpu.FPU"
 */
object VerilogGenerator extends App {
    chisel3.Driver.execute(args, () => new FPU)
}
