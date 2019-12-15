package float32

import chisel3._
import chisel3.util._

import floatingpoint._
import floatingpoint.FloatingPoint._

class Float32() extends FloatingPoint(Float32.exp, Float32.man) {

    override def cloneType = (new Float32()).asInstanceOf[this.type]

}

object Float32 {

    val exp = 8
    val man = 23

    implicit class UIntToFloat32(elem: UInt) {

        def toFloat32(): Float32 = {
            return (elem.toFloatingPoint(Float32.exp, Float32.man)).asTypeOf(new Float32)
        }
    }

    implicit class SIntToFloat32(elem: SInt) {

        def toFloat32(): Float32 = {
            return (elem.toFloatingPoint(Float32.exp, Float32.man)).asTypeOf(new Float32)
        }
    }

}
