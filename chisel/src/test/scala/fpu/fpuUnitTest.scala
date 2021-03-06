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

    val floatInput1 = Array("h0000000040100000".U, "h0000000043061000".U, "h00000000c0100000".U, "h0000000043061000".U, "h0000000040100000".U, "h00000000c3061000".U, "h00000000c0100000".U, "h00000000c3061000".U, "h000000004048f5c3".U, "h00000000c048f5c3".U, "h0000000000000000".U, "h0000000080000000".U, "h000000007fc00000".U, "h000000007fc00000".U, "h00000000ffc00000".U, "h00000000ffc00000".U, "h000000007f800000".U, "h000000007f800000".U, "h00000000ff800000".U, "h00000000ff800000".U, "h000000007f800000".U, "h000000007f800000".U, "h000000003f57168b".U, "h000000003f487931".U, "h000000003f6961b9".U, "h000000003eaba251".U, "h000000003e8e387d".U, "h000000003ef46d63".U, "h000000003ebac508".U, "h000000003f73c554".U, "h000000003f22be01".U, "h000000003e110043".U, "h000000003c8588c7".U, "h000000003e0c866f".U, "h000000003e20707c".U, "h000000003e04e7c9".U, "h000000003f7fb984".U, "h000000003f034f8a".U, "h000000003f1cd5f7".U, "h000000003f2336a0".U, "h000000003efcb6e9".U, "h000000003e95c4c3".U, "h000000003f06d8c2".U, "h000000003ecceac4".U, "h000000003e910ea1".U, "h000000003f4ec709".U, "h000000003d8edbdb".U, "h000000003f06a7a2".U, "h000000003e44d3b5".U, "h000000003f63e649".U, "h000000003d836c41".U, "h000000003eea57e2".U, "h000000003e73ffa9".U, "h000000003f66f71c".U, "h000000003e88886a".U, "h000000003ec01b21".U, "h000000003f033585".U, "h000000003f08175c".U, "h000000003ee01209".U, "h000000003f6e498d".U, "h000000003e918ee8".U, "h000000003f23d5a7".U, "h000000003f3017af".U, "h000000003ee15561".U, "h000000003f544686".U, "h000000003e6a76a2".U, "h000000003eb36269".U, "h000000003f74db1a".U, "h000000003f284514".U, "h000000003ee10dff".U, "h000000003ecbffe4".U, "h000000003f2f28f2".U, "h000000003ef70904".U, "h000000003f7343bd".U, "h000000003e173430".U, "h000000003f241ddc".U, "h000000003f1e9de0".U, "h000000003f49376f".U, "h000000003ee4e196".U, "h000000003e4008ae".U, "h000000003f0e7319".U, "h000000003e2dad7b".U, "h000000003dd34b6a".U, "h000000003efdaad8".U, "h000000003f7c18af".U, "h000000003f2f37ca".U, "h000000003f3ff0fc".U, "h000000003e969c30".U, "h000000003f15a10a".U, "h000000003e1c0c12".U, "h000000003e007c7e".U, "h000000003e280a56".U, "h000000003d98a314".U, "h000000003d5728ec".U, "h000000003e347091".U, "h000000003f4c3c7e".U, "h000000003f28148e".U, "h000000003f23b38b".U, "h000000003dbf72b1".U, "h000000003f052c7d".U, "h000000003d8f2b16".U, "h000000003eec3f4e".U, "h000000003f12c502".U, "h000000003d54bdcc".U, "h000000003f7fff94".U, "h000000003f63d422".U, "h000000003f7f6fc1".U, "h000000003f5edbb3".U, "h000000003b885e1b".U, "h000000003f180951".U, "h000000003e270bf2".U, "h000000003f69bc1e".U, "h000000003eb7db59".U, "h000000003f145586".U, "h000000003f2ff89f".U, "h000000003f07e308".U, "h000000003e9bcc93".U, "h000000003f13b461".U, "h000000003f3f706e".U, "h000000003d111583".U, "h000000003f554f1f".U, "h000000003f5f8eb6".U
    )
    val floatInput2 = Array("h0000000043061000".U, "h0000000040100000".U, "h0000000043061000".U, "h00000000c0100000".U, "h00000000c3061000".U, "h0000000040100000".U, "h00000000c3061000".U, "h00000000c0100000".U, "h00000000c048f5c3".U, "h000000004048f5c3".U, "h0000000000000000".U, "h0000000080000000".U, "h0000000042340000".U, "h00000000c2340000".U, "h0000000042340000".U, "h00000000c2340000".U, "h000000003dcccccd".U, "h00000000bdcccccd".U, "h000000003dcccccd".U, "h00000000bdcccccd".U, "h000000007f800000".U, "h00000000ff800000".U, "h000000003ec9ec8f".U, "h000000003f4c6691".U, "h000000003e4a4ae8".U, "h000000003f44aab2".U, "h000000003f0dd0fa".U, "h000000003f20fdaf".U, "h000000003f036e3e".U, "h000000003f6a8bc3".U, "h000000003f37a0c6".U, "h000000003f1b6250".U, "h000000003e78b74f".U, "h000000003f4dde87".U, "h000000003ecd4895".U, "h000000003dded726".U, "h000000003e5f7ebd".U, "h000000003f56d00f".U, "h000000003e979175".U, "h000000003f0637af".U, "h000000003f7907c9".U, "h000000003f4577b3".U, "h000000003f451913".U, "h000000003f643b46".U, "h000000003eb4756c".U, "h000000003f6b4552".U, "h000000003f730719".U, "h000000003db03e0c".U, "h000000003f29c93d".U, "h000000003eb2a218".U, "h000000003ca40760".U, "h000000003d813864".U, "h000000003f787b7a".U, "h000000003f59d5e1".U, "h000000003f0a2dbc".U, "h000000003f429fa9".U, "h000000003f2aeff2".U, "h000000003d20e46d".U, "h000000003f6e8cbe".U, "h000000003f389055".U, "h000000003f3d1096".U, "h000000003eb545de".U, "h000000003e29f522".U, "h000000003f614c9c".U, "h000000003ea921f3".U, "h000000003f64b40e".U, "h000000003f2fc999".U, "h000000003f16b11f".U, "h000000003f5bd236".U, "h000000003f6c8949".U, "h000000003f509490".U, "h000000003f693577".U, "h000000003e5d0138".U, "h000000003f6b8d86".U, "h000000003f618d4a".U, "h000000003edd2900".U, "h000000003e8fe705".U, "h000000003e9d6b1e".U, "h000000003e67887f".U, "h000000003e8d6ea2".U, "h000000003ed53fa8".U, "h000000003f68244d".U, "h000000003e0119e5".U, "h000000003f42ae81".U, "h000000003f6f5c6c".U, "h000000003ec43143".U, "h000000003ebcc178".U, "h000000003e6dd5f8".U, "h000000003e7a4755".U, "h000000003f3b6e16".U, "h000000003f4b20e0".U, "h000000003f3ebd00".U, "h000000003f733a05".U, "h000000003f05852d".U, "h000000003e75d2e9".U, "h000000003f3b8f3d".U, "h000000003f77a7dd".U, "h000000003f427dfc".U, "h000000003e0a23dc".U, "h000000003da0382c".U, "h000000003e51911a".U, "h000000003f51d65f".U, "h000000003f416dbf".U, "h000000003e219831".U, "h000000003e513b85".U, "h000000003e007acf".U, "h000000003d5d6b7a".U, "h000000003d94211f".U, "h000000003f6c4e42".U, "h000000003e38b382".U, "h000000003ec88b9f".U, "h000000003f51d78b".U, "h000000003f0d6fa9".U, "h000000003ee7b805".U, "h000000003dcc1017".U, "h000000003f41de02".U, "h000000003f7e02af".U, "h000000003f60ab4c".U, "h000000003f21003e".U, "h000000003f3f7002".U, "h000000003f6ce57a".U, "h000000003f54bee0".U
    )
    val floatOutputAdd = Array("h0000000043085000".U, "h0000000043085000".U, "h000000004303d000".U, "h000000004303d000".U, "h00000000c303d000".U, "h00000000c303d000".U, "h00000000c3085000".U, "h00000000c3085000".U, "h0000000000000000".U, "h0000000000000000".U, "h0000000000000000".U, "h0000000080000000".U, "h000000007fc00000".U, "h000000007fc00000".U, "h00000000ffc00000".U, "h00000000ffc00000".U, "h000000007f800000".U, "h000000007f800000".U, "h00000000ff800000".U, "h00000000ff800000".U, "h000000007f800000".U, "h00000000ffc00000".U, "h000000003f9e0669".U, "h000000003fca6fe1".U, "h000000003f8dfa3a".U, "h000000003f8d3ded".U, "h000000003f54ed38".U, "h000000003f8d9a30".U, "h000000003f60d0c2".U, "h000000003fef288c".U, "h000000003fad2f64".U, "h000000003f3fa261".U, "h000000003e84b434".U, "h000000003f710023".U, "h000000003f0ec06a".U, "h000000003e74535c".U, "h000000003f9bcc9a".U, "h000000003fad0fcc".U, "h000000003f689eb2".U, "h000000003f94b728".U, "h000000003fbbb19f".U, "h000000003f882d0a".U, "h000000003fa5f8ea".U, "h000000003fa55854".U, "h000000003f22c206".U, "h000000003fdd062e".U, "h000000003f82714a".U, "h000000003f1caf64".U, "h000000003f5afe2a".U, "h000000003f9e9baa".U, "h000000003dac6e19".U, "h000000003f0552fe".U, "h000000003f9abdb2".U, "h000000003fe0667e".U, "h000000003f4e71f1".U, "h000000003f91569d".U, "h000000003f9712bc".U, "h000000003f1225a3".U, "h000000003faf4ae1".U, "h000000003fd36cf1".U, "h000000003f82ec05".U, "h000000003f7e7896".U, "h000000003f5a94f8".U, "h000000003fa8fba6".U, "h000000003f946bc0".U, "h000000003f8fa8db".U, "h000000003f84bd67".U, "h000000003fc5c61c".U, "h000000003fc20ba5".U, "h000000003fae8824".U, "h000000003f9b4a41".U, "h000000003fcc2f34".U, "h000000003f32c4d0".U, "h000000003fef68a2".U, "h000000003f83ad2b".U, "h000000003f89592e".U, "h000000003f669162".U, "h000000003f8bf67f".U, "h000000003f2c52eb".U, "h000000003eed72f9".U, "h000000003f7912ed".U, "h000000003f89c7d6".U, "h000000003e6abf9a".U, "h000000003fa0c1f6".U, "h000000003ff5ba8e".U, "h000000003f88a836".U, "h000000003f8f28dc".U, "h000000003f06c396".U, "h000000003f5432df".U, "h000000003f62711a".U, "h000000003f6b4000".U, "h000000003f68bf96".U, "h000000003f832734".U, "h000000003f12f7bc".U, "h000000003ed521bd".U, "h000000003fc3e5de".U, "h000000003fcfde36".U, "h000000003fb318c4".U, "h000000003e69dd34".U, "h000000003f193382".U, "h000000003e8c9352".U, "h000000003fa3fb03".U, "h000000003faa1960".U, "h000000003e56c7a4".U, "h000000003f9a273b".U, "h000000003f81f96b".U, "h000000003f86a33c".U, "h000000003f715fd7".U, "h000000003f6d5efe".U, "h000000003f463632".U, "h000000003f0e08cc".U, "h000000003fddc9d4".U, "h000000003f695d56".U, "h000000003f8418c4".U, "h000000003f497aa2".U, "h000000003fa4e085".U, "h000000003fa5f47c".U, "h000000003fba2fd6".U, "h000000003fb03856".U, "h000000003f48815a".U, "h000000003fe11a4c".U, "h000000003fda26cb".U
    )
    val floatOutputMul = Array("h000000004396d200".U, "h000000004396d200".U, "h00000000c396d200".U, "h00000000c396d200".U, "h00000000c396d200".U, "h00000000c396d200".U, "h000000004396d200".U, "h000000004396d200".U, "h00000000c11dc0ed".U, "h00000000c11dc0ed".U, "h0000000000000000".U, "h0000000000000000".U, "h000000007fc00000".U, "h000000007fc00000".U, "h00000000ffc00000".U, "h00000000ffc00000".U, "h000000007f800000".U, "h00000000ff800000".U, "h00000000ff800000".U, "h000000007f800000".U, "h000000007f800000".U, "h00000000ff800000".U, "h000000003ea9a774".U, "h000000003f2010e5".U, "h000000003e386b66".U, "h000000003e83dab7".U, "h000000003e1d926b".U, "h000000003e99b695".U, "h000000003e3fc682".U, "h000000003f5f5775".U, "h000000003ee9780f".U, "h000000003db005b0".U, "h000000003b81bc1f".U, "h000000003de2039b".U, "h000000003d80a790".U, "h000000003c676165".U, "h000000003e5f4134".U, "h000000003edc5e6b".U, "h000000003e39b6a3".U, "h000000003eab2430".U, "h000000003ef5d598".U, "h000000003e670ce2".U, "h000000003ecfa405".U, "h000000003eb6b089".U, "h000000003dcc81a4".U, "h000000003f3e08b3".U, "h000000003d879ea7".U, "h000000003d3967c5".U, "h000000003e028a7c".U, "h000000003e9f066c".U, "h000000003aa86a46".U, "h000000003cec93cf".U, "h000000003e6cd55c".U, "h000000003f44886d".U, "h000000003e1363db".U, "h000000003e920c5f".U, "h000000003eaf390b".U, "h000000003cab1010".U, "h000000003ed0cbf5".U, "h000000003f2bcb36".U, "h000000003e56ffdf".U, "h000000003e68058b".U, "h000000003de9d081".U, "h000000003ec64f79".U, "h000000003e8c3eb5".U, "h000000003e517691".U, "h000000003e765b13".U, "h000000003f1021ca".U, "h000000003f107d44".U, "h000000003ecff198".U, "h000000003ea6364c".U, "h000000003f1f90d9".U, "h000000003dd543f6".U, "h000000003f5fd5aa".U, "h000000003e053852".U, "h000000003e8dc810".U, "h000000003e3252a7".U, "h000000003e777662".U, "h000000003dcf0198".U, "h000000003d542f8a".U, "h000000003e6d525f".U, "h000000003e1d7dd8".U, "h000000003c551cc0".U, "h000000003ec0e862".U, "h000000003f6bb60f".U, "h000000003e86486e".U, "h000000003e8d8608".U, "h000000003d8bec7a".U, "h000000003e1248f5".U, "h000000003de47fd7".U, "h000000003dcbe670".U, "h000000003dfa6777".U, "h000000003d910564".U, "h000000003ce07061".U, "h000000003d2d4463".U, "h000000003f15a276".U, "h000000003f229a0c".U, "h000000003ef8bd3e".U, "h000000003c4e9d49".U, "h000000003d26b20d".U, "h000000003c6a66a6".U, "h000000003ec1a583".U, "h000000003eddcae4".U, "h000000003c0649d7".U, "h000000003e513b2d".U, "h000000003de4aeb9".U, "h000000003d5ceeb7".U, "h000000003d80f3d9".U, "h000000003b7bc0e1".U, "h000000003ddb629c".U, "h000000003d82dc70".U, "h000000003f3f9760".U, "h000000003e4b2803".U, "h000000003e8643cc".U, "h000000003d8c452e".U, "h000000003ecdd002".U, "h000000003e9a969c".U, "h000000003f01a0aa".U, "h000000003ef0cbc7".U, "h000000003cd8fd0f".U, "h000000003f456426".U, "h000000003f39c8de".U
    )

    val floatCastToUIntInput = Array(
    "h0000000040000000".U, "h0000000040a00000".U, "h000000003f000000".U, "h000000003f400000".U, "h000000003fc00000".U
    )
    val floatCastToUIntOutput = Array(
    "h0000000000000002".U, "h0000000000000005".U, "h0000000000000000".U, "h0000000000000000".U, "h0000000000000001".U
    )

    val floatCastToSIntInput = Array(
    "h0000000040000000".U, "h0000000040a00000".U, "h000000003f000000".U, "h000000003f400000".U, "h000000003fc00000".U, "h00000000c0000000".U, "h00000000c0a00000".U, "h00000000bf000000".U, "h00000000bf400000".U, "h00000000bfc00000".U
    )
    val floatCastToSIntOutput = Array(
    "h0000000000000002".U, "h0000000000000005".U, "h0000000000000000".U, "h0000000000000000".U, "h0000000000000001".U, "h00000000fffffffe".U, "h00000000fffffffb".U, "h0000000000000000".U, "h0000000000000000".U, "h00000000ffffffff".U
    )

    val uIntCastToFloatInput = Array(
    "h00000000007b4567".U, "h00000000007b23c6".U, "h00000000003c9869".U, "h0000000000334873".U, "h000000000050dc51".U
    )
    val uIntCastToFloatOutput = Array(
    "h000000004af68ace".U, "h000000004af6478c".U, "h000000004a7261a4".U, "h000000004a4d21cc".U, "h000000004aa1b8a2".U
    )

    val sIntCastToFloatInput = Array(
    "h00000000007b4567".U, "h00000000007b23c6".U, "h00000000003c9869".U, "h0000000000334873".U, "h000000000050dc51".U, "h00000000ff84ba99".U, "h00000000ff84dc3a".U, "h00000000ffc36797".U, "h00000000ffccb78d".U, "h00000000ffaf23af".U, "h0000000000000000".U
    )
    val sIntCastToFloatOutput = Array(
    "h000000004af68ace".U, "h000000004af6478c".U, "h000000004a7261a4".U, "h000000004a4d21cc".U, "h000000004aa1b8a2".U, "h00000000caf68ace".U, "h00000000caf6478c".U, "h00000000ca7261a4".U, "h00000000ca4d21cc".U, "h00000000caa1b8a2".U, "h0000000000000000".U
    )

    val doubleInput1 = Array(
    "h4002000000000000".U, "h4060c20000000000".U, "hc002000000000000".U, "h4060c20000000000".U, "h4002000000000000".U, "hc060c20000000000".U, "hc002000000000000".U, "hc060c20000000000".U, "h40091eb851eb851f".U, "hc0091eb851eb851f".U, "h0000000000000000".U, "h8000000000000000".U, "h7ff8000000000000".U, "h7ff8000000000000".U, "hfff8000000000000".U, "hfff8000000000000".U, "h7ff0000000000000".U, "h7ff0000000000000".U, "hfff0000000000000".U, "hfff0000000000000".U, "h7ff0000000000000".U, "h7ff0000000000000".U, "h3feae2d159f5c5a3".U, "h3fe90f261a721e4c".U, "h3fed2c37147a586e".U, "h3fd5744a252ae894".U, "h3fd1c70f94a38e1f".U, "h3fde8dac5d3d1b59".U, "h3fd758a0f92eb142".U, "h3fee78aa78fcf155".U, "h3fe457c01f28af80".U, "h3fc2200854244011".U, "h3f90b118d8216232".U, "h3fc190cde723219c".U, "h3fc40e0f76281c1f".U, "h3fc09cf92e2139f2".U, "h3feff7308cffee61".U, "h3fe069f13260d3e2".U, "h3fe39abed9a7357e".U, "h3fe466d40368cda8".U, "h3fdf96dd18bf2dba".U, "h3fd2b89851a57131".U, "h3fe0db184961b631".U, "h3fd99d5882b33ab1".U, "h3fd221d42c2443a8".U, "h3fe9d8e117b3b1c2".U, "h3fb1db7b5623b6f7".U, "h3fe0d4f43361a9e8".U, "h3fc89a769b3134ed".U, "h3fec7cc91538f992".U, "h3fb06d881c20db10".U, "h3fdd4afc3a3a95f8".U, "h3fce7ff5213cffea".U, "h3fecdee37739bdc7".U, "h3fd1110d3822221a".U, "h3fd803641f3006c8".U, "h3fe066b09060cd61".U, "h3fe102eb7f2205d7".U, "h3fdc02411f380482".U, "h3fedc9319fbb9263".U, "h3fd231dcf52463ba".U, "h3fe47ab4dae8f56a".U, "h3fe602f5e3ec05ec".U, "h3fdc2aac2e385558".U, "h3fea88d0bb3511a1".U, "h3fcd4ed43b3a9da8".U, "h3fd66c4d192cd89a".U, "h3fee9b634f3d36c7".U, "h3fe508a27b2a1145".U, "h3fdc21bfedb84380".U, "h3fd97ffc8132fff9".U, "h3fe5e51e3fabca3c".U, "h3fdee1207dbdc241".U, "h3fee6877aabcd0ef".U, "h3fc2e685fb25cd0c".U, "h3fe483bb74690777".U, "h3fe3d3bc0167a778".U, "h3fe926eddf324ddc".U, "h3fdc9c32bab93865".U, "h3fc80115be30022b".U, "h3fe1ce6322639cc6".U, "h3fc5b5af5c2b6b5f".U, "h3fba696d5034d2db".U, "h3fdfb55b07bf6ab6".U, "h3fef8315ec7f062c".U, "h3fe5e6f93c6bcdf2".U, "h3fe7fe1f816ffc3f".U, "h3fd2d385fba5a70c".U, "h3fe2b4213a656842".U, "h3fc381823a270304".U, "h3fc00f8fca201f20".U, "h3fc5014acb2a0296".U, "h3fb31462902628c5".U, "h3faae51d9035ca3b".U, "h3fc68e121f2d1c24".U, "h3fe9878fc7b30f20".U, "h3fe50291c72a0524".U, "h3fe476715928ece3".U, "h3fb7ee56282fdcac".U, "h3fe0a58f96a14b1f".U, "h3fb1e562bc23cac5".U, "h3fdd87e9bcbb0fd3".U, "h3fe258a04ee4b141".U, "h3faa97b990352f73".U, "h3feffff2847fffe5".U, "h3fec7a844278f509".U, "h3fefedf82abfdbf0".U, "h3febdb766b37b6ed".U, "h3f710bc360221787".U, "h3fe3012a2be60254".U, "h3fc4e17e3329c2fc".U, "h3fed3783b8fa6f07".U, "h3fd6fb6b242df6d6".U, "h3fe28ab0c5651562".U, "h3fe5ff13eeebfe28".U, "h3fe0fc6108a1f8c2".U, "h3fd379925d26f325".U, "h3fe2768c1f64ed18".U, "h3fe7ee0dc2efdc1c".U, "h3fa222b068244561".U, "h3feaa9e3dff553c8".U, "h3febf1d6be37e3ad".U
    )
    val doubleInput2 = Array(
    "h4060c20000000000".U, "h4002000000000000".U, "h4060c20000000000".U, "hc002000000000000".U, "hc060c20000000000".U, "h4002000000000000".U, "hc060c20000000000".U, "hc002000000000000".U, "hc0091eb851eb851f".U, "h40091eb851eb851f".U, "h0000000000000000".U, "h8000000000000000".U, "h4046800000000000".U, "hc046800000000000".U, "h4046800000000000".U, "hc046800000000000".U, "h3fb999999999999a".U, "hbfb999999999999a".U, "h3fb999999999999a".U, "hbfb999999999999a".U, "h7ff0000000000000".U, "hfff0000000000000".U, "h3fd93d91e3327b24".U, "h3fe98cd21cf319a4".U, "h3fc9495cff3292ba".U, "h3fe895563b312aac".U, "h3fe1ba1f3363743e".U, "h3fe41fb5eae83f6c".U, "h3fe06dc7bee0db8f".U, "h3fed517851baa2f1".U, "h3fe6f418b0ade831".U, "h3fe36c49fe26d894".U, "h3fcf16e9e83e2dd4".U, "h3fe9bbd0e37377a2".U, "h3fd9a912ad335225".U, "h3fbbdae4c637b5ca".U, "h3fcbefd79f37dfaf".U, "h3feada01e6b5b404".U, "h3fd2f22e9925e45d".U, "h3fe0c6f5ede18dec".U, "h3fef20f9163e41f2".U, "h3fe8aef656b15ded".U, "h3fe8a32257714645".U, "h3fec8768c5f90ed2".U, "h3fd68ead74ad1d5b".U, "h3fed68aa353ad154".U, "h3fee60e32cbcc1c6".U, "h3fb607c18c2c0f83".U, "h3fe53927ad2a724f".U, "h3fd6544308aca886".U, "h3f9480ec102901d8".U, "h3fb0270c82204e19".U, "h3fef0f6f4f7e1edf".U, "h3feb3abc21f67578".U, "h3fe145b77a628b6f".U, "h3fe853f52870a7ea".U, "h3fe55dfe386abbfc".U, "h3fa41c8d9c28391b".U, "h3fedd197c07ba330".U, "h3fe7120aa5ee2415".U, "h3fe7a212b72f4425".U, "h3fd6a8bbcb2d5178".U, "h3fc53ea4382a7d48".U, "h3fec29938ab85327".U, "h3fd5243e582a487d".U, "h3fec9681bef92d03".U, "h3fe5f9332bebf266".U, "h3fe2d623d525ac48".U, "h3feb7a46c636f48e".U, "h3fed9129173b2252".U, "h3fea129206b42524".U, "h3fed26aed0fa4d5e".U, "h3fcba026fa37404e".U, "h3fed71b0cebae362".U, "h3fec31a94a786353".U, "h3fdba51ff3374a40".U, "h3fd1fce09e23f9c1".U, "h3fd3ad63ca275ac8".U, "h3fccf10fd839e220".U, "h3fd1add430a35ba8".U, "h3fdaa7f4fcb54fea".U, "h3fed0489aefa0913".U, "h3fc0233c99204679".U, "h3fe855d02570aba0".U, "h3fedeb8d7afbd71b".U, "h3fd8862859b10c51".U, "h3fd7982ef7af305e".U, "h3fcdbabf003b757e".U, "h3fcf48eaa13e91d5".U, "h3fe76dc2b96edb85".U, "h3fe9641c02f2c838".U, "h3fe7d79ff42faf40".U, "h3fee674091fcce81".U, "h3fe0b0a5af61614b".U, "h3fceba5d233d74ba".U, "h3fe771e7aa2ee3cf".U, "h3feef4fb9efde9f7".U, "h3fe84fbf71709f7f".U, "h3fc1447b732288f7".U, "h3fb407058a280e0b".U, "h3fca32234b346447".U, "h3fea3acbd8f47598".U, "h3fe82db7dc305b70".U, "h3fc433062428660c".U, "h3fca27709e344ee1".U, "h3fc00f59dc201eb4".U, "h3fabad6f50375adf".U, "h3fb28423e4250848".U, "h3fed89c8443b1391".U, "h3fc716703b2e2ce0".U, "h3fd91173e6b222e8".U, "h3fea3af1543475e3".U, "h3fe1adf511e35bea".U, "h3fdcf700ae39ee01".U, "h3fb98202de330406".U, "h3fe83bc046707781".U, "h3fefc055e6ff80ac".U, "h3fec15697d782ad3".U, "h3fe42007b868400f".U, "h3fe7ee00472fdc01".U, "h3fed9caf48fb395f".U, "h3fea97dc0a752fb8".U
    )
    val doubleOutputAdd = Array(
    "h40610a0000000000".U, "h40610a0000000000".U, "h40607a0000000000".U, "h40607a0000000000".U, "hc0607a0000000000".U, "hc0607a0000000000".U, "hc0610a0000000000".U, "hc0610a0000000000".U, "h0000000000000000".U, "h0000000000000000".U, "h0000000000000000".U, "h8000000000000000".U, "h7ff8000000000000".U, "h7ff8000000000000".U, "hfff8000000000000".U, "hfff8000000000000".U, "h7ff0000000000000".U, "h7ff0000000000000".U, "hfff0000000000000".U, "hfff0000000000000".U, "h7ff0000000000000".U, "hfff8000000000000".U, "h3ff3c0cd25c7819a".U, "h3ff94dfc1bb29bf8".U, "h3ff1bf472a237e8e".U, "h3ff1a7bda6e34f7b".U, "h3fea9da6fdb53b4e".U, "h3ff1b3460cc3668c".U, "h3fec1a183b783430".U, "h3ffde511655bca23".U, "h3ff5a5ec67eb4bd8".U, "h3fe7f44c132fe898".U, "h3fd0968681a12d0d".U, "h3fee20045d3c4009".U, "h3fe1d80d3423b01a".U, "h3fce8a6b913d14d7".U, "h3ff379933a66f326".U, "h3ff5a1f98c8b43f3".U, "h3fed13d6263a27ac".U, "h3ff296e4f8a52dca".U, "h3ff77633d14eec68".U, "h3ff105a13fc20b43".U, "h3ff4bf1d50697e3b".U, "h3ff4ab0a83a95615".U, "h3fe45840d068b082".U, "h3ffba0c5a677418b".U, "h3ff04e294bc09c52".U, "h3fe395ec64e72bd8".U, "h3feb5fc553f6bf8a".U, "h3ff3d3754cc7a6ea".U, "h3fb58dc3202b1b86".U, "h3fe0aa5fad6154bf".U, "h3ff357b64be6af6d".U, "h3ffc0ccfcc9819a0".U, "h3fe9ce3e16739c7c".U, "h3ff22ad39c0455a7".U, "h3ff2e2576465c4ae".U, "h3fe244b458e48969".U, "h3ff5e95c280bd2b8".U, "h3ffa6d9e22d4db3c".U, "h3ff05d8098e0bb01".U, "h3fefcf12c07f9e26".U, "h3feb529ef1f6a53e".U, "h3ff51f74d0ea3eea".U, "h3ff28d77f3a51af0".U, "h3ff1f51b66e3ea36".U, "h3ff097acdc412f5a".U, "h3ff8b8c392317188".U, "h3ff84174a0b082ea".U, "h3ff5d104870ba209".U, "h3ff3694823a6d290".U, "h3ff985e688530bcd".U, "h3fe65899fd6cb134".U, "h3ffded143cbbda28".U, "h3ff075a564a0eb4b".U, "h3ff12b25b702564c".U, "h3fecd22c5079a458".U, "h3ff17ecfe222fda0".U, "h3fe58a5d536b14ba".U, "h3fddae5f0fbb5cbe".U, "h3fef225da0be44bb".U, "h3ff138fac30271f5".U, "h3fcd57f3413aafe6".U, "h3ff4183ed4a8307e".U, "h3ffeb751b3bd6ea4".U, "h3ff11506b4a22a0d".U, "h3ff1e51b7ea3ca37".U, "h3fe0d872bde1b0e6".U, "h3fea865be2b50cb7".U, "h3fec4e2347f89c46".U, "h3fed67fff57ad000".U, "h3fed17f2a6fa2fe6".U, "h3ff064e67200c9cd".U, "h3fe25ef78864bdef".U, "h3fdaa437a135486f".U, "h3ff87cbbb8f0f978".U, "h3ff9fbc6b313f78e".U, "h3ff66318654cc631".U, "h3fcd3ba6873a774d".U, "h3fe3267047e64ce0".U, "h3fd1926a54a324d5".U, "h3ff47f605ba8fec1".U, "h3ff5432c158a8658".U, "h3fcad8f48835b1e9".U, "h3ff344e7560689cf".U, "h3ff03f2d5cc07e5b".U, "h3ff0d4678fe1a8cf".U, "h3fee2bfae7bc57f6".U, "h3fedabdfcafb57c0".U, "h3fe8c6c63ab18d8c".U, "h3fe1c11980238233".U, "h3ffbb93a86977275".U, "h3fed2baaa3fa5755".U, "h3ff083188e410631".U, "h3fe92f544ab25ea9".U, "h3ff49c10a7893822".U, "h3ff4be8f8ac97d1f".U, "h3ff745face6e8bf6".U, "h3ff6070abdac0e16".U, "h3fe9102b4db22057".U, "h3ffc234994784694".U, "h3ffb44d9645689b2".U
    )
    val doubleOutputMul = Array(
    "h4072da4000000000".U, "h4072da4000000000".U, "hc072da4000000000".U, "hc072da4000000000".U, "hc072da4000000000".U, "hc072da4000000000".U, "h4072da4000000000".U, "h4072da4000000000".U, "hc023b81d7dbf4880".U, "hc023b81d7dbf4880".U, "h0000000000000000".U, "h0000000000000000".U, "h7ff8000000000000".U, "h7ff8000000000000".U, "hfff8000000000000".U, "hfff8000000000000".U, "h7ff0000000000000".U, "hfff0000000000000".U, "hfff0000000000000".U, "h7ff0000000000000".U, "h7ff0000000000000".U, "hfff0000000000000".U, "h3fd534ee8003857d".U, "h3fe4021c957288f2".U, "h3fc70d6cad6b18d4".U, "h3fd07b56e553e17f".U, "h3fc3b24d4dd4e6d4".U, "h3fd336d2aae12fc5".U, "h3fc7f8d036a6eb49".U, "h3febeaee7f79e8b6".U, "h3fdd2f01c0f5a9dc".U, "h3fb600b5ee31864e".U, "h3f703783d51636ee".U, "h3fbc407373ce3943".U, "h3fb014f20a6c39be".U, "h3f8cec2ccb9bcbcc".U, "h3fcbe8268d6f457f".U, "h3fdb8bcd5b782515".U, "h3fc736d45402fda1".U, "h3fd5648618f4323d".U, "h3fdebab2f0ef8970".U, "h3fcce19c2e85b2fa".U, "h3fd9f480a3306a1b".U, "h3fd6d6111d9be55b".U, "h3fb9903489b92d72".U, "h3fe7c11654326600".U, "h3fb0f3d4d8adf0e7".U, "h3fa72cf88db422d6".U, "h3fc0514f7a80fb5b".U, "h3fd3e0cd7b6ccc97".U, "h3f550d48c4395a3f".U, "h3f9d9279e78216be".U, "h3fcd9aab8ab9123a".U, "h3fe8910da4fd633e".U, "h3fc26c7b4a38ab6d".U, "h3fd2418bdb643f76".U, "h3fd5e72137ce0737".U, "h3f9562020acb8f4e".U, "h3fda197e90bbb37f".U, "h3fe57966c00b3e7b".U, "h3fcadffbc5ff02c1".U, "h3fcd00b1762b7602".U, "h3fbd3a101f962b4d".U, "h3fd8c9ef332dbc44".U, "h3fd187d697dd27ad".U, "h3fca2ed20b58be37".U, "h3fcecb6257a62009".U, "h3fe204394a874c54".U, "h3fe20fa8888e090c".U, "h3fd9fe32fcc64f0b".U, "h3fd4c6c98449b700".U, "h3fe3f21b1a704d06".U, "h3fbaa87ea95784d7".U, "h3febfab55c524cce".U, "h3fc0a70a36596ca2".U, "h3fd1b901e4523e20".U, "h3fc64a54ead3cb60".U, "h3fceeecc43b23a21".U, "h3fb9e032ebfa32a2".U, "h3faa85f12c83d72c".U, "h3fcdaa4be73cfbf4".U, "h3fc3afbb09ff2c6d".U, "h3f8aa397f7739fad".U, "h3fd81d0c3f852ce7".U, "h3fed76c1da08401a".U, "h3fd0c90dbf18fc9b".U, "h3fd1b0c0f1b1787b".U, "h3fb17d8f457f8361".U, "h3fc2491e9ec7759b".U, "h3fbc8ffadf1fd6f2".U, "h3fb97cce0cb2c170".U, "h3fbf4ceede7ff9a4".U, "h3fb220ac8433fb9e".U, "h3f9c0e0c56f69cfe".U, "h3fa5a88c6c4ae01b".U, "h3fe2b44edd9d6133".U, "h3fe4534176d0dd1f".U, "h3fdf17a7ac633f59".U, "h3f89d3a91d48ad64".U, "h3fa4d641ae960680".U, "h3f8d4cd4c02bed64".U, "h3fd834b05f67d7c0".U, "h3fdbb95c8b588262".U, "h3f80c93ae45e20ad".U, "h3fca2765993e57e3".U, "h3fbc95d70fc4be70".U, "h3fab9dd6fb37fb32".U, "h3fb01e7b236f0aaf".U, "h3f6f781c32e00eca".U, "h3fbb6c538789570e".U, "h3fb05b8e03ea4e87".U, "h3fe7f2ebfc0468db".U, "h3fc965005072561d".U, "h3fd0c879961fc917".U, "h3fb188a5cc50e7cc".U, "h3fd9ba005216c00b".U, "h3fd352d37feeb7c1".U, "h3fe0341544cd1951".U, "h3fde1978db25418e".U, "h3f9b1fa1e6716d56".U, "h3fe8ac84c7bfe5c4".U, "h3fe7391bcfa54578".U
    )

    val doubleCastToUIntInput = Array(
    "h4008000000000000".U
    )
    val doubleCastToUIntOutput = Array(
    "h0000000000000003".U
    )

    val doubleCastToSIntInput = Array(
    "h4008000000000000".U, "hc008000000000000".U
    )
    val doubleCastToSIntOutput = Array(
    "h0000000000000003".U, "hfffffffffffffffd".U
    )

    val uIntCastToDoubleInput = Array(
    "h0000000000000003".U, "h000000006b8b4567".U, "h00000000327b23c6".U, "h00000000643c9869".U, "h0000000066334873".U, "h0000000074b0dc51".U, "h0000000000000000".U
    )
    val uIntCastToDoubleOutput = Array(
    "h4008000000000000".U, "h41dae2d159c00000".U, "h41c93d91e3000000".U, "h41d90f261a400000".U, "h41d98cd21cc00000".U, "h41dd2c3714400000".U, "h0000000000000000".U
    )

    val sIntCastToDoubleInput = Array(
    "hfffffffffffffffb".U, "hfffffffffffffffc".U, "hfffffffffffffffd".U, "hfffffffffffffffe".U, "hffffffffffffffff".U, "h0000000000000000".U, "h0000000000000001".U, "h0000000000000002".U, "h0000000000000003".U, "h0000000000000004".U, "h0000000000000005".U, "h000000006b8b4567".U, "h00000000327b23c6".U, "h00000000643c9869".U, "h0000000066334873".U, "h0000000014b0dc51".U, "hffffffff9474ba99".U, "hffffffffcd84dc3a".U, "hffffffff9bc36797".U, "hffffffff99ccb78d".U, "hffffffffeb4f23af".U
    )
    val sIntCastToDoubleOutput = Array(
    "hc014000000000000".U, "hc010000000000000".U, "hc008000000000000".U, "hc000000000000000".U, "hbff0000000000000".U, "h0000000000000000".U, "h3ff0000000000000".U, "h4000000000000000".U, "h4008000000000000".U, "h4010000000000000".U, "h4014000000000000".U, "h41dae2d159c00000".U, "h41c93d91e3000000".U, "h41d90f261a400000".U, "h41d98cd21cc00000".U, "h41b4b0dc51000000".U, "hc1dae2d159c00000".U, "hc1c93d91e3000000".U, "hc1d90f261a400000".U, "hc1d98cd21cc00000".U, "hc1b4b0dc51000000".U
    )

    poke(c.io.inputType, 0) // ask for float
