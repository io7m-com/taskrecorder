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

import java.util.Objects;
import java.util.Optional;

/**
 * A single step in task.
 */

public final class TRStep implements TRStepType
{
  private final Logger logger;
  private final String name;
  private TRResolutionType resolution;

  TRStep(
    final Logger inLogger,
    final String inName)
  {
    this.logger =
      Objects.requireNonNull(inLogger, "logger");
    this.name =
      Objects.requireNonNull(inName, "name");
    this.resolution = new TRSucceeded("");
  }

  @Override
  public void setSucceeded(
    final String message)
  {
    if (message.isEmpty()) {
      this.logger.debug("succeeded: {}", this.name);
    } else {
      this.logger.debug("succeeded: {}: {}", this.name, message);
    }

    this.resolution = new TRSucceeded(message);
  }

  @Override
  public String toString()
  {
    return "[TRStep %s %s]".formatted(this.name, this.resolution);
  }

  @Override
  public void setFailed(
    final String message,
    final Optional<Throwable> exception)
  {
    this.logger.debug("failure: {}: {}: ", this.name, message, exception.orElse(null));
    this.resolution = new TRFailed(message, exception);
  }

  @Override
  public TRResolutionType resolution()
  {
    return this.resolution;
  }

  @Override
  public String name()
  {
    return this.name;
  }
}
