package float

import chisel3._
import chisel3.util._

import floatingpoint._
import floatingpoint.FloatingPoint._

class Float() extends FloatingPoint(Float.exp, Float.man) {

    override def cloneType = (new Float()).asInstanceOf[this.type]

}

object Float {

    val exp = 8
    val man = 23

    implicit class UIntToFloat(elem: UInt) {

        def toFloat(): Float = {
            return (elem.toFloatingPoint(Float.exp, Float.man)).asTypeOf(new Float)
        }
    }

    implicit class SIntToFloat(elem: SInt) {

        def toFloat(): Float = {
            return (elem.toFloatingPoint(Float.exp, Float.man)).asTypeOf(new Float)
        }
    }

}
