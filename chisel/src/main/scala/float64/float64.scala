package float64

import chisel3._
import chisel3.util._

import floatingpoint._
import floatingpoint.FloatingPoint._

class Float64() extends FloatingPoint(Float64.exp, Float64.man) {

    override def cloneType = (new Float64()).asInstanceOf[this.type]

}


object Float64 {

    val exp = 11
    val man = 52

    implicit class UIntToFloat64(elem: UInt) {

        def toFloat64(): Float64 = {
            return (elem.toFloatingPoint(Float64.exp, Float64.man)).asTypeOf(new Float64)
        }
    }

    implicit class SIntToFloat64(elem: SInt) {

        def toFloat64(): Float64 = {
            return (elem.toFloatingPoint(Float64.exp, Float64.man)).asTypeOf(new Float64)
        }
    }

}
