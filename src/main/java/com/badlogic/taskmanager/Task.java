package com.badlogic.taskmanager;

import java.time.LocalDate;

class Task {
  private int id;
  private String title;
  private LocalDate startTime;
  private LocalDate dueTime;
  private boolean completed;

  Task(int id, String title, LocalDate startTime, LocalDate dueTime) {
    this.id = id;
    this.title = title;
    this.startTime = startTime;
    this.dueTime = dueTime;
    this.completed = false;
  }

  void setCompleted(boolean isTrue) {
    completed = isTrue;
  }

  boolean isCompleted() {
    return completed;
  }

  int getID() {
    return id;
  }

  String getTitle() {
    return title;
  }

  LocalDate getStartTime() {
    return startTime;
  }

  LocalDate getDueTime() {
    return dueTime;
  }
}
