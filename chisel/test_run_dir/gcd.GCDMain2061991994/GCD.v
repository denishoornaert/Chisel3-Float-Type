module GCD(
  input         clock,
  input         reset,
  input  [15:0] io_value1,
  input  [15:0] io_value2,
  input         io_loadingValues,
  output [15:0] io_outputGCD,
  output        io_outputValid
);
  reg [15:0] x; // @[GCD.scala 21:15]
  reg [31:0] _RAND_0;
  reg [15:0] y; // @[GCD.scala 22:15]
  reg [31:0] _RAND_1;
  wire  _T_17; // @[GCD.scala 24:10]
  wire [16:0] _T_18; // @[GCD.scala 24:24]
  wire [16:0] _T_19; // @[GCD.scala 24:24]
  wire [15:0] _T_20; // @[GCD.scala 24:24]
  wire [16:0] _T_21; // @[GCD.scala 25:25]
  wire [16:0] _T_22; // @[GCD.scala 25:25]
  wire [15:0] _T_23; // @[GCD.scala 25:25]
  wire [15:0] _GEN_0; // @[GCD.scala 24:15]
  wire [15:0] _GEN_1; // @[GCD.scala 24:15]
  assign _T_17 = x > y; // @[GCD.scala 24:10]
  assign _T_18 = x - y; // @[GCD.scala 24:24]
  assign _T_19 = $unsigned(_T_18); // @[GCD.scala 24:24]
  assign _T_20 = _T_19[15:0]; // @[GCD.scala 24:24]
  assign _T_21 = y - x; // @[GCD.scala 25:25]
  assign _T_22 = $unsigned(_T_21); // @[GCD.scala 25:25]
  assign _T_23 = _T_22[15:0]; // @[GCD.scala 25:25]
  assign _GEN_0 = _T_17 ? _T_20 : x; // @[GCD.scala 24:15]
  assign _GEN_1 = _T_17 ? y : _T_23; // @[GCD.scala 24:15]
  assign io_outputGCD = x; // @[GCD.scala 32:16]
  assign io_outputValid = y == 16'h0; // @[GCD.scala 33:18]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  x = _RAND_0[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  y = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (io_loadingValues) begin
      x <= io_value1;
    end else begin
      if (_T_17) begin
        x <= _T_20;
      end
    end
    if (io_loadingValues) begin
      y <= io_value2;
    end else begin
      if (!(_T_17)) begin
        y <= _T_23;
      end
    end
  end
endmodule
