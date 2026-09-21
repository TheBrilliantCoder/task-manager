package com.badlogic.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Stores tasks as JSON Lines (one task per line) — the same on-disk format as
 * before, so existing data files keep working.
 *
 * The file is parsed once at start-up and held in a map afterwards. Lookups by
 * ID are O(1), listing touches no I/O at all, adding a task costs one append,
 * and deleting or updating costs one rewrite (previously each of those
 * re-read and re-wrote the file several times over).
 */
class TaskRepository {
  private static final Path DEFAULT_FILE = Paths.get("data", "tasks.json");

  private final Path filePath;
  private final Gson gson;
  // id -> task, in insertion order. The source of truth while running.
  private final Map<Integer, Task> tasks = new LinkedHashMap<>();
  private int maxId;

  TaskRepository() {
    this(DEFAULT_FILE);
  }

  // Alternate location, handy for tests.
  TaskRepository(Path filePath) {
    this.filePath = filePath;
    this.gson = new GsonBuilder()
      .registerTypeAdapter(
        LocalDate.class,
        (JsonSerializer<LocalDate>)
          (src, typeOfSrc, context) -> context.serialize(src.toString()))
      .registerTypeAdapter(
        LocalDate.class,
        (JsonDeserializer<LocalDate>)
          (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
      .create();
    load();
  }


  Task find(int taskId) {
    return tasks.get(taskId);
  }

  Collection<Task> findAll() {
    return Collections.unmodifiableCollection(tasks.values());
  }

  int nextId() {
    return maxId + 1;
  }


  // Adds a new task. Costs a single append to the file.
  void add(Task task) {
    tasks.put(task.getId(), task);
    maxId = Math.max(maxId, task.getId());
    append(task);
  }

  // Persists an in-place change (e.g. completion) to an existing task.
  void update(Task task) {
    if (tasks.containsKey(task.getId())) {
      tasks.put(task.getId(), task);
      rewrite();
    }
  }

  // return true when a task with that ID existed and was removed.
  boolean remove(int taskId) {
    if (tasks.remove(taskId) == null) {
      return false;
    }
    rewrite();
    return true;
  }

  private void load() {
    if (!Files.exists(filePath)) {
      return;
    }

    int skipped = 0;
    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }
        Task task = parse(line);
        if (task == null) {
          skipped++;
          continue;
        }
        tasks.put(task.getId(), task);
        maxId = Math.max(maxId, task.getId());
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Could not read " + filePath, e);
    }

    if (skipped > 0) {
      System.err.println("Warning: skipped " + skipped + " unreadable record(s) in " + filePath);
    }
  }

  // return the parsed task, or null if the line is corrupt or incomplete.
  private Task parse(String line) {
    try {
      Task task = gson.fromJson(line, Task.class);
      return (task != null && task.isValid()) ? task : null;
    } catch (RuntimeException e) {
      return null;
    }
  }

  private void append(Task task) {
    try {
      ensureParentDirectory();
      Files.writeString(
        filePath,
        gson.toJson(task) + "\n",
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      );
    } catch (IOException e) {
      throw new UncheckedIOException("Could not write to " + filePath, e);
    }
  }

  /*
   * Writes every task in one pass to a temporary file, then moves it into
   * place, so an interrupted write cannot leave a half-written task list.
   */
  private void rewrite() {
    Path temp = filePath.resolveSibling(filePath.getFileName() + ".tmp");
    try {
      ensureParentDirectory();
      try (BufferedWriter writer = Files.newBufferedWriter(
        temp,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE)
      ) {
        for (Task task : tasks.values()) {
          writer.write(gson.toJson(task));
          writer.write('\n');
        }
      }
      moveIntoPlace(temp);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not write to " + filePath, e);
    }
  }

  private void moveIntoPlace(Path temp) throws IOException {
    try {
      Files.move(
        temp, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
      );
    } catch (AtomicMoveNotSupportedException e) {
      Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private void ensureParentDirectory() throws IOException {
    Path parent = filePath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
  }
}
