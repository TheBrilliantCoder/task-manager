---
id: 03-exercise
aliases: []
tags: []
---
# EXERCISE — SMALL VALIDATION PRACTICE

## Project

You have already completed the Specification, Design, and Implementation phases for the Task Manager.

For this exercise, focus on only **two functional requirements**:

```text
FR-01:
The system shall allow the user to create a task with a title, start time, and due time.

FR-07:
The system shall calculate the correct task status based on completion state, start time, and due time.
```

---

# Part 1 — Validation Checklist

Create a validation checklist for **FR-01 and FR-07 only**.

For each requirement:

```text
Requirement
What must be checked?
How will it be checked?
```

You only need **2 checklist items**.

FR-01
* What must be checked? A task created when a valid title, start and due time is given.
* How will it be checked? A unit test that construct Task object and see if the Task can be created.

FR-07
* What must be checked? A task status must be be DONE if completed, OVERDUE if over due time, TODO when before start time and DOING when between start and due time.
* How will it be checked? A unit test that create 3 Tasks object correspond to each state, mark 1 task to be DONE and check if the status is shown correctly.

---

# Part 2 — Test Cases

Create test cases covering the two requirements.

Include:

* Normal case
* Invalid case
* Boundary cases

For each test case:

```text
Test ID
Requirement
Input
Steps
Expected result
```

Determine the **expected result before running the program**.

**FR-01**

TC-01
    Input: non-blank title, valid start and due time
    Step: call TaskManager -> create a task with title, start and due time -> assert if title, start and due time is recorded in the created Task.
    Expected output: Input values match Task attribute values.

TC-02A
    Input: blank title
    Step: call TaskManager -> create a task with blank title -> check if software assert exception for invalid input.
    Expected output: Throw an exception of illegal Argument.

TC-02B
    Input: valid title, start time = 2026-09-24, due time = 2026-09-23
    Step: Construct Task object -> assert invalid time duration
    Expected output: Throw exception.


**FR-07**
TC-03A
    Input: valid title, start time = 2026-09-23, due time = 2026-09-24
    Step: Construct Task object -> check status of that object
    Expected output: Task status is DOING

TC-03B
    Input: valid title, start time = 2026-09-20, due time = 2026-09-21
    Step: Construct Task object -> check status of that object
    Expected output: Task status is OVERDUE

TC-03C
    Input: valid title, start time = 2026-09-25, due time = 2026-09-26
    Step: Construct Task object -> check status of that object
    Expected output: Task status is TODO

TC-03D
    Input: valid title, start time = 2026-09-20, due time = 2026-09-21
    Step: Construct Task object -> mark the task as completed -> check status of Task object
    Expected output: Task status is DONE

TC-04A
    Input: valid title, start time = 2026-09-23, due time = 2026-09-24
    Step: Construct Task object -> check status of Task object with current date of 2026-09-24
    Expected output: Task status is DOING

TC-04B
    Input: valid title, start time = 2026-09-23, due time = 2026-09-24
    Step: Construct Task object -> check status of Task object with current date of 2026-09-23
    Expected output: Task status is DOING

---

# Part 3 — Execute Tests

Run the tests against your actual Task Manager.

Record:

```text
Test ID
Expected result
Actual result
PASS / FAIL
```

Do not change the expected result to match the implementation.

TC-01
    * Expected output: Task("Junit test", 2026-09-23, 2026-09-24)
    * Actual output: Task("Junit test", 2026-09-23, 2026-09-24)
    PASS

TC-02A
    * Expected output: IllegalArgumentException("A task needs a title.")
    * Actual output: IllegalArgumentException("A task needs a title.")
    PASS

TC-02B
    * Expected output: IllegalArgumentException("The due date cannot be before the start date.")
    * Actual output: IllegalArgumentException("The due date cannot be before the start date.")
    PASS

TC-03A
    * Expected output: DOING
    * Actual output: DOING
    PASS

TC-03B
    * Expected output: OVERDUE
    * Actual output: OVERDUE
    PASS

TC-03C
    * Expected output: TODO
    * Actual output: TODO
    PASS

