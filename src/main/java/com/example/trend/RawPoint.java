package com.example.trend;

import java.util.StringJoiner;

public class RawPoint {

  long ts;
  int val;

  public RawPoint(long ts, int val) {
    this.ts = ts;
    this.val = val;
  }

  @Override
  public String toString() {
    return ts + "," + val;
  }
}
