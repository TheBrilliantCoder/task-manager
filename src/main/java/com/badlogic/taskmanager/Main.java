package com.badlogic.taskmanager;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {

    LocalDate date1 = LocalDate.of(2026, 9, 19);
    LocalDate date2 = LocalDate.of(2026, 9, 20);
    LocalDate date3 = LocalDate.of(2026, 9, 21);
    LocalDate date4 = LocalDate.of(2026, 9, 22);
    LocalDate date5 = LocalDate.of(2026, 9, 23);
    LocalDate date6 = LocalDate.of(2026, 9, 24);
    LocalDate date7 = LocalDate.of(2026, 9, 25);
    LocalDate date8 = LocalDate.of(2026, 9, 26);

    TaskManager manager = new TaskManager();
    manager.createTask("Specification practice", date1, date2);
    manager.createTask("Design practice", date3, date4);
    manager.createTask("Implementation practice", date5, date6);
    manager.createTask("Testing practice", date7, date8);
    manager.createTask("Evolution practice", date5, date8);
    manager.listAllTasks();

    Scanner scanner = new Scanner(System.in);
    int id = 0;

    System.out.print("Enter task ID to mark as completed: ");
    id = scanner.nextInt();
    scanner.nextLine();
    manager.completeTask(id);
    manager.listAllTasks();

    System.out.print("Enter task ID to delete: ");
    id = scanner.nextInt();
    scanner.nextLine();
    manager.deleteTask(id);
    manager.listAllTasks();

    scanner.close();
  }
}