TC-03D
    * Expected output: DONE
    * Actual output: DONE
    PASS

TC-04A
    * Expected output: DOING
    * Actual output: DOING
    PASS

TC-04B
    * Expected output: DOING
    * Actual output: DOING
    PASS

---

# Part 4 — Integration Test

Perform **one integration test**.

Test:

```text
CLI
 ↓
Task Manager
 ↓
Task Repository
 ↓
tasks.json
```

Example:

Create a task through the CLI and verify that the correct task data is saved to `tasks.json`.

Record:

```text
Expected
Actual
PASS / FAIL
```

IT-01: Test create a Task from CLI
    Input: command line `add task Integration test`
    Step: call CLI logic -> handle command line input of adding a task -> check if `IT-01.json` contain the record of task created.
    Expected output: {"id":1,"title":"Integration test","startTime":"2026-09-23","dueTime":"2026-09-24","completed":false}
    Actual output: {"id":1,"title":"Integration test","startTime":"2026-09-23","dueTime":"2026-09-24","completed":false}
    PASS

---

# Part 5 — System Test

Perform **one complete system test** from the user's perspective.

Scenario:

```text
1. Create a task.
2. List the task.
3. Complete the task.
4. List the task again.
```

Record:

```text
Expected
Actual
PASS / FAIL
```


ST-01
    Expected:
        Create a task -> "Created task <id>: <title>"
        List the task -> "<id>  DOING   <title>"
        Complete the task -> "Marked task <id> as done"
        List task again -> "<id>  DONE   <title>"
    Actual:
        Create a task -> "Created task <id>: <title>"
        List the task -> "<id>  DOING   <title>"
        Complete the task -> "Marked task <id> as done"
        List task again -> "<id>  DONE   <title>"
    PASS

---

# Part 6 — Non-Functional Requirement

Test **one case** of NFR-01:

```text
The CLI should provide a clear error message
for invalid input.
```

Use one invalid input, for example:

```text
Create a task with an empty title.
```

Record:

```text
Input
Expected error
Actual error
PASS / FAIL
```

TC-05
    Input: `task add`
    Expected error: "Error: Missing task title. (e.g., task add My Title)"
    Actual error: "Error: Missing task title. (e.g., task add My Title)"
    PASS

---

# Part 7 — Defect

If any test fails, create **one defect report**.

```text
Defect ID:

Related test:

Description:

Steps to reproduce:

Expected result:

Actual result:
```

Focus on **what went wrong**, not the technical cause.

> There is no defect, all test case pass successfully.
> In the standard solution, you should give an example to illustrate defect report

---

# Part 8 — Fix and Regression Test

Fix one defect if you found one.

Then:

1. Rerun the original failed test.
2. Rerun one related test.

Record:

| Test          | Before fix | After fix |
| ------------- | ---------- | --------- |
| Original test | FAIL       | PASS      |
| Related test  | PASS/FAIL  | PASS/FAIL |

The goal is to check:

```text
Was the defect fixed?
+
Did the fix break related functionality?
```

> All test cases passed.
> There is only problem with test code writing which is not related to functionality of the software.
> Whenever i create a new test, i always rerun all the test cases and they all passed.

---

# Part 9 — Acceptance Test

Perform **one small acceptance test**.

User goal:

> "I want to create a task and see its correct status."

Test:

```text
1. Create a task.
2. View the task.
3. Check its status.
```

Record:

```text
User action
Expected outcome
Actual outcome
PASS / FAIL
```

AT-01
    User action: user run the software manually -> 'task add Acceptance test' -> 'task list'
    Expected outcome: user see the task he created and know the status of it through task list.
    Actual outcome: the output on Terminal show the information needed.
    PASS

---

# Validation Flow

Your exercise should now follow this flow:

```text
Requirements
    ↓
Validation checklist
    ↓
Test cases
    ↓
Execute tests
    ↓
Integration test
    ↓
System test
    ↓
Non-functional test
    ↓
Defect
    ↓
Fix
    ↓
Regression test
    ↓
Acceptance test
```

**No validation report is required for this exercise.**
