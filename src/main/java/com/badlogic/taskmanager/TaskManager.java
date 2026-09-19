package com.badlogic.taskmanager;

import java.util.ArrayList;
import java.time.LocalDate;

class TaskManager {
  private ArrayList<Task> tasks;

  TaskManager() {
    tasks = new ArrayList<>();
  }

  void createTask(int id, String title, LocalDate startTime, LocalDate dueTime) {
    Task task = new Task(id, title, startTime, dueTime);
    tasks.add(task);
  }

  void deleteTask(int taskid) {
    for (int i = tasks.size() - 1; i >= 0; i--) {
      int curid = tasks.get(i).getID();
      if (curid == taskid) {
        tasks.remove(i);
        break;
      }
    }
  }

  void completeTask(int taskid) {
    for (Task task : tasks) {
      if (task.getID() == taskid) {
        task.setCompleted(true);
        break;
      }
    }
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

  void listTasks() {
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
