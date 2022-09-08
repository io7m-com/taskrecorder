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

import com.io7m.taskrecorder.core.TRFailed;
import com.io7m.taskrecorder.core.TRSucceeded;
import com.io7m.taskrecorder.core.TRTask;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TRTaskRecorderTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(TRTaskRecorderTest.class);

  @Test
  public void testTaskFailure()
  {
    final var task = TRTask.create(LOG, "Started task...");
    task.setFailed("Failed!");

    assertEquals(
      new TRFailed("Failed!", Optional.empty()),
      task.resolution()
    );
    assertEquals(1, task.steps().size());
    assertEquals("Started task...", task.name());
    assertEquals("Started task...", task.steps().get(0).name());
  }

  @Test
  public void testTaskFailureWithException()
  {
    final var task = TRTask.create(LOG, "Started task...");
    final var ex = new IOException("x");
    task.setFailed("Failed!", ex);

    assertEquals(
      new TRFailed("Failed!", Optional.of(ex)),
      task.resolution()
    );
    assertEquals(1, task.steps().size());
    assertEquals("Started task...", task.name());
    assertEquals("Started task...", task.steps().get(0).name());
  }

  @Test
  public void testTaskSuccess()
  {
    final var task =
      TRTask.create(LOG, "Started task...");

    task.beginStep("Step 0");
    task.setSucceeded("Success!");
    task.beginStep("Step 1");
    task.setSucceeded();

    assertEquals(
      new TRSucceeded(""),
      task.resolution()
    );
    assertEquals(3, task.steps().size());
    assertEquals("Started task...", task.name());
    assertEquals("Started task...", task.steps().get(0).name());
    assertEquals("Step 0", task.steps().get(1).name());
    assertEquals("Step 1", task.steps().get(2).name());
  }

  @Test
  public void testTaskSubtasks()
  {
    final var task =
      TRTask.<Integer>create(LOG, "Started task...");

    final var t0 = task.beginSubtask("t0");
    t0.setSucceeded();
    final var t1 = task.beginSubtask("t1");
    t1.setSucceeded();
    final var t2 = task.beginSubtask("t2");
    t2.setSucceeded();

    task.setResult(23);

    assertEquals(
      new TRSucceeded(""),
      task.resolution()
    );
    assertEquals(4, task.steps().size());
    assertEquals("Started task...", task.name());
    assertEquals("Started task...", task.steps().get(0).name());
    assertEquals("t0", task.steps().get(1).name());
    assertEquals("t1", task.steps().get(2).name());
    assertEquals("t2", task.steps().get(3).name());

    assertEquals(23, task.result().orElseThrow());
  }
}
