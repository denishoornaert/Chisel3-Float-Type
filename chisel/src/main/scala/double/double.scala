package double

import chisel3._
import chisel3.util._

object Double {

    val nan :: an :: ninfinity :: infinity :: Nil = Enum(4)

}

class Double() extends Bundle {

    val sign = Bool()
    val exponent = UInt(11.W)
    val mantissa = UInt(52.W)

    def identify(f: Double): UInt = {
        val res = Wire(UInt(2.W))
        when(f.exponent === "h7ff".U) {
            when(f.mantissa === 0.U) {
                when(f.sign === 0.U) {
                    res := Double.infinity
                }
                .otherwise{
                    res := Double.ninfinity
                }
            }
            .otherwise{
                res := Double.nan
            }
        }
        .otherwise {
            res := Double.an
        }
        return res
    }

    def absoluteValue(a: SInt): UInt = {
        val res = Wire(UInt())
        when(a < 0.S) {
            res := (-a).asUInt
        }
        .otherwise {
            res := a.asUInt
        }
        return res
    }

    def toInt(a: UInt, s: UInt): SInt = {
        val res = Wire(SInt())
        when(s(0).toBool) {
            res := -(a.asSInt)
        }
        .otherwise{
            res := a.asSInt
        }
        return res
    }

    def +(that: Double): Double = {
        // SETUP
        val res = Wire(new Double())
        val Aidentity = Wire(UInt(2.W))
        val Bidentity = Wire(UInt(2.W))
        Aidentity := identify(this)
        Bidentity := identify(that)
        when((Aidentity === Double.infinity & Bidentity === Double.ninfinity)|(Bidentity === Double.infinity & Aidentity === Double.ninfinity)) {
            res := ("hfff8000000000000".U).asTypeOf(new Double)
        }
        .elsewhen(Aidentity === Double.infinity | Aidentity === Double.ninfinity) {
            res := this
        }
        .elsewhen(Bidentity === Double.infinity | Bidentity === Double.ninfinity) {
            res := that
        }
        .otherwise {
            // INIT
            val exponent   = Wire(UInt(11.W))
            val difference = Wire(UInt(11.W))
            val a_mantissa = Wire(UInt(55.W))
            val b_mantissa = Wire(UInt(55.W))
            val mantissa   = Wire(UInt(55.W))
            val sum        = Wire(SInt(55.W))
            difference := absoluteValue((this.exponent).asSInt-(that.exponent).asSInt)
            // SHIFT EXPONENT
            when(this.exponent > that.exponent) {
                exponent := this.exponent
                a_mantissa := Cat(1.U(1.W), this.mantissa)
                b_mantissa := Cat(1.U(1.W), that.mantissa) >> difference
            }
            .otherwise{
                exponent := that.exponent;
                a_mantissa := Cat(1.U(1.W), this.mantissa) >> difference
                b_mantissa := Cat(1.U(1.W), that.mantissa)
            }
            sum := toInt(a_mantissa, this.sign)+toInt(b_mantissa, that.sign)
            mantissa := absoluteValue(sum)
            // MANTISSA COMPUTATION
            res.mantissa := Mux(mantissa(53), ((mantissa)+1.U)>>1.U, mantissa)(51, 0)
            // SIGN MANAGMENT
            res.sign := (sum < 0.S)
            // EXPONENT MANAGMENT
            res.exponent := Mux(sum =/= 0.S, exponent, 0.U)+Cat(0.U(10.W), mantissa(53))
        }
        return res
    }

    def *(that: Double): Double = {
        // SETUP
        val res = Wire(new Double())
        val Aidentity = Wire(UInt(2.W))
        val Bidentity = Wire(UInt(2.W))
        Aidentity := identify(this)
        Bidentity := identify(that)
        when(Aidentity === Double.infinity | Aidentity === Double.ninfinity | Bidentity === Double.infinity | Bidentity === Double.ninfinity) {
            res := (Cat((this.sign^that.sign), "h7ff0000000000000".U(63.W))).asTypeOf(new Double)
        }
        .elsewhen(Aidentity === Double.nan) {
            res := (Cat(this.sign, "h7ff8000000000000".U(63.W))).asTypeOf(new Double)
        }
        .elsewhen(Bidentity === Double.nan) {
            res := (Cat(that.sign, "h7ff8000000000000".U(63.W))).asTypeOf(new Double)
        }
        .elsewhen(this.asUInt === 0.U | that.asUInt === 0.U | this.asUInt === "h8000000000000000".U | that.asUInt === "h8000000000000000".U) {
            res := ("h0000000000000000".U).asTypeOf(new Double)
        }
        .otherwise {
            res.sign := (this.sign^that.sign)
            val mantissa = (Cat(1.U(1.W), this.mantissa)*Cat(1.U(1.W), that.mantissa)) >> 52.U
            res.mantissa := Mux(mantissa(53).asBool, (mantissa+1.U)>>1.U, mantissa)(51, 0)
            val exponent = this.exponent+that.exponent
            res.exponent := exponent-1023.U+mantissa(53)
        }
        return res
    }

}