//    poke(c.io.operand, 0) // ask for addition
//    for (i <- 0 to 121) {
//        poke(c.io.operand1, floatInput1(i))
//        poke(c.io.operand2, floatInput2(i))
//        step(1)
//        expect(c.io.result, floatOutputAdd(i))
//    }

//    poke(c.io.operand, 1) // ask for multiplication
//    for (i <- 0 to 121) {
//        poke(c.io.operand1, floatInput1(i))
//        poke(c.io.operand2, floatInput2(i))
//        step(1)
//        expect(c.io.result, floatOutputMul(i))
//    }

    poke(c.io.operand, 2) // ask for converstion to uint
    for (i <- 0 to 4) {
        poke(c.io.operand1, floatCastToUIntInput(i))
        poke(c.io.operand2, 1.U)
        step(1)
        expect(c.io.result, floatCastToUIntOutput(i))
    }

//    poke(c.io.operand, 3) // ask for converstion to uint
//    for (i <- 0 to 9) {
//        poke(c.io.operand1, floatCastToSIntInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, floatCastToSIntOutput(i))
//    }

//    poke(c.io.operand, 4) // ask for converstion to double from uint
//    for (i <- 0 to 4) {
//        poke(c.io.operand1, uIntCastToFloatInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, uIntCastToFloatOutput(i))
//    }

