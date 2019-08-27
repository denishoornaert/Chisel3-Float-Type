package fpu

import chisel3._
import chisel3.util._

import float._
import double._

class FPU extends Module {
    val io = IO(new Bundle {
        val inputType = Input(UInt(1.W)) // 0: float & double: 1
        val operand   = Input(UInt(2.W)) // 0: mul, 1: add, 2: toUInt & 3: toSInt
        val operand1  = Input(UInt(64.W))
        val operand2  = Input(UInt(64.W))
        val result    = Output(UInt(64.W))
    })

    when(io.inputType.asBool) {
        val op1 = (io.operand1).asTypeOf(new Double)
        val op2 = (io.operand2).asTypeOf(new Double)

        val res = Wire(new Double)
        when(io.operand === 0.U) {
            res := op1+op2
        }
        .elsewhen(io.operand === 1.U) {
            res := op1*op2
        }
        .elsewhen(io.operand === 2.U) {
            res := (op1.toUInt).asTypeOf(new Double)
        }
        .otherwise{
            res := (op1.toSInt).asTypeOf(new Double)
        }
        io.result := res.asUInt
    }
    .otherwise {
        val op1 = (io.operand1).asTypeOf(new Float)
        val op2 = (io.operand2).asTypeOf(new Float)

        val res = Wire(new Float)
        when(io.operand === 0.U) {
            res := op1+op2
        }
        .elsewhen(io.operand === 1.U) {
            res := op1*op2
        }
        .elsewhen(io.operand === 2.U) {
            res := (op1.toUInt).asTypeOf(new Float)
        }
        .otherwise{
            res := (op1.toSInt).asTypeOf(new Float)
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
