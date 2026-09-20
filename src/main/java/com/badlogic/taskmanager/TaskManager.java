package com.badlogic.taskmanager;

import java.util.ArrayList;
import java.time.LocalDate;

class TaskManager {
  private TaskRepository repository;
  private int currentID;

  TaskManager() {
    repository = new TaskRepository();
    currentID = repository.getLatestTaskID();
  }

  void createTask(String title, LocalDate startTime, LocalDate dueTime) {
    Task task = new Task(currentID+1, title, startTime, dueTime);
    repository.saveTask(task);
    currentID++;
  }

  void deleteTask(int taskid) {
    repository.removeTask(taskid);
  }

  void completeTask(int taskid) {
    repository.markCompleted(taskid);
  }

  String calculateStatus(LocalDate startTime, LocalDate dueTime) {
    LocalDate date = LocalDate.now();

    if (date.compareTo(startTime) < 0) {
      return "TODO";
    } else if (date.compareTo(dueTime) > 0) {
      return "OVERDUE";
    }
    return "DOING";
  }

  void listAllTasks() {
    ArrayList<Task> tasks = repository.loadAllTasks();
    System.out.println("ID\tSTATUS\tTitle");
    for (Task task : tasks) {
      String status;

      if (task.isCompleted()) {
        status = "DONE";
      } else {
        status = calculateStatus(task.getStartTime(), task.getDueTime());
      }
      System.out.printf("%d\t%s\t\t%s%n", task.getID(), status, task.getTitle());
    }
  }

}
