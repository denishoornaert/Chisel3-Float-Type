// See README.md for license details.

package fpu

import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class FPUUnitTester(c: FPU) extends PeekPokeTester(c) {
    // Methods reminder :
    // poke(c.io.x, value)   -> set value
    // expect(c.oi.y, value) -> assert an output
    // peek(c.io.z, value)   -> return True if both equal
    // step(x)               -> execute x clock cycles

    // dec. input      12.863000, -12.863000, 0.000000, 1.000000, -1.000000, 0.100000, -0.100000
    val input  = Array("h414dced9".U, "hc14dced9".U, "h00000000".U, "h3f800000".U, "hbf800000".U, "h3dcccccd".U, "hbdcccccd".U)
    // dec. output     -25.726000, 12.863000, 1.000000, -2.000000, 1.100000, -0.200000
    val output = Array("hc1cdced9".U, "h414dced9".U, "h3f800000".U, "hc0000000".U, "h3f8ccccd".U, "hbe4ccccd".U)

    for (i <- 1 to 6) {
        poke(c.io.operand1, input(i))
        poke(c.io.operand2, input(i-1))
        step(1)
        expect(c.io.result, output(i-1))
    }

}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  *
  * testOnly example.test.FPUTester
  *
  * From a terminal shell use:
  *
  * sbt 'testOnly example.test.FPUTester'
  *
  */
class FPUTester extends ChiselFlatSpec {
  // Disable this until we fix isCommandAvailable to swallow stderr along with stdout
  private val backendNames = if(false && firrtl.FileUtils.isCommandAvailable(Seq("verilator"))) {
    Array("firrtl", "verilator")
  }
  else {
    Array("firrtl")
  }
  for ( backendName <- backendNames ) {
    "FPU" should s"calculate proper greatest common denominator (with $backendName)" in {
      Driver(() => new FPU, backendName) {
        c => new FPUUnitTester(c)
      } should be (true)
    }
  }

  "Basic test using Driver.execute" should "be used as an alternative way to run specification" in {
    iotesters.Driver.execute(Array(), () => new FPU) {
      c => new FPUUnitTester(c)
    } should be (true)
  }

  "using --backend-name verilator" should "be an alternative way to run using verilator" in {
    if(backendNames.contains("verilator")) {
      iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new FPU) {
        c => new FPUUnitTester(c)
      } should be(true)
    }
  }

  "running with --is-verbose" should "show more about what's going on in your tester" in {
    iotesters.Driver.execute(Array("--is-verbose"), () => new FPU) {
      c => new FPUUnitTester(c)
    } should be(true)
  }

  "running with --fint-write-vcd" should "create a vcd file from your test" in {
    iotesters.Driver.execute(Array("--fint-write-vcd"), () => new FPU) {
      c => new FPUUnitTester(c)
    } should be(true)
  }
}
