---
id: 03-exercise
aliases: []
tags: []
---
# EXERCISE — VALIDATION PHASE

## Project

Task Manager

Functional requirements under validation:

```text
FR-01:
The system shall allow the user to create a task with a title, start time, and due time.

FR-07:
The system shall calculate the correct task status based on completion state, start time, and due time.
```

---

# Part 1 — Validation Checklist

## FR-01

**Requirement**  
The system shall allow the user to create a task with a title, start time, and due time.

**What must be checked?**  
A task can be created when a valid title, start time, and due time are provided, and the resulting task contains exactly those values.

**How will it be checked?**  
Use a unit test against the Task Manager creation operation.

### Review of your answer

Your answer was essentially correct:

> "A task created when a valid title, start and due time is given."

The important improvement is to include **what must be true about the created task**, not merely that creation does not fail.

A better validation question is:

> "After creation, does the system produce a task containing the requested data?"

This makes the expected behavior observable.

---

## FR-07

**Requirement**  
The system shall calculate the correct task status based on completion state, start time, and due time.

**What must be checked?**  
The system must return:

```text
TODO     → current date is before start date
DOING    → current date is on/after start and on/before due date
OVERDUE  → current date is after due date
DONE     → task has been completed
```

**How will it be checked?**  
Use unit tests with a **controlled reference date** covering normal cases and boundary cases.

### Important lesson from your answer

You correctly identified the four states.  
However, your test design did not fully define **what date the status calculation was being evaluated against**.

For example:

```text
TC-03A
start = 2026-09-23
due   = 2026-09-24
expected = DOING
```

This is only meaningful if we also say:

```text
current date = 2026-09-24
```

Without that condition, the test case is incomplete.

**Lesson:**

> For time-dependent behavior, the test input must include the reference time/date.

---

# Part 2 — Test Cases

A good test set should cover:

```text
Normal behavior
Invalid behavior
Boundary behavior
```

## FR-01 Test Cases

### TC-01 — Create task with valid input

**Requirement:** FR-01

**Input:**

```text
Title = "Junit test"
Start = 2026-09-23
Due   = 2026-09-24
```

**Steps:**

1. Call Task Manager's create operation.
2. Provide the title, start date, and due date.
3. Inspect the created task.

**Expected result:**

```text
A task is created successfully and contains:

Title     = "Junit test"
Start     = 2026-09-23
Due       = 2026-09-24
```

---

### TC-02A — Blank title

**Requirement:** FR-01

**Input:**

```text
Title = ""
Start = 2026-09-23
Due   = 2026-09-24
```

**Steps:**

1. Attempt to create the task.
2. Observe the result.

**Expected result:**

```text
IllegalArgumentException is thrown.
```

### Review of your answer

You wrote:

> "check if software assert exception"

The concept is correct, but remember:

> The software **throws** an exception; the test **asserts** that the exception is thrown.

So the validation behavior is:

```text
Software → throws IllegalArgumentException
Test     → assertThrows(IllegalArgumentException.class, ...)
```

---

### TC-02B — Due date before start date

**Requirement:** FR-01

**Input:**

```text
Title = "Junit test"
Start = 2026-09-24
Due   = 2026-09-23
```

**Steps:**

1. Attempt to create the task.
2. Observe the result.

**Expected result:**

```text
IllegalArgumentException is thrown.
```

---

## FR-07 Test Cases

For these tests, explicitly define the reference date.

### TC-03A — TODO

**Input:**

```text
Start = 2026-09-25
Due   = 2026-09-26
Current date = 2026-09-24
Completed = false
```

**Steps:**

1. Create a task with Task Manager.
2. Set the current date before start time.
3. Check for the Task status.

**Expected result:**

```text
TODO
```

---

### TC-03B — DOING

**Input:**

```text
Start = 2026-09-23
Due   = 2026-09-24
Current date = 2026-09-24
Completed = false
```

**Steps:**

1. Create a task with Task Manager.
2. Set the current date between start time and due time.
3. Check for the Task status.

**Expected result:**

```text
DOING
```

---

### TC-03C — OVERDUE

**Input:**

```text
Start = 2026-09-19
Due   = 2026-09-20
Current date = 2026-09-24
Completed = false
```

**Steps:**

1. Create a task with Task Manager.
2. Set the current date after due time.
3. Check for the Task status.

**Expected result:**

```text
OVERDUE
```

---

### TC-03D — DONE overrides time status

**Input:**

```text
Start = 2026-09-19
Due   = 2026-09-20
Current date = 2026-09-24
Completed = true
```

**Steps:**

1. Create a task with Task Manager.
2. Set the current date after due time.
3. Mark the Task as completed with Task Manager.
4. Check for the Task status.

**Expected result:**

```text
DONE
```

This verifies an important business rule:

```text
completed = true
        ↓
      DONE
```

even though the task is also past its due date.

---

## Boundary Cases

### TC-04A — Current date equals start date

**Input:**

```text
Start = 2026-09-23
Due   = 2026-09-24
Current date = 2026-09-23
Completed = false
```

**Steps:**

1. Create a task with Task Manager.
2. Set the current date the same as start time.
3. Check for the Task status.

**Expected result:**

```text
DOING
```

---

### TC-04B — Current date equals due date

**Input:**

```text
Start = 2026-09-23
Due   = 2026-09-24
Current date = 2026-09-24
Completed = false
```

**Steps:**

1. Create a task with Task Manager.
2. Set the current date the same as due time.
3. Check for the Task status.

**Expected result:**

```text
DOING
```

> A boundary test does not merely test code. It also exposes ambiguity in the requirement.

---

# Part 3 — Execute Tests

The rule is:

> Expected result is defined before execution. Actual result is recorded after execution.

