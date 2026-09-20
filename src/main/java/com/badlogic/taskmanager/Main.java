package com.badlogic.taskmanager;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    CLI app = new CLI();
    Scanner scanner = new Scanner(System.in);

    app.applicationLoop(scanner);

    scanner.close();
  }
}
