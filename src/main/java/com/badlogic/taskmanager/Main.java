package com.badlogic.taskmanager;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {

      // Constructed inside the guard: loading the data file can fail too.
      new CLI().applicationLoop(scanner);

    } catch (RuntimeException e) {
      System.err.println("Fatal error: " + e.getMessage());
      System.exit(1);
    }
  }
}