Based on your execution:

| Test   | Expected                           | Actual                             | Result |
| ------ | ---------------------------------- | ---------------------------------- | ------ |
| TC-01  | Task created with requested values | Task created with requested values | PASS   |
| TC-02A | IllegalArgumentException           | IllegalArgumentException           | PASS   |
| TC-02B | IllegalArgumentException           | IllegalArgumentException           | PASS   |
| TC-03A | TODO                               | TODO                               | PASS   |
| TC-03B | DOING                              | DOING                              | PASS   |
| TC-03C | OVERDUE                            | OVERDUE                            | PASS   |
| TC-03D | DONE                               | DONE                               | PASS   |
| TC-04A | DOING                              | DOING                              | PASS   |
| TC-04B | DOING                              | DOING                              | PASS   |

Therefore, based on your recorded execution:

```text
All functional test cases passed.
```

### One important improvement

You used:

```java
LocalDate date = LocalDate.now();
```

inside the status test.

That works today because the current date is September 24, 2026, but it makes the test **time-dependent**.

Tomorrow, the same test could produce a different result.

For example:

```java
assertEquals("DOING", task2.status(LocalDate.now()));
```

is not a stable test.

A better test is:

```java
LocalDate testDate = LocalDate.of(2026, 9, 24);

assertEquals("DOING", task2.status(testDate));
```

Even better, the test cases should define the reference date deliberately for every case.

### Lesson

> Automated tests should not depend on the real clock when testing a specific historical condition.

This is one of the most important test-writing lessons from your exercise.

---

# Part 4 — Integration Test

## IT-01 — Create task through CLI and persist it

**Purpose**

Verify that the components work together:

```text
CLI
 ↓
Task Manager
 ↓
Task Repository
 ↓
File
```

**Input:**

```text
task add Integration test
2026-09-23
2026-09-24
```

**Expected:**

A task is created and persisted with:

```json
{
  "id": 1,
  "title": "Integration test",
  "startTime": "2026-09-23",
  "dueTime": "2026-09-24",
  "completed": false
}
```

**Actual:**

The created task was found in the repository with the expected values.

**Result:**

```text
PASS
```

### Lesson

> Always know exactly what layer your test is proving.

---

# Part 5 — System Test

## ST-01 — Complete user workflow

The scenario is:

```text
1. Create a task.
2. List the task.
3. Complete the task.
4. List the task again.
```

## Expected

```text
Create:
Created task <id>: System test

List:
<id> DOING System test

Complete:
Marked task <id> as done

List again:
<id> DONE System test
```

## Actual

Your recorded execution produced the same sequence.

```text
PASS
```

---

# Part 6 — Non-Functional Requirement

## NFR-01 — Clear error message

**Requirement:**

The CLI should provide a clear error message for invalid input.

**Input:**

```text
task add
```

**Expected error:**

```text
Error: Missing task title. (e.g., task add My Title)
```

**Actual error:**

```text
Error: Missing task title. (e.g., task add My Title)
```

**Result:**

```text
PASS
```

Your test satisfies the exercise.

### Why this is an NFR test

The functional rule is:

```text
Invalid command → reject invalid operation
```

The non-functional requirement is concerned with **how the rejection is communicated**:

```text
Is the error message understandable and useful?
```

That distinction is worth remembering.

---

# Part 7 — Defect

Because all of your real tests passed, there was no actual defect to report.  
However, the exercise asks you to understand what a defect report would look like, so here is an **illustrative example only**.

## Example Defect Report

**Defect ID:**

```text
DEF-001
```

**Related test:**

```text
TC-04B
```

**Description:**

```text
A task is reported as OVERDUE when the current date is exactly equal to its due date.
```

**Steps to reproduce:**

```text
1. Create a task with start date 2026-09-23.
2. Set due date to 2026-09-24.
3. Evaluate the task on 2026-09-24.
4. View the task status.
```

**Expected result:**

```text
DOING
```

**Actual result:**

```text
OVERDUE
```

### Important lesson

Notice that the defect report does **not** say:

```text
The comparison operator in Task.status() is wrong.
```

That would be a technical cause.

The defect report focuses on:

```text
What the user/test observed
vs.
What was expected
```

This matches the instruction:

> Focus on what went wrong, not the technical cause.

---

# Part 8 — Fix and Regression Test

Since your real execution found no defect, there is no real fix or regression run to record.  
For learning purposes, using the illustrative defect above:

| Test   | Before fix | After fix |
| ------ | ---------- | --------- |
| TC-04B | FAIL       | PASS      |
| TC-03C | PASS       | PASS      |

Interpretation:

```text
TC-04B:
The original defect was fixed.

TC-03C:
The fix did not break the normal OVERDUE behavior.
```

### What regression testing means

Regression testing is not:

> "Run the whole application again."

The important idea is:

```text
Fix defect
   ↓
Rerun failed test
   ↓
Rerun related behavior
   ↓
Check that old functionality still works
```

Your statement:

> "Whenever I create a new test, I always rerun all the test cases and they all passed."

is actually a good development habit.

But there is an important distinction:

```text
Running all tests after a code change
        =
Regression testing practice

Choosing related tests specifically to verify a fix
        =
Targeted regression testing
```

Both are useful.

---

# Part 9 — Acceptance Test

## AT-01 — Create a task and see its status

**User goal:**

> I want to create a task and see its correct status.

**User action:**

```text
1. Run the application.
2. Enter:

task add Acceptance test
<start date>
<due date>

3. Enter:

task list
```

**Expected outcome:**

```text
The created task is displayed,
and its status is shown correctly.
```

**Actual outcome:**

```text
The terminal displays the created task and its status.
```

**Result:**

```text
PASS
```

