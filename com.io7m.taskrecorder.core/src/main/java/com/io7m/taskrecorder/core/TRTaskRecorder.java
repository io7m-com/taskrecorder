/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TRTaskRecorder<T> implements TRTaskRecorderType<T>
{
  private final String taskDescription;
  private final Optional<TRTaskRecorder<?>> parent;
  private final AtomicBoolean closed;

  private TRTaskRecorder(
    final Optional<TRTaskRecorder<?>> inParent,
    final String inTaskDescription)
  {
    this.parent =
      Objects.requireNonNull(inParent, "inParent");
    this.taskDescription =
      Objects.requireNonNull(inTaskDescription, "taskDescription");
    this.closed =
      new AtomicBoolean(false);
  }

  public static <T> TRTaskRecorderType<T> create(
    final String taskDescription)
  {
    return new TRTaskRecorder<>(Optional.empty(), taskDescription);
  }

  @Override
  public String toString()
  {
    return "[TRTaskRecorder '%s']".formatted(this.taskDescription);
  }

  @Override
  public <R> TRTaskRecorderType<R> beginSubtask(
    final String description)
  {
    this.checkNotClosed();

    return new TRTaskRecorder<>(
      Optional.of(this),
      description
    );
  }

  private void checkNotClosed()
  {
    if (this.closed.get()) {
      throw new IllegalStateException("Task recorder is closed.");
    }
  }

  @Override
  public void close()
  {
    if (this.closed.compareAndSet(false, true)) {
      // Nothing
    }
  }
}
