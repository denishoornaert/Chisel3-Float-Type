package double

import chisel3._
import chisel3.util._

import floatingpoint._
import floatingpoint.FloatingPoint._

class Double() extends FloatingPoint(Double.exp, Double.man) {

    override def cloneType = (new Double()).asInstanceOf[this.type]

}


object Double {

    val exp = 11
    val man = 52

    implicit class UIntToDouble(elem: UInt) {

        def toDouble(): Double = {
            return (elem.toFloatingPoint(Double.exp, Double.man)).asTypeOf(new Double)
        }
    }

    implicit class SIntToDouble(elem: SInt) {

        def toDouble(): Double = {
            return (elem.toFloatingPoint(Double.exp, Double.man)).asTypeOf(new Double)
        }
    }

}
