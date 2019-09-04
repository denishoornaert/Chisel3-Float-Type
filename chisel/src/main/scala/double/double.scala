package double

import chisel3._
import chisel3.util._

import floatingpoint._
import floatingpoint.FloatingPoint._

class Double() extends FloatingPoint(11, 52) {

    override def cloneType = (new Double()).asInstanceOf[this.type]

}


object Double {

    implicit class UIntToDouble(elem: UInt) {

        def toDouble(): Double = {
            return (elem.toFloatingPoint(11, 52)).asTypeOf(new Double)
        }
    }

    implicit class SIntToDouble(elem: SInt) {

        def toDouble(): Double = {
            return (elem.toFloatingPoint(11, 52)).asTypeOf(new Double)
        }
    }

}
