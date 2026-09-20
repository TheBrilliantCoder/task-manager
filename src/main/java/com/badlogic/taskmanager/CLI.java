package com.badlogic.taskmanager;

import java.time.LocalDate;
import java.util.Scanner;

class CLI {
  private TaskManager manager;

  CLI() {
    manager = new TaskManager();
  }

  void add(String title, Scanner scanner) {
    System.out.print("Enter start time (yyyy-mm-dd): ");
    String startTimeStr = scanner.nextLine();
    LocalDate startTime = LocalDate.parse(startTimeStr);

    System.out.print("Enter due time (yyyy-mm-dd): ");
    String dueTimeStr = scanner.nextLine();
    LocalDate dueTime = LocalDate.parse(dueTimeStr);

    manager.createTask(title, startTime, dueTime);
    System.out.println("Created a task successfully");
  }

  void done(int taskid) {
    manager.completeTask(taskid);
    System.out.println("Marked task " + taskid + " as done.");
  }

  void list() {
    manager.listAllTasks();
  }

  void delete(int taskid) {
    manager.deleteTask(taskid);
    System.out.println("Deleted task " + taskid + " from the list.");
  }

  void applicationLoop(Scanner scanner) {
    System.out.println("Task Manager CLI started. (Type 'exit' to quit)");

    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("exit")) {
        System.out.println("Goodbye!");
        break;
      }

      // Split the input into a maximum of 3 parts.
      // Example: "task add Learn Java" -> ["task", "add", "Learn Java"]
      String[] parts = input.split("\\s+", 3);

      // Validate that it starts with "task" and has at least an action
      if (parts.length < 2 || !parts[0].equalsIgnoreCase("task")) {
        System.out.println("Invalid format! Use: task <add|list|done|delete> [argument]");
        continue;
      }

      String action = parts[1].toLowerCase();
      // If there is a 3rd part, that's the argument. Otherwise, it's an empty string.
      String argument = (parts.length == 3) ? parts[2] : "";

      try {
        switch (action) {
          case "add":
            if (argument.isEmpty()) {
              System.out.println("Error: Missing task title. (e.g., task add My Title)");
            } else {
              add(argument, scanner);
            }
            break;

          case "delete":
            if (argument.isEmpty()) {
              System.out.println("Error: Missing task ID. (e.g., task delete 1)");
            } else {
              int deleteId = Integer.parseInt(argument);
              delete(deleteId);
            }
            break;

          case "done":
            if (argument.isEmpty()) {
              System.out.println("Error: Missing task ID. (e.g., task done 1)");
            } else {
              int doneId = Integer.parseInt(argument);
              done(doneId);
            }
            break;

          case "list":
            list();
            break;

          default:
            System.out.println("Invalid command! Unknown action: " + action);
        }
      } catch (Exception e) {
        System.out.println("An unexpected error occurred: " + e.getMessage());
      }
    }
    return;
  }

}
