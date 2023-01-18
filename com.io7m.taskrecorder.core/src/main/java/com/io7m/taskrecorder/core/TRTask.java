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

import java.util.List;
import java.util.Objects;

/**
 * An immutable record of a task.
 *
 * @param items      The task items (steps, subtasks)
 * @param resolution The task resolution
 * @param <T>        The type of values returned by succcessful tasks
 */

public record TRTask<T>(
  List<TRTaskItemType> items,
  TRTaskResolutionType<T> resolution)
  implements TRTaskItemType
{
  /**
   * An immutable record of a task.
   *
   * @param items      The task items (steps, subtasks)
   * @param resolution The task resolution
   */

  public TRTask
  {
    Objects.requireNonNull(items, "items");
    Objects.requireNonNull(resolution, "resolution");

    if (items.isEmpty()) {
      throw new IllegalArgumentException("Task item lists cannot be empty.");
    }
  }

  @Override
  public String description()
  {
    return this.items.get(0).description();
  }
}
