package com.badlogic.taskmanager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.time.LocalDate;
import java.util.ArrayList;

class TaskRepository {
  private Path filePath;
  private Gson gson;

  TaskRepository() {
    filePath = Paths.get("./data/tasks.json");
    gson = new GsonBuilder()
      // LocalDate -> JSON String
      .registerTypeAdapter(
        LocalDate.class,
        (JsonSerializer<LocalDate>)
          (src, typeOfSrc, context) -> context.serialize(src.toString())
      )
      // JSON String -> LocalDate
      .registerTypeAdapter(
        LocalDate.class,
        (JsonDeserializer<LocalDate>)
          (json, typeOfT, context) -> LocalDate.parse(json.getAsString())
      )
      .create();

    try {
      Files.createDirectories(filePath.getParent());

      if (Files.notExists(filePath)) {
        Files.writeString(filePath, "");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  int getLatestTaskID() {
    ArrayList<Task> tasks = loadAllTasks();
    int mx = 0;
    for (Task task : tasks) {
      mx = mx > task.getID() ? mx : task.getID();
    }
    return mx;
  }

  ArrayList<Task> loadAllTasks() {
    ArrayList<Task> tasks = new ArrayList<>();

    try {
      for (String line : Files.readAllLines(filePath)) {
        if (line.isBlank()) {
          continue;
        }

        Task task = gson.fromJson(line, Task.class);
        tasks.add(task);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tasks;
  }

  void saveTask(Task task) {
    String json = gson.toJson(task);

    try {
      Files.writeString(
          filePath,
          json + "\n",
          StandardOpenOption.APPEND
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void overwriteAllTasks(ArrayList<Task> tasks) {
    try {
      Files.writeString(filePath, "");
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (Task task : tasks) {
      saveTask(task);
    }
  }

  void markCompleted(int taskid) {
    Task task = null;

    try {
      for (String line : Files.readAllLines(filePath)) {
        if (line.isBlank()) {
          continue;
        }

        task = gson.fromJson(line, Task.class);
        if (task.getID() == taskid) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    task.setCompleted(true);
    removeTask(taskid);
    saveTask(task);
  }

  void removeTask(int taskid) {
    ArrayList<Task> tasks = loadAllTasks();
    for (int i = 0; i < tasks.size(); i++) {
      Task task = tasks.get(i);
      if (task.getID() == taskid) {
        tasks.remove(i);
        break;
      }
    }
    overwriteAllTasks(tasks);
  }

}
