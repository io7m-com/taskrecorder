/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.taskrecorder.tests;

import com.io7m.taskrecorder.core.TRStep;
import com.io7m.taskrecorder.core.TRStepFailed;
import com.io7m.taskrecorder.core.TRStepSucceeded;
import com.io7m.taskrecorder.core.TRTask;
import com.io7m.taskrecorder.core.TRTaskFailed;
import com.io7m.taskrecorder.core.TRTaskRecorder;
import com.io7m.taskrecorder.core.TRTaskSucceeded;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Task recorder tests.
 */

public final class TRTaskRecorderTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(TRTaskRecorderTest.class);

  /**
   * Tasks cannot be empty.
   */

  @Test
  public void testTaskNotEmpty()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new TRTask<>(
        List.of(),
        new TRTaskFailed<>("WHAT?", Optional.empty())
      );
    });
  }

  /**
   * Tasks must be resolved before closing.
   */

  @Test
  public void testTaskNotResolved()
  {
    assertThrows(IllegalStateException.class, () -> {
      TRTaskRecorder.create(LOG, "Started task...")
        .close();
    });
  }

  /**
   * Tasks must be resolved before closing.
   */

  @Test
  public void testTaskResolvedSuccess()
  {
    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {
      assertEquals(
        "[TRTaskRecorder (Started task...)]",
        taskRec.toString()
      );

      taskRec.setTaskResolution(
        new TRTaskSucceeded<>("OK!", Integer.valueOf(23))
      );
      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskSucceeded<Integer> r) {
      assertEquals(23, r.result());
    } else {
      fail();
    }
  }

  /**
   * Tasks must be resolved before closing.
   */

  @Test
  public void testTaskResolvedFailed()
  {
    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {
      taskRec.setTaskResolution(new TRTaskFailed<>(
        "FAILED!",
        Optional.empty()));
      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskFailed<Integer> r) {
      assertEquals("FAILED!", r.message());
    } else {
      fail();
    }
  }

  /**
   * Tasks can be recorded.
   */

  @Test
  public void testTaskRecorded0()
  {
    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {
      taskRec.beginStep("Step 0");
      taskRec.setStepSucceeded("OK 0");
      taskRec.beginStep("Step 1");
      taskRec.setStepSucceeded();
      taskRec.beginStep("Step 2");
      taskRec.setStepSucceeded("OK 2");
      taskRec.setTaskResolution(new TRTaskSucceeded<>("OK!",
                                                      Integer.valueOf(23)));
      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskSucceeded<Integer> r) {
      final var items = task.items();
      assertEquals("Started task...", items.get(0).description());
      final var step0 = (TRStep) items.get(1);
      assertEquals("Step 0", step0.description());
      assertEquals("OK 0", step0.resolution().message());
      assertEquals(TRStepSucceeded.class, step0.resolution().getClass());
      final var step1 = (TRStep) items.get(2);
      assertEquals("Step 1", step1.description());
      assertEquals("", step1.resolution().message());
      assertEquals(TRStepSucceeded.class, step1.resolution().getClass());
      final var step2 = (TRStep) items.get(3);
      assertEquals("Step 2", step2.description());
      assertEquals("OK 2", step2.resolution().message());
      assertEquals(TRStepSucceeded.class, step2.resolution().getClass());
      assertEquals(23, r.result());
    } else {
      fail();
    }
  }

  /**
   * Tasks can be recorded.
   */

  @Test
  public void testTaskRecorded1()
  {
    final var ex0 = new Exception();
    final var ex1 = new Exception();

    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {
      taskRec.beginStep("Step 0");
      taskRec.setStepFailed("Failed 0");
      taskRec.beginStep("Step 1");
      taskRec.setStepFailed("Failed 1", ex0);
      taskRec.beginStep("Step 2");
      taskRec.setStepFailed("Failed 2", Optional.of(ex1));
      taskRec.setTaskResolution(new TRTaskSucceeded<>("OK!",
                                                      Integer.valueOf(23)));
      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskSucceeded<Integer> r) {
      final var items = task.items();
      assertEquals("Started task...", items.get(0).description());
      final var step0 = (TRStep) items.get(1);
      assertEquals("Step 0", step0.description());
      assertEquals("Failed 0", step0.resolution().message());
      assertEquals(TRStepFailed.class, step0.resolution().getClass());
      final var step1 = (TRStep) items.get(2);
      assertEquals("Step 1", step1.description());
      assertEquals("Failed 1", step1.resolution().message());
      assertEquals(ex0, ((TRStepFailed) step1.resolution()).exception().get());
      final var step2 = (TRStep) items.get(3);
      assertEquals("Step 2", step2.description());
      assertEquals("Failed 2", step2.resolution().message());
      assertEquals(ex1, ((TRStepFailed) step2.resolution()).exception().get());
      assertEquals(23, r.result());
    } else {
      fail();
    }
  }

  /**
   * Tasks can be recorded.
   */

  @Test
  public void testTaskRecorded2()
  {
    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {

      final TRTask<Integer> task0;
      try (var st = taskRec.<Integer>beginSubtask("Subtask X")) {
        st.setTaskSucceeded("X", Integer.valueOf(23));
        task0 = st.toTask();
      }

      final TRTask<Integer> task1;
      try (var st = taskRec.<Integer>beginSubtask("Subtask Y")) {
        st.setTaskSucceeded("Y", Integer.valueOf(17));
        task1 = st.toTask();
      }

      final var task0r =
        ((TRTaskSucceeded<Integer>) task0.resolution()).result().intValue();
      final var task1r =
        ((TRTaskSucceeded<Integer>) task1.resolution()).result().intValue();
      taskRec.setTaskSucceeded(
        "Z",
        Integer.valueOf(task0r + task1r)
      );

      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskSucceeded<Integer> r) {
      final var items = task.items();
      assertEquals("Started task...", items.get(0).description());

      final var t0 = (TRTask<Integer>) items.get(1);
      assertEquals("Subtask X", t0.description());
      final var t0s = (TRTaskSucceeded<Integer>) t0.resolution();
      assertEquals(23, t0s.result());

      final var t1 = (TRTask<Integer>) items.get(2);
      assertEquals("Subtask Y", t1.description());
      final var t1s = (TRTaskSucceeded<Integer>) t1.resolution();
      assertEquals(17, t1s.result());

      assertEquals(40, r.result());
    } else {
      fail();
    }
  }

  /**
   * Tasks can be recorded.
   */

  @Test
  public void testTaskRecorded3()
  {
    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {
      taskRec.setTaskFailed("FAILED!");
      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskFailed<Integer> r) {
      final var items = task.items();
      assertEquals("Started task...", items.get(0).description());
      assertEquals("FAILED!", r.message());
      assertEquals(Optional.empty(), r.exception());
    } else {
      fail();
    }
  }

  /**
   * Tasks can be recorded.
   */

  @Test
  public void testTaskRecorded4()
  {
    final TRTask<Integer> task;
    try (var taskRec =
           TRTaskRecorder.<Integer>create(LOG, "Started task...")) {
      taskRec.setTaskFailed("FAILED!", Optional.empty());
      task = taskRec.toTask();
    }

    final var resolution = task.resolution();
    if (resolution instanceof TRTaskFailed<Integer> r) {
      final var items = task.items();
      assertEquals("Started task...", items.get(0).description());
      assertEquals("FAILED!", r.message());
      assertEquals(Optional.empty(), r.exception());
    } else {
      fail();
    }
  }
}
