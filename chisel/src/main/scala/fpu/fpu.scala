package fpu

import chisel3._
import chisel3.util._

import float32._
import float32.Float32._
import float64._
import float64.Float64._

class FPU extends Module {
    val io = IO(new Bundle {
        val inputType = Input(UInt(1.W)) // 0: float32 & float64: 1
        val operand   = Input(UInt(3.W)) // 0: mul, 1: add, 2: asUInt, 3: asSInt, 4: toFloat64 (UInt) & 5: toFloat64 (SInt)
        val operand1  = Input(UInt(64.W))
        val operand2  = Input(UInt(64.W))
        val result    = Output(UInt(64.W))
    })

    when(io.inputType.asBool) {
        val res = Wire(new Float64)
        res := io.operand1.asTypeOf(new Float64) // default value if no match
        switch(io.operand) {
            is(0.U) { res := (io.operand1.asTypeOf(new Float64))+(io.operand2.asTypeOf(new Float64)) }
            is(1.U) { res := (io.operand1.asTypeOf(new Float64))*(io.operand2.asTypeOf(new Float64)) }
            is(2.U) { res := (io.operand1.toUInt).asTypeOf(new Float64) }
            is(3.U) { res := (io.operand1.toSInt).asTypeOf(new Float64) }
            is(4.U) { res := (io.operand1.asUInt).toFloat64 }
            is(5.U) { res := (io.operand1.asUInt.asSInt).toFloat64 }
        }
        io.result := res.asUInt
    }
    .otherwise {
        val res = Wire(new Float32)
        res := io.operand1.asTypeOf(new Float32) // default value if no match
        switch(io.operand) {
            is(0.U) { res := (io.operand1.asTypeOf(new Float32))+(io.operand2.asTypeOf(new Float32)) }
            is(1.U) { res := (io.operand1.asTypeOf(new Float32))*(io.operand2.asTypeOf(new Float32)) }
            is(2.U) { res := (io.operand1.toUInt).asTypeOf(new Float32) }
            is(3.U) { res := (io.operand1.toSInt).asTypeOf(new Float32) }
            is(4.U) { res := (io.operand1.asUInt).toFloat32 }
            is(5.U) { res := (io.operand1.asUInt.asSInt).toFloat32 }
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