//    poke(c.io.operand, 5) // ask for converstion to double from sint
//    for (i <- 0 to 10) {
//        poke(c.io.operand1, sIntCastToFloatInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, sIntCastToFloatOutput(i))
//    }

//    poke(c.io.inputType, 1) // ask for double
//    poke(c.io.operand, 0) // ask for addition
//    for (i <- 0 to 121) {
//        poke(c.io.operand1, doubleInput1(i))
//        poke(c.io.operand2, doubleInput2(i))
//        step(1)
//        expect(c.io.result, doubleOutputAdd(i))
//    }

//    poke(c.io.operand, 1) // ask for multiplication
//    for (i <- 0 to 121) {
//        poke(c.io.operand1, doubleInput1(i))
//        poke(c.io.operand2, doubleInput2(i))
//        step(1)
//        expect(c.io.result, doubleOutputMul(i))
//    }

//    poke(c.io.operand, 2) // ask for converstion to uint
//    for (i <- 0 to 0) {
//        poke(c.io.operand1, doubleCastToUIntInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, doubleCastToUIntOutput(i))
//    }

//    poke(c.io.operand, 3) // ask for converstion to uint
//    for (i <- 0 to 1) {
//        poke(c.io.operand1, doubleCastToSIntInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, doubleCastToSIntOutput(i))
//    }

//    poke(c.io.operand, 4) // ask for converstion to double from uint
//    for (i <- 0 to 6) {
//        poke(c.io.operand1, uIntCastToDoubleInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, uIntCastToDoubleOutput(i))
//    }

//    poke(c.io.operand, 5) // ask for converstion to double from sint
//    for (i <- 0 to 20) {
//        poke(c.io.operand1, sIntCastToDoubleInput(i))
//        poke(c.io.operand2, 1.U)
//        step(1)
//        expect(c.io.result, sIntCastToDoubleOutput(i))
//    }

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
