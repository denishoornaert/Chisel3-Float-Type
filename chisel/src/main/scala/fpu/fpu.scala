package fpu

import chisel3._

import float._

class FPU extends Module {
    val io = IO(new Bundle {
        val operand1 = Input(UInt(32.W))
        val operand2 = Input(UInt(32.W))
        val result   = Output(UInt(32.W))
    })

    val op1 = Float.init(io.operand1)
    val op2 = Float.init(io.operand2)

    val res = op1+op2
    io.result := res.asUInt

}

/*
 * Enable the generation of the FIRRTL and Verilog equivalents once called via :
 * sbt "runMain fpu.FPU"
 */
object VerilogGenerator extends App {
    chisel3.Driver.execute(args, () => new FPU)
}
