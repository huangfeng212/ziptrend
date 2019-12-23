package com.example.trend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Demo {

  public static final Random RANDOM = new Random();
  public static final int P_CHANGE = 20;
  public static final int D_CHANGE = 2;
  public static final int[] int0 = new int[1];

  static List<RawPoint> raw() {
    int0[0] = 0;
    return LongStream.range(0, 3600).boxed().map(aLong -> {
      boolean shouldChange = RANDOM.nextInt(100) < P_CHANGE;
      if (shouldChange) {
        boolean shouldAdd = RANDOM.nextBoolean();
        if (shouldAdd) {
          int0[0] = int0[0] + RANDOM.nextInt(D_CHANGE);
        } else {
          int0[0] = int0[0] - RANDOM.nextInt(D_CHANGE);
        }
      }
      return new RawPoint(aLong, int0[0]);
    }).collect(Collectors.toList());
  }

  public static void main(String[] args) throws FileNotFoundException {
    final List<RawPoint> raw = raw();
    final List<Point> zip = zip(raw);
    final List<RawPoint> unzip = unzip(zip);
    try (PrintWriter pw = new PrintWriter(new File("raw.csv"))) {
      raw.stream()
          .map(RawPoint::toString)
          .forEach(pw::println);
    }
    try (PrintWriter pw = new PrintWriter(new File("zip.csv"))) {
      zip.stream()
          .map(Point::toString)
          .forEach(pw::println);
    }
    try (PrintWriter pw = new PrintWriter(new File("unzip.csv"))) {
      unzip.stream()
          .map(RawPoint::toString)
          .forEach(pw::println);
    }
  }

  static List<Point> zip(List<RawPoint> rawPoints) {
    Stack<Point> stack = new Stack<>();
    rawPoints.forEach(rawPoint -> {
      if (stack.isEmpty()) {
        stack.push(new Point(rawPoint.ts, rawPoint.val, 1));
      } else {
        final Point peek = stack.peek();
        if (peek.val == rawPoint.val) {
          peek.dup++;
        } else {
          stack.push(new Point(rawPoint.ts, rawPoint.val, 1));
        }
      }
    });
    return new ArrayList<>(stack);
  }

  static List<RawPoint> unzip(List<Point> points) {
    List<RawPoint> rawPoints = new ArrayList<>();
    points.forEach(point -> {
      while (point.dup-- > 0) {
        rawPoints.add(new RawPoint(point.ts++, point.val));
      }
    });
    return rawPoints;
  }
}
