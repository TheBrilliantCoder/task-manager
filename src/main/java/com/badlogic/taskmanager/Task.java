package com.badlogic.taskmanager;

import java.time.LocalDate;

/*
 * A single task. Everything except the completion flag is immutable, so a Task
 * can be handed around freely without defensive copying.
 */
class Task {
  private final int id;
  private final String title;
  private final LocalDate startTime;
  private final LocalDate dueTime;
  private boolean completed;

  Task(int id, String title, LocalDate startTime, LocalDate dueTime) {
    this.id = id;
    this.title = title;
    this.startTime = startTime;
    this.dueTime = dueTime;
    this.completed = false;
  }

  int getId() {
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

  boolean isCompleted() {
    return completed;
  }

  void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /*
   Status relative to the given day. The caller passes the date in so a whole
   listing is computed against one consistent "today" instead of asking the
   system clock once per task.
   */
  String status(LocalDate today) {
    if (completed) {
      return "DONE";
    }
    if (today.isBefore(startTime)) {
      return "TODO";
    }
    if (today.isAfter(dueTime)) {
      return "OVERDUE";
    }
    return "DOING";
  }

  // True when the record loaded from disk has all the fields we rely on.
  boolean isValid() {
    return title != null && startTime != null && dueTime != null;
  }
}
