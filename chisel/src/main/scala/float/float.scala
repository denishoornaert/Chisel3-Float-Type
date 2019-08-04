package float

import chisel3._
import chisel3.util._

object Float {

    val nan :: an :: ninfinity :: infinity :: Nil = Enum(4)

}

class Float() extends Bundle {

    val sign = Bool()
    val exponent = UInt(8.W)
    val mantissa = UInt(23.W)

    def identify(f: Float): UInt = {
        val res = Wire(UInt(2.W))
        when(f.exponent === "hff".U) {
            when(f.mantissa === 0.U) {
                when(f.sign === 0.U) {
                    res := Float.infinity
                }
                .otherwise{
                    res := Float.ninfinity
                }
            }
            .otherwise{
                res := Float.nan
            }
        }
        .otherwise {
            res := Float.an
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

    def +(that: Float): Float = {
        // SETUP
        val res = Wire(new Float())
        val Aidentity = Wire(UInt(2.W))
        val Bidentity = Wire(UInt(2.W))
        Aidentity := identify(this)
        Bidentity := identify(that)
        when((Aidentity === Float.infinity & Bidentity === Float.ninfinity)|(Bidentity === Float.infinity & Aidentity === Float.ninfinity)) {
            res := ("hffc00000".U).asTypeOf(new Float)
        }
        .elsewhen(Aidentity === Float.infinity | Aidentity === Float.ninfinity) {
            res := this
        }
        .elsewhen(Bidentity === Float.infinity | Bidentity === Float.ninfinity) {
            res := that
        }
        .otherwise {
            // INIT
            val exponent   = Wire(UInt(8.W))
            val difference = Wire(UInt(8.W))
            val a_mantissa = Wire(UInt(26.W))
            val b_mantissa = Wire(UInt(26.W))
            val mantissa   = Wire(UInt(26.W))
            val sum        = Wire(SInt(26.W))
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
            res.mantissa := Mux(mantissa(24), ((mantissa)+1.U)>>1.U, mantissa)(22, 0)
            // SIGN MANAGMENT
            res.sign := (sum < 0.S)
            // EXPONENT MANAGMENT
            res.exponent := Mux(sum =/= 0.S, exponent, 0.U)+Cat(0.U(7.W), mantissa(24))
        }
        return res
    }

    def *(that: Float): Float = {
        // SETUP
        val res = Wire(new Float())
        val Aidentity = Wire(UInt(2.W))
        val Bidentity = Wire(UInt(2.W))
        Aidentity := identify(this)
        Bidentity := identify(that)
        when(Aidentity === Float.infinity | Aidentity === Float.ninfinity | Bidentity === Float.infinity | Bidentity === Float.ninfinity) {
            res := (Cat((this.sign^that.sign), "h7f800000".U(31.W))).asTypeOf(new Float)
        }
        .elsewhen(Aidentity === Float.nan) {
            res := (Cat(this.sign, "h7fc00000".U(31.W))).asTypeOf(new Float)
        }
        .elsewhen(Bidentity === Float.nan) {
            res := (Cat(that.sign, "h7fc00000".U(31.W))).asTypeOf(new Float)
        }
        .elsewhen(this.asUInt === 0.U | that.asUInt === 0.U | this.asUInt === "h80000000".U | that.asUInt === "h80000000".U) {
            res := (0.U).asTypeOf(new Float)
        }
        .otherwise {
            res.sign := (this.sign^that.sign)
            val mantissa = (Cat(1.U(1.W), this.mantissa)*Cat(1.U(1.W), that.mantissa)) >> 23.U
            res.mantissa := Mux(mantissa(24).asBool, (mantissa+1.U)>>1.U, mantissa)(22, 0)
            val exponent = this.exponent+that.exponent
            res.exponent := exponent-127.U+mantissa(24)
        }
        return res
    }

}
