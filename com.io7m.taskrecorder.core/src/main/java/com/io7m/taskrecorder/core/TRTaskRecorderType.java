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

package com.io7m.taskrecorder.core;

import java.util.Optional;

/**
 * The type of task recorders.
 *
 * @param <T> The type of values returned by successful tasks
 */

public non-sealed interface TRTaskRecorderType<T>
  extends AutoCloseable, TRRecorderType, TRTaskStepResolutionRecorderType
{
  /**
   * Begin recording a new subtask.
   *
   * @param description The description
   * @param <U>         The type of returned values
   *
   * @return The new subtask recorder
   */

  <U> TRTaskRecorderType<U> beginSubtask(
    String description);

  /**
   * Begin a new step. This sets the current step of the current task recorder
   * to the new step.
   *
   * @param description The description
   *
   * @return A new step recorder
   */

  TRTaskStepRecorderType beginStep(
    String description);

  /**
   * @return The current step
   */

  TRTaskStepRecorderType stepCurrent();

  /**
   * Set the resolution of the current task.
   *
   * @param resolution The resolution
   */

  void setTaskResolution(
    TRTaskResolutionType<T> resolution);

  /**
   * Set the task as having succeeded.
   *
   * @param message The message
   * @param value   The value
   */

  default void setTaskSucceeded(
    final String message,
    final T value)
  {
    this.setTaskResolution(new TRTaskSucceeded<>(message, value));
  }

  /**
   * Set the task as having failed.
   *
   * @param message   The message
   * @param exception The exception
   */

  default void setTaskFailed(
    final String message,
    final Optional<Throwable> exception)
  {
    this.setTaskResolution(new TRTaskFailed<>(message, exception));
  }

  /**
   * Set the task as having failed.
   *
   * @param message The message
   */

  default void setTaskFailed(
    final String message)
  {
    this.setTaskResolution(new TRTaskFailed<>(message, Optional.empty()));
  }

  /**
   * Set the resolution of the current step.
   *
   * @param resolution The resolution
   */

  @Override
  default void setStepResolution(
    final TRStepResolutionType resolution)
  {
    this.stepCurrent().setStepResolution(resolution);
  }

  /**
   * @return The current recorded task (and all subtasks) as an immutable task
   */

  TRTask<T> toTask();

  @Override
  void close()
    throws IllegalStateException;
}
