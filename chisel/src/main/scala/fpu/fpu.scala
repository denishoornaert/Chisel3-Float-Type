package fpu

import chisel3._

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

/*
 * Enable the generation of the FIRRTL and Verilog equivalents once called via :
 * sbt "runMain fpu.FPU"
 */
object VerilogGenerator extends App {
    chisel3.Driver.execute(args, () => new FPU)
}
