package double

import chisel3._
import chisel3.util._

import floatingpoint._

class Double() extends FloatingPoint(11, 52) {

    override def cloneType = (new Double()).asInstanceOf[this.type]

}


object Double {

    implicit class UIntToDouble(elem: UInt) {

        def toDouble(): Double = {
            val res = Wire(new Double)
            val shifts = FloatingPoint.countZerosFromTheLeft(elem(51, 0))
            printf(p"shifts = ${shifts}\n")
            res.mantissa := elem << shifts
            res.exponent := (1023+52).U-shifts
            res.sign := 0.U
            return res
        }
    }
}
