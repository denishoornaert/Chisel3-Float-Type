package float

import chisel3._

class Float extends Module {
    val io = IO(new Bundle {
//      val in = Input(UInt(X.W))
//      val out = Output(Bool())
    })

//  to fill...

}

/*
 * Enable the generation of the FIRRTL and Verilog equivalents once called via :
 * sbt "runMain float.Float"
 */
object VerilogGenerator extends App {
    chisel3.Driver.execute(args, () => new Float)
}
