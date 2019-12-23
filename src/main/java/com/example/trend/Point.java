package com.example.trend;

import java.util.StringJoiner;

public class Point {

  long ts;
  int val;
  long dup;

  public Point(long ts, int val, long dup) {
    this.ts = ts;
    this.val = val;
    this.dup = dup;
  }

  @Override
  public String toString() {
    return ts + "," + val + "," + dup;
  }
}
