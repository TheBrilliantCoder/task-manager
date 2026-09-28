package com.badlogic.taskmanager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Reads commands, delegates to TaskManager, prints the outcome.
class CLI {
  private static final int MAX_DATE_ATTEMPTS = 3;
  private final TaskManager manager;

  CLI() {
    this(new TaskManager());
  }

  CLI(TaskManager manager) {
    this.manager = manager;
  }

  void applicationLoop(Scanner scanner) {
    System.out.println("Task Manager CLI started. (Type 'exit' to quit)");

    while (true) {
      System.out.print("> ");
      if (!scanner.hasNextLine()) { // end of input (Ctrl-D or a piped script)
        System.out.println();
        break;
      }

      String input = scanner.nextLine().trim();
      if (input.isEmpty()) {
        continue;
      }
      if (input.equalsIgnoreCase("exit")) {
        System.out.println("Goodbye!");
        break;
      }

      handleCommand(input, scanner);
    }
  }

  private void handleCommand(String input, Scanner scanner) {
    // "task add Learn Java" -> ["task", "add", "Learn Java"]
    String[] parts = input.split("\\s+", 3);
    if (parts.length < 2 || !parts[0].equalsIgnoreCase("task")) {
      System.out.println("Invalid format! Use: task <add|list|done|delete> [argument]");
      return;
    }

    String action = parts[1].toLowerCase();
    String argument = (parts.length == 3) ? parts[2] : "";

    try {
      switch (action) {
        case "add" -> add(argument, scanner);
        case "delete" -> delete(argument);
        case "done" -> done(argument);
        case "edit" -> edit(argument, scanner);
        case "list" -> System.out.print(manager.renderTaskList());
        default -> System.out.println(
            "Invalid command! Unknown action: " + action + ". Use add, list, done, edit or delete.");
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    } catch (RuntimeException e) {
      System.out.println("An unexpected error occurred: " + e.getMessage());
    }
  }

  private void edit(String argument, Scanner scanner) {
    Integer taskId = parseId(argument, "task edit 1");
    if (taskId == null) {
      return;
    }

    String titleInput = readLine("Enter new title (ENTER to keep): ", scanner);
    if (titleInput == null) {
      System.out.println("Edit cancelled.");
      return;
    }
    String title = titleInput.isEmpty() ? null : titleInput;

    String dueInput = readLine("Enter new due time (yyyy-mm-dd, ENTER to keep): ", scanner);
    if (dueInput == null) {
      System.out.println("Invalid title. Edit cancelled.");
      return;
    }

    LocalDate dueTime = null;
    if (!dueInput.isEmpty()) {
      try {
        dueTime = LocalDate.parse(dueInput);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date. Use the format yyyy-mm-dd. Edit cancelled.");
        return;
      }
    }

    switch (manager.editTask(taskId, title, dueTime)) {
      case UPDATED -> System.out.println("Updated task " + taskId + ".");
      case NO_CHANGES -> System.out.println("Nothing to update for task " + taskId + ".");
      case NOT_FOUND -> System.out.println("No task found with ID " + taskId + ".");
    }
  }

  private void add(String title, Scanner scanner) {
    if (title.isEmpty()) {
      System.out.println("Error: Missing task title. (e.g., task add My Title)");
      return;
    }

    LocalDate startTime = readDate("Enter start time (yyyy-mm-dd): ", scanner);
    if (startTime == null) {
      return;
    }
    LocalDate dueTime = readDate("Enter due time (yyyy-mm-dd): ", scanner);
    if (dueTime == null) {
      return;
    }

    Task task = manager.createTask(title, startTime, dueTime);
    System.out.println("Created task " + task.getId() + ": " + task.getTitle());
  }

  private void done(String argument) {
    Integer taskId = parseId(argument, "task done 1");
    if (taskId == null) {
      return;
    }

    switch (manager.completeTask(taskId)) {
      case COMPLETED -> System.out.println("Marked task " + taskId + " as done.");
      case ALREADY_DONE -> System.out.println("Task " + taskId + " was already done.");
      case NOT_FOUND -> System.out.println("No task found with ID " + taskId + ".");
    }
  }

  private void delete(String argument) {
    Integer taskId = parseId(argument, "task delete 1");
    if (taskId == null) {
      return;
    }

    if (manager.deleteTask(taskId)) {
      System.out.println("Deleted task " + taskId + " from the list.");
    } else {
      System.out.println("No task found with ID " + taskId + ".");
    }
  }

  // return the parsed ID, or null after printing why it could not be read.
  private Integer parseId(String argument, String example) {
    if (argument.isEmpty()) {
      System.out.println("Error: Missing task ID. (e.g., " + example + ")");
      return null;
    }
    try {
      return Integer.valueOf(argument.trim());
    } catch (NumberFormatException e) {
      System.out.println("Error: '" + argument + "' is not a valid task ID.");
      return null;
    }
  }

  private String readLine(String prompt, Scanner scanner) {
    System.out.print(prompt);
    return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
  }

  private LocalDate readDate(String prompt, Scanner scanner) {
    for (int attempt = 0; attempt < MAX_DATE_ATTEMPTS; attempt++) {
      String line = readLine(prompt, scanner);
      if (line == null) {
        return null;
      }

      try {
        return LocalDate.parse(line);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date. Use the format yyyy-mm-dd, e.g. 2026-09-20.");
      }
    }

    System.out.println("Cancelled: too many invalid dates.");
    return null;
  }

}
