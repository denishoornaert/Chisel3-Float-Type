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

    val input1 = Array("h40100000".U, "h43061000".U, "hc0100000".U, "h43061000".U, "h40100000".U, "hc3061000".U, "hc0100000".U, "hc3061000".U, "h4048f5c3".U, "hc048f5c3".U, "h00000000".U, "h80000000".U, "h7fc00000".U, "h7fc00000".U, "hffc00000".U, "hffc00000".U, "h7f800000".U, "h7f800000".U, "hff800000".U, "hff800000".U, "h7f800000".U, "h7f800000".U, "h3f57168b".U, "h3f487931".U, "h3f6961b9".U, "h3eaba251".U, "h3e8e387d".U, "h3ef46d63".U, "h3ebac508".U, "h3f73c554".U, "h3f22be01".U, "h3e110043".U, "h3c8588c7".U, "h3e0c866f".U, "h3e20707c".U, "h3e04e7c9".U, "h3f7fb984".U, "h3f034f8a".U, "h3f1cd5f7".U, "h3f2336a0".U, "h3efcb6e9".U, "h3e95c4c3".U, "h3f06d8c2".U, "h3ecceac4".U, "h3e910ea1".U, "h3f4ec709".U, "h3d8edbdb".U, "h3f06a7a2".U, "h3e44d3b5".U, "h3f63e649".U, "h3d836c41".U, "h3eea57e2".U, "h3e73ffa9".U, "h3f66f71c".U, "h3e88886a".U, "h3ec01b21".U, "h3f033585".U, "h3f08175c".U, "h3ee01209".U, "h3f6e498d".U, "h3e918ee8".U, "h3f23d5a7".U, "h3f3017af".U, "h3ee15561".U, "h3f544686".U, "h3e6a76a2".U, "h3eb36269".U, "h3f74db1a".U, "h3f284514".U, "h3ee10dff".U, "h3ecbffe4".U, "h3f2f28f2".U, "h3ef70904".U, "h3f7343bd".U, "h3e173430".U, "h3f241ddc".U, "h3f1e9de0".U, "h3f49376f".U, "h3ee4e196".U, "h3e4008ae".U, "h3f0e7319".U, "h3e2dad7b".U, "h3dd34b6a".U, "h3efdaad8".U, "h3f7c18af".U, "h3f2f37ca".U, "h3f3ff0fc".U, "h3e969c30".U, "h3f15a10a".U, "h3e1c0c12".U, "h3e007c7e".U, "h3e280a56".U, "h3d98a314".U, "h3d5728ec".U, "h3e347091".U, "h3f4c3c7e".U, "h3f28148e".U, "h3f23b38b".U, "h3dbf72b1".U, "h3f052c7d".U, "h3d8f2b16".U, "h3eec3f4e".U, "h3f12c502".U, "h3d54bdcc".U, "h3f7fff94".U, "h3f63d422".U, "h3f7f6fc1".U, "h3f5edbb3".U, "h3b885e1b".U, "h3f180951".U, "h3e270bf2".U, "h3f69bc1e".U, "h3eb7db59".U, "h3f145586".U, "h3f2ff89f".U, "h3f07e308".U, "h3e9bcc93".U, "h3f13b461".U, "h3f3f706e".U, "h3d111583".U, "h3f554f1f".U, "h3f5f8eb6".U
    )
    val input2 = Array("h43061000".U, "h40100000".U, "h43061000".U, "hc0100000".U, "hc3061000".U, "h40100000".U, "hc3061000".U, "hc0100000".U, "hc048f5c3".U, "h4048f5c3".U, "h00000000".U, "h80000000".U, "h42340000".U, "hc2340000".U, "h42340000".U, "hc2340000".U, "h3dcccccd".U, "hbdcccccd".U, "h3dcccccd".U, "hbdcccccd".U, "h7f800000".U, "hff800000".U, "h3ec9ec8f".U, "h3f4c6691".U, "h3e4a4ae8".U, "h3f44aab2".U, "h3f0dd0fa".U, "h3f20fdaf".U, "h3f036e3e".U, "h3f6a8bc3".U, "h3f37a0c6".U, "h3f1b6250".U, "h3e78b74f".U, "h3f4dde87".U, "h3ecd4895".U, "h3dded726".U, "h3e5f7ebd".U, "h3f56d00f".U, "h3e979175".U, "h3f0637af".U, "h3f7907c9".U, "h3f4577b3".U, "h3f451913".U, "h3f643b46".U, "h3eb4756c".U, "h3f6b4552".U, "h3f730719".U, "h3db03e0c".U, "h3f29c93d".U, "h3eb2a218".U, "h3ca40760".U, "h3d813864".U, "h3f787b7a".U, "h3f59d5e1".U, "h3f0a2dbc".U, "h3f429fa9".U, "h3f2aeff2".U, "h3d20e46d".U, "h3f6e8cbe".U, "h3f389055".U, "h3f3d1096".U, "h3eb545de".U, "h3e29f522".U, "h3f614c9c".U, "h3ea921f3".U, "h3f64b40e".U, "h3f2fc999".U, "h3f16b11f".U, "h3f5bd236".U, "h3f6c8949".U, "h3f509490".U, "h3f693577".U, "h3e5d0138".U, "h3f6b8d86".U, "h3f618d4a".U, "h3edd2900".U, "h3e8fe705".U, "h3e9d6b1e".U, "h3e67887f".U, "h3e8d6ea2".U, "h3ed53fa8".U, "h3f68244d".U, "h3e0119e5".U, "h3f42ae81".U, "h3f6f5c6c".U, "h3ec43143".U, "h3ebcc178".U, "h3e6dd5f8".U, "h3e7a4755".U, "h3f3b6e16".U, "h3f4b20e0".U, "h3f3ebd00".U, "h3f733a05".U, "h3f05852d".U, "h3e75d2e9".U, "h3f3b8f3d".U, "h3f77a7dd".U, "h3f427dfc".U, "h3e0a23dc".U, "h3da0382c".U, "h3e51911a".U, "h3f51d65f".U, "h3f416dbf".U, "h3e219831".U, "h3e513b85".U, "h3e007acf".U, "h3d5d6b7a".U, "h3d94211f".U, "h3f6c4e42".U, "h3e38b382".U, "h3ec88b9f".U, "h3f51d78b".U, "h3f0d6fa9".U, "h3ee7b805".U, "h3dcc1017".U, "h3f41de02".U, "h3f7e02af".U, "h3f60ab4c".U, "h3f21003e".U, "h3f3f7002".U, "h3f6ce57a".U, "h3f54bee0".U
    )
    val output = Array("h43085000".U, "h43085000".U, "h4303d000".U, "h4303d000".U, "hc303d000".U, "hc303d000".U, "hc3085000".U, "hc3085000".U, "h00000000".U, "h00000000".U, "h00000000".U, "h80000000".U, "h7fc00000".U, "h7fc00000".U, "hffc00000".U, "hffc00000".U, "h7f800000".U, "h7f800000".U, "hff800000".U, "hff800000".U, "h7f800000".U, "hffc00000".U, "h3f9e0669".U, "h3fca6fe1".U, "h3f8dfa3a".U, "h3f8d3ded".U, "h3f54ed38".U, "h3f8d9a30".U, "h3f60d0c2".U, "h3fef288c".U, "h3fad2f64".U, "h3f3fa261".U, "h3e84b434".U, "h3f710023".U, "h3f0ec06a".U, "h3e74535c".U, "h3f9bcc9a".U, "h3fad0fcc".U, "h3f689eb2".U, "h3f94b728".U, "h3fbbb19f".U, "h3f882d0a".U, "h3fa5f8ea".U, "h3fa55854".U, "h3f22c206".U, "h3fdd062e".U, "h3f82714a".U, "h3f1caf64".U, "h3f5afe2a".U, "h3f9e9baa".U, "h3dac6e19".U, "h3f0552fe".U, "h3f9abdb2".U, "h3fe0667e".U, "h3f4e71f1".U, "h3f91569d".U, "h3f9712bc".U, "h3f1225a3".U, "h3faf4ae1".U, "h3fd36cf1".U, "h3f82ec05".U, "h3f7e7896".U, "h3f5a94f8".U, "h3fa8fba6".U, "h3f946bc0".U, "h3f8fa8db".U, "h3f84bd67".U, "h3fc5c61c".U, "h3fc20ba5".U, "h3fae8824".U, "h3f9b4a41".U, "h3fcc2f34".U, "h3f32c4d0".U, "h3fef68a2".U, "h3f83ad2b".U, "h3f89592e".U, "h3f669162".U, "h3f8bf67f".U, "h3f2c52eb".U, "h3eed72f9".U, "h3f7912ed".U, "h3f89c7d6".U, "h3e6abf9a".U, "h3fa0c1f6".U, "h3ff5ba8e".U, "h3f88a836".U, "h3f8f28dc".U, "h3f06c396".U, "h3f5432df".U, "h3f62711a".U, "h3f6b4000".U, "h3f68bf96".U, "h3f832734".U, "h3f12f7bc".U, "h3ed521bd".U, "h3fc3e5de".U, "h3fcfde36".U, "h3fb318c4".U, "h3e69dd34".U, "h3f193382".U, "h3e8c9352".U, "h3fa3fb03".U, "h3faa1960".U, "h3e56c7a4".U, "h3f9a273b".U, "h3f81f96b".U, "h3f86a33c".U, "h3f715fd7".U, "h3f6d5efe".U, "h3f463632".U, "h3f0e08cc".U, "h3fddc9d4".U, "h3f695d56".U, "h3f8418c4".U, "h3f497aa2".U, "h3fa4e085".U, "h3fa5f47c".U, "h3fba2fd6".U, "h3fb03856".U, "h3f48815a".U, "h3fe11a4c".U, "h3fda26cb".U
    )

    for (i <- 0 to 121) {
        poke(c.io.operand1, input1(i))
        poke(c.io.operand2, input2(i))
        step(1)
        expect(c.io.result, output(i))
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
