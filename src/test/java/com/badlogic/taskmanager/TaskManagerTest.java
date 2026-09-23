package com.badlogic.taskmanager;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskManagerTest {

  @Test
  void createTaskWithValidInput() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "TC-01.json"));
    TaskManager manager = new TaskManager(repository);

    Task task = manager.createTask(
      "Junit test",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    assertEquals("Junit test", task.getTitle());
    assertEquals(LocalDate.of(2026, 9, 23), task.getStartTime());
    assertEquals(LocalDate.of(2026, 9, 24), task.getDueTime());
  }

  @Test
  void createTaskWithInvalidInput() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "TC-02.json"));
    TaskManager manager = new TaskManager(repository);

    assertThrows(IllegalArgumentException.class, () -> {
      Task task = manager.createTask(
        "",
        LocalDate.of(2026, 9, 23),
        LocalDate.of(2026, 9, 24)
      );
    });

    assertThrows(IllegalArgumentException.class, () -> {
      Task task = manager.createTask(
        "Junit test",
        LocalDate.of(2026, 9, 24),
        LocalDate.of(2026, 9, 23)
      );
    });
  }

  @Test
  void calculateTaskStatus() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "TC-03.json"));
    TaskManager manager = new TaskManager(repository);

    // TODO
    Task task1 = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 25),
      LocalDate.of(2026, 9, 26)
    );

    // DOING
    Task task2 = manager.createTask(
      "Junit test 2",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    // OVERDUE
    Task task3 = manager.createTask(
      "Junit test 3",
     LocalDate.of(2026, 9, 19),
      LocalDate.of(2026, 9, 20)
    );

    LocalDate date = LocalDate.now();

    assertEquals("TODO", task1.status(date));
    assertEquals("DOING", task2.status(date));
    assertEquals("OVERDUE", task3.status(date));

    manager.completeTask(task3.getId());
    assertEquals("DONE", task3.status(date));
  }

  @Test
  void calculateTaskStatusBoundary() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "TC-04.json"));
    TaskManager manager = new TaskManager(repository);

    Task task = manager.createTask(
      "Junit test",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    LocalDate date1 = LocalDate.of(2026, 9, 23);
    LocalDate date2 = LocalDate.of(2026, 9, 24);

    assertEquals("DOING", task.status(date1));
    assertEquals("DOING", task.status(date2));
  }


  // Other tests
  @Test
  void taskNotReuseId() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test3.json"));
    TaskManager manager = new TaskManager(repository);

    Task task1 = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    Task task2 = manager.createTask(
      "Junit test 2",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    int id = task2.getId();

    manager.deleteTask(id);

    Task task3 = manager.createTask(
      "Junit test 3",
     LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    assertNotEquals(id, task3.getId());
  }

  @Test
  void taskHasUniqueId() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test4.json"));
    TaskManager manager = new TaskManager(repository);

    Task task1 = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    Task task2 = manager.createTask(
      "Junit test 2",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    Task task3 = manager.createTask(
      "Junit test 3",
     LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    assertNotEquals(task1.getId(), task2.getId());
    assertNotEquals(task1.getId(), task3.getId());
    assertNotEquals(task2.getId(), task3.getId());
  }

  @Test
  void testListAllTasks() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test5.json"));
    TaskManager manager = new TaskManager(repository);

    Task task1 = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    Task task2 = manager.createTask(
      "Junit test 2",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    String result = manager.renderTaskList();
    assertTrue(result.contains("Junit test 1"));
    assertTrue(result.contains("Junit test 2"));
  }

  @Test
  void testEmptyList() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test6.json"));
    TaskManager manager = new TaskManager(repository);

    String result = manager.renderTaskList();
    assertTrue(result.contains("No tasks yet"));
  }

  @Test
  void completeExistingTask() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test7.json"));
    TaskManager manager = new TaskManager(repository);

    Task task = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    manager.completeTask(task.getId());
    assertEquals(true, task.isCompleted());
  }

  @Test
  void completeNonExistingTask() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test8.json"));
    TaskManager manager = new TaskManager(repository);

    TaskManager.CompletionResult result = manager.completeTask(1);
    assertEquals(TaskManager.CompletionResult.NOT_FOUND, result);
  }

  @Test
  void deleteExistingTask() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test9.json"));
    TaskManager manager = new TaskManager(repository);

    Task task = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    assertTrue(manager.deleteTask(task.getId()));
    String result = manager.renderTaskList();
    assertTrue(result.contains("No tasks yet"));
  }

  @Test
  void deleteNonExistingTask() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test10.json"));
    TaskManager manager = new TaskManager(repository);

    assertTrue(!manager.deleteTask(1));
  }

  @Test
  void remainTask() {
    TaskRepository repository = new TaskRepository(Paths.get("data", "test11.json"));
    TaskManager manager = new TaskManager(repository);

    Task task = manager.createTask(
      "Junit test 1",
      LocalDate.of(2026, 9, 23),
      LocalDate.of(2026, 9, 24)
    );

    TaskRepository repository1 = new TaskRepository(Paths.get("data", "test11.json"));
    TaskManager manager1 = new TaskManager(repository1);
    String result = manager1.renderTaskList();
    assertTrue(result.contains("Junit test 1"));
  }

}
