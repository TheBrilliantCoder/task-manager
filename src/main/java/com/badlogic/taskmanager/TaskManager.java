package com.badlogic.taskmanager;

import java.time.LocalDate;
import java.util.Collection;

/*
 * Application logic. Returns results to the caller instead of printing, so the
 * CLI can report what actually happened (a missing ID used to be reported as a
 * success).
 */
class TaskManager {
  // Outcome of a completion attempt.
  enum CompletionResult {
    COMPLETED,
    ALREADY_DONE,
    NOT_FOUND
  }

  enum EditResult {
    UPDATED,
    NO_CHANGES,
    NOT_FOUND
  }

  private final TaskRepository repository;

  TaskManager() {
    this(new TaskRepository());
  }

  TaskManager(TaskRepository repository) {
    this.repository = repository;
  }

  /*
   * throw IllegalArgumentException if the title is blank or the dates are
   * the wrong way round.
   */
  Task createTask(String title, LocalDate startTime, LocalDate dueTime) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("A task needs a title.");
    }

    requireValidRange(startTime, dueTime);

    Task task = new Task(repository.nextId(), title.trim(), startTime, dueTime);
    repository.add(task);
    return task;
  }

  boolean deleteTask(int taskId) {
    return repository.remove(taskId);
  }

  CompletionResult completeTask(int taskId) {
    Task task = repository.find(taskId);
    if (task == null) {
      return CompletionResult.NOT_FOUND;
    }
    if (task.isCompleted()) {
      return CompletionResult.ALREADY_DONE;
    }

    task.setCompleted(true);
    repository.update(task);
    return CompletionResult.COMPLETED;
  }

  // Renders the whole list into one string: one clock read, one print call.
  String renderTaskList() {
    Collection<Task> tasks = repository.findAll();
    if (tasks.isEmpty()) {
      return "No tasks yet. Add one with: task add <title>\n";
    }

    LocalDate today = LocalDate.now();
    StringBuilder out = new StringBuilder(tasks.size() * 48);
    out.append(String.format("%-5s %-8s %s%n", "ID", "STATUS", "TITLE"));
    for (Task task : tasks) {
      out.append(String.format(
        "%-5d %-8s %s%n", task.getId(), task.status(today), task.getTitle())
      );
    }
    return out.toString();
  }

  private static void requireValidRange(LocalDate startTime, LocalDate dueTime) {
    if (dueTime.isBefore(startTime)) {
      throw new IllegalArgumentException("The due date cannot be before the start date.");
    }
  }

  EditResult editTask(int taskId, String newTitle, LocalDate newDueTime) {
    Task task = repository.find(taskId);
    if (task == null) {
      return EditResult.NOT_FOUND;
    }
    if (newTitle == null && newDueTime == null) {
      return EditResult.NO_CHANGES;
    }

    if (newDueTime != null) {
      requireValidRange(task.getStartTime(), newDueTime);
    }

    boolean titleChanged = newTitle != null && !newTitle.equals(task.getTitle());
    boolean dueChanged = newDueTime != null && !newDueTime.equals(task.getDueTime());

    if (titleChanged) {
      task.setTitle(newTitle);
    }
    if (dueChanged) {
      task.setDueTime(newDueTime);
    }
    if (titleChanged || dueChanged) {
      repository.update(task);
    }
    return EditResult.UPDATED;
  }
}
