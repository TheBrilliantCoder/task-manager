package com.badlogic.taskmanager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class CLITest {

  @Test
  void createTaskFromCLI() {
    Path path = Paths.get("data", "IT-01.json");
    TaskRepository repository = new TaskRepository(path);
    TaskManager manager = new TaskManager(repository);
    CLI cli = new CLI(manager);

    String input = """
      task add Integration test
      2026-09-23
      2026-09-24
      exit
      """;

    Scanner scanner = new Scanner(input);
    cli.applicationLoop(scanner);

    TaskRepository repo = new TaskRepository(path);
    Collection<Task> tasks = repo.findAll();

    boolean check = false;
    for (Task task : tasks) {
      if (task.getTitle().equals("Integration test") &&
          task.getStartTime().toString().equals("2026-09-23") &&
          task.getDueTime().toString().equals("2026-09-24")) {
        check = true;
        break;
      }
    }
    assertTrue(check);
  }

  @Test
  void userInteractionWithSystem() {
    Path path = Paths.get("data", "ST-01.json");
    TaskRepository repository = new TaskRepository(path);
    TaskManager manager = new TaskManager(repository);
    CLI cli = new CLI(manager);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    // ---------- Step 1: Create + List ----------
    String input1 = """
      task add System test
      2026-09-24
      2026-09-25
      task list
      """;

    cli.applicationLoop(new Scanner(input1));

    String result1 = output.toString();

    // Verify task was created and listed
    assertTrue(result1.contains("Created task"));
    assertTrue(result1.contains("System test"));
    assertTrue(result1.contains("DOING"));

    // ---------- Extract task ID ----------
    String taskLine = Arrays.stream(result1.split("\\R"))
      .map(String::trim)
      .filter(line -> line.matches("\\d+\\s+DOING\\s+System test"))
      .findFirst()
      .orElseThrow();

    int taskId = Integer.parseInt(taskLine.split("\\s+")[0]);

    // ---------- Step 2: Complete + List ----------
    output.reset();

    String input2 = """
      task done %d
      task list
      exit
      """.formatted(taskId);

    cli.applicationLoop(new Scanner(input2));

    String result2 = output.toString().trim();

    // Verify task was completed
    assertTrue(result2.contains("Marked task " + taskId + " as done"));

    // Verify the same task is now DONE
    String expectedDoneLine = taskId + " DONE System test";

    String normalizedResult = result2.replaceAll("\\s+", " ");

    assertTrue(normalizedResult.contains(expectedDoneLine));
  }

  @Test
  void inputInvalidTaskTitle() {
    Path path = Paths.get("data", "TC-05.json");
    TaskRepository repository = new TaskRepository(path);
    TaskManager manager = new TaskManager(repository);
    CLI cli = new CLI(manager);

    String input = """
      task add
      exit
      """;

    Scanner scanner = new Scanner(input);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    cli.applicationLoop(scanner);

    String result = output.toString().trim();
    assertTrue(result.contains("Error: Missing task title. (e.g., task add My Title)"));
  }
}
