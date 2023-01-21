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

import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.Objects;

/**
 * The default task recorder implementation.
 *
 * @param <T> The type of returned values
 */

public final class TRTaskRecorder<T> implements TRTaskRecorderType<T>
{
  private final LinkedList<TRRecorderType> recorders;
  private final Logger logger;
  private TRTaskResolutionType<T> resolution;
  private TRStepRecorder stepCurrent;

  private TRTaskRecorder(
    final Logger inLogger,
    final String inDescription)
  {
    this.logger =
      Objects.requireNonNull(inLogger, "inLogger");
    this.recorders =
      new LinkedList<>();
    this.recorders.add(new TRStepRecorder(inDescription));
    this.stepCurrent =
      (TRStepRecorder) this.recorders.getFirst();
  }

  /**
   * Create a new task recorder.
   *
   * @param logger      The logger used for debug messages
   * @param description The description of the first step
   * @param <T>         The type of returned values
   *
   * @return A new task recorder
   */

  public static <T> TRTaskRecorderType<T> create(
    final Logger logger,
    final String description)
  {
    return new TRTaskRecorder<>(logger, description);
  }

  private static TRTaskItemType toTaskItem(
    final TRRecorderType r)
  {
    if (r instanceof TRTaskStepRecorderType step) {
      return step.toStep();
    }

    if (r instanceof TRTaskRecorderType<?> task) {
      return task.toTask();
    }

    throw new IllegalStateException(
      "Unrecognized recorder type: %s".formatted(r)
    );
  }

  @Override
  public String toString()
  {
    return "[TRTaskRecorder (%s)]".formatted(this.stepCurrent.description);
  }

  @Override
  public <U> TRTaskRecorderType<U> beginSubtask(
    final String inDescription)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("beginSubtask: {}", inDescription);
    }

    final var task = new TRTaskRecorder<U>(this.logger, inDescription);
    this.recorders.add(task);
    return task;
  }

  @Override
  public TRTaskStepRecorderType beginStep(
    final String inDescription)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("beginStep: {}", inDescription);
    }

    final var step = new TRStepRecorder(inDescription);
    this.recorders.add(step);
    this.stepCurrent = step;
    return step;
  }

  @Override
  public TRTaskStepRecorderType stepCurrent()
  {
    return this.stepCurrent;
  }

  @Override
  public void setTaskResolution(
    final TRTaskResolutionType<T> inResolution)
  {
    this.resolution =
      Objects.requireNonNull(inResolution, "resolution");
  }

  @Override
  public TRTask<T> toTask()
  {
    this.checkResolution();

    return new TRTask<>(
      this.recorders.stream()
        .map(TRTaskRecorder::toTaskItem)
        .toList(),
      this.resolution
    );
  }

  @Override
  public void close()
    throws IllegalStateException
  {
    this.checkResolution();
  }

  private void checkResolution()
  {
    if (this.resolution == null) {
      throw new IllegalStateException(
        "No resolution was set for task '%s'"
          .formatted(this.stepCurrent.description)
      );
    }
  }

  private static final class TRStepRecorder
    implements TRTaskStepRecorderType
  {
    private final String description;
    private TRStepResolutionType resolution;

    TRStepRecorder(
      final String inDescription)
    {
      this.description =
        Objects.requireNonNull(inDescription, "inDescription");
      this.resolution =
        new TRStepSucceeded("");
    }

    @Override
    public TRStep toStep()
    {
      return new TRStep(this.description, this.resolution);
    }

    @Override
    public void setStepResolution(
      final TRStepResolutionType inResolution)
    {
      this.resolution =
        Objects.requireNonNull(inResolution, "resolution");
    }
  }
}
