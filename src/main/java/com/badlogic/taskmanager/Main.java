package com.badlogic.taskmanager;

import java.time.LocalDate;

public class Main {
  public static void main(String[] args) {

    // TODO
    LocalDate date1 = LocalDate.of(2026, 9, 20);
    LocalDate date2 = LocalDate.of(2026, 9, 22);

    // DOING
    LocalDate date3 = LocalDate.of(2026, 9, 16);
    LocalDate date4 = LocalDate.of(2026, 9, 23);

    // OVERDUE
    LocalDate date5 = LocalDate.of(2026, 9, 17);
    LocalDate date6 = LocalDate.of(2026, 9, 18);

    // test createTask
    TaskManager manager = new TaskManager();
    manager.createTask(1, "Specification practice", date1, date2);
    manager.createTask(2, "Design practice", date3, date4);
    manager.createTask(3, "Implementation practice", date5, date6);
    manager.listTasks();

    // test completeTask and calculateStatus
    System.out.println("\n");
    manager.completeTask(3);
    manager.listTasks();

    // test deleteTask
    manager.deleteTask(2);
    System.out.println("\n");
    manager.listTasks();
  }
}
