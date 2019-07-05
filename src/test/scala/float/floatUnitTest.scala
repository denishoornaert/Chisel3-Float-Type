// See README.md for license details.

package float

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class FloatUnitTester(c: Float) extends PeekPokeTester(c) {
    // Methods reminder :
    // poke(c.io.x, value)   -> set value
    // expect(c.oi.y, value) -> assert an output
    // peek(c.io.z, value)   -> return True if both equal
    // step(x)               -> execute x clock cycles
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  *
  * testOnly example.test.FloatTester
  *
  * From a terminal shell use:
  *
  * sbt 'testOnly example.test.FloatTester'
  *
  */
class FloatTester extends ChiselFlatSpec {
  // Disable this until we fix isCommandAvailable to swallow stderr along with stdout
  private val backendNames = if(false && firrtl.FileUtils.isCommandAvailable(Seq("verilator"))) {
    Array("firrtl", "verilator")
  }
  else {
    Array("firrtl")
  }
  for ( backendName <- backendNames ) {
    "Float" should s"calculate proper greatest common denominator (with $backendName)" in {
      Driver(() => new Float, backendName) {
        c => new FloatUnitTester(c)
      } should be (true)
    }
  }

  "Basic test using Driver.execute" should "be used as an alternative way to run specification" in {
    iotesters.Driver.execute(Array(), () => new Float) {
      c => new FloatUnitTester(c)
    } should be (true)
  }

  "using --backend-name verilator" should "be an alternative way to run using verilator" in {
    if(backendNames.contains("verilator")) {
      iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Float) {
        c => new FloatUnitTester(c)
      } should be(true)
    }
  }

  "running with --is-verbose" should "show more about what's going on in your tester" in {
    iotesters.Driver.execute(Array("--is-verbose"), () => new Float) {
      c => new FloatUnitTester(c)
    } should be(true)
  }

  "running with --fint-write-vcd" should "create a vcd file from your test" in {
    iotesters.Driver.execute(Array("--fint-write-vcd"), () => new Float) {
      c => new FloatUnitTester(c)
    } should be(true)
  }
}
