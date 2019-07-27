package float

import chisel3._
import chisel3.util._

object Float {

    def init(input: UInt): Float = {
        val res = Wire(new Float())
        res.sign := input(31)
        res.exponent := input(30, 23)
        res.mantissa := input(22, 0)
        return res
    }

}

class Float() extends Bundle {

    val sign = Bool()
    val exponent = UInt(8.W)
    val mantissa = UInt(23.W)

    def shift32RightJamming(a: UInt, count: UInt): UInt = {
        val res = Wire(UInt())
        when(count === 0.U) {
            res := a
        }
        .elsewhen(count < 32.U) {
            res := (a >> count) | ((a << ((-(count.asSInt())).asUInt & 31.U)) =/= 0.U)
        }
        .otherwise {
            res := (a =/= 0.U)
        }
        return res
    }

    def expDiffNotZero(exponent: UInt, diff: UInt, frac: UInt): UInt = {
        val fracIntermediate = Wire(UInt())
        val diffOut = Wire(UInt())
        when(exponent === 0.U) {
            diffOut := diff-1.U
            fracIntermediate := frac
        }
        .otherwise {
            diffOut := diff
            fracIntermediate := frac | "h20000000".U
        }
        val fracOut = shift32RightJamming(fracIntermediate, diffOut)
        return fracIntermediate
    }

    def endOfCondition(aFrac: UInt, bFrac: UInt, zExp: UInt): UInt = {
        val aFracOut = aFrac | "h20000000".U
        val zFracOut = Wire(UInt())
        val zExpOut = Wire(UInt())
        when(zFracOut < 0.U) {
            zFracOut := aFrac + bFrac
            zExpOut := zExp + 1.U
        }
        .otherwise {
            zFracOut := (aFrac + bFrac) << 1.U
            zExpOut := zExp - 1.U
        }
        return Cat(this.exponent, ((zExp << 23.U) + (zFracOut >> 7.U)))
    }

    def +(that: Float): Float = {
        val res = Wire(UInt())
        when((this.exponent-that.exponent) > 0.U) {
            res := endOfCondition(this.mantissa, expDiffNotZero(that.exponent, this.exponent-that.exponent, that.mantissa << 6.U), this.exponent)
        }
        .elsewhen((this.exponent-that.exponent) < 0.U) {
            res := endOfCondition(expDiffNotZero(this.exponent, this.exponent-that.exponent, this.mantissa << 6.U), that.mantissa, that.exponent)
        }
        .elsewhen(this.exponent === 0.U) {
            res := Cat(this.exponent, 0.U(8.W), (this.mantissa+that.mantissa))
        }
        .otherwise {
            res := Cat(this.exponent, (this.exponent << 23.U)+(("h40000000".U + this.mantissa + that.mantissa) >> 7.U))
        }
        return res.asTypeOf(new Float())
    }
}
