package float

import chisel3._
import chisel3.util._

object Float {

    val nan :: an :: ninfinity :: infinity :: Nil = Enum(4)

    def init(input: UInt): Float = {
        val res = Wire(new Float())
        res.sign := input(31)
        res.exponent := input(30, 23)
        res.mantissa := input(22, 0)
        return res
    }

    def identify(f: Float): UInt = {
        val res = Wire(UInt(2.W))
        when(f.exponent === "hff".U) {
            when(f.mantissa === 0.U) {
                when(f.sign === 0.U) {
                    res := infinity
                }
                .otherwise{
                    res := ninfinity
                }
            }
            .otherwise{
                res := nan
            }
        }
        .otherwise {
            res := an
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

    def add(a: Float, b: Float): Float = {
        // SETUP
        val res = Wire(new Float())
        val Aidentity = Wire(UInt(2.W))
        val Bidentity = Wire(UInt(2.W))
        Aidentity := identify(a)
        Bidentity := identify(b)
        when((Aidentity === infinity & Bidentity === ninfinity)|(Bidentity === infinity & Aidentity === ninfinity)) {
            res := ("hffc00000".U).asTypeOf(new Float)
        }
        .elsewhen(Aidentity === infinity | Aidentity === ninfinity) {
            res := a
        }
        .elsewhen(Bidentity === infinity | Bidentity === ninfinity) {
            res := b
        }
        .otherwise {
            // INIT
            val exponent   = Wire(UInt(8.W))
            val difference = Wire(UInt(8.W))
            val a_mantissa = Wire(UInt(26.W))
            val b_mantissa = Wire(UInt(26.W))
            val mantissa   = Wire(UInt(26.W))
            val sum        = Wire(SInt(26.W))
            difference := absoluteValue((a.exponent).asSInt-(b.exponent).asSInt)
            // SHIFT EXPONENT
            when(a.exponent > b.exponent) {
                exponent := a.exponent
                a_mantissa := Cat(1.U(1.W), a.mantissa)
                b_mantissa := Cat(1.U(1.W), b.mantissa) >> difference
            }
            .otherwise{
                exponent := b.exponent;
                a_mantissa := Cat(1.U(1.W), a.mantissa) >> difference
                b_mantissa := Cat(1.U(1.W), b.mantissa)
            }
            sum := toInt(a_mantissa, a.sign)+toInt(b_mantissa, b.sign)
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

    def mul(a: Float, b: Float): Float = {
        // SETUP
        val res = Wire(new Float())
        val Aidentity = Wire(UInt(2.W))
        val Bidentity = Wire(UInt(2.W))
        Aidentity := identify(a)
        Bidentity := identify(b)
        when(Aidentity === infinity | Aidentity === ninfinity | Bidentity === infinity | Bidentity === ninfinity) {
            res := (Cat((a.sign^b.sign), "h7f800000".U(31.W))).asTypeOf(new Float)
        }
        .elsewhen(Aidentity === nan) {
            res := (Cat(a.sign, "h7fc00000".U(31.W))).asTypeOf(new Float)
        }
        .elsewhen(Bidentity === nan) {
            res := (Cat(b.sign, "h7fc00000".U(31.W))).asTypeOf(new Float)
        }
        .elsewhen(a.asUInt === 0.U | b.asUInt === 0.U | a.asUInt === "h80000000".U | b.asUInt === "h80000000".U) {
            res := (0.U).asTypeOf(new Float)
        }
        .otherwise {
            res.sign := (a.sign^b.sign)
            val mantissa = (Cat(1.U(1.W), a.mantissa)*Cat(1.U(1.W), b.mantissa)) >> 23.U
            res.mantissa := Mux(mantissa(24).asBool, (mantissa+1.U)>>1.U, mantissa)(22, 0)
            val exponent = a.exponent+b.exponent
            res.exponent := exponent-127.U+mantissa(24)
        }
        return res
    }

}

class Float() extends Bundle {

    val sign = Bool()
    val exponent = UInt(8.W)
    val mantissa = UInt(23.W)

}
