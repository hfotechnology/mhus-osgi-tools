/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gwt.thirdparty.guava.common.primitives;

import static com.google.gwt.thirdparty.guava.common.base.Preconditions.checkArgument;
import static com.google.gwt.thirdparty.guava.common.base.Preconditions.checkElementIndex;
import static com.google.gwt.thirdparty.guava.common.base.Preconditions.checkNotNull;
import static com.google.gwt.thirdparty.guava.common.base.Preconditions.checkPositionIndexes;

import com.google.gwt.thirdparty.guava.common.annotations.Beta;
import com.google.gwt.thirdparty.guava.common.annotations.GwtCompatible;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

/* 
 * Static utility methods pertaining to {@code long} primitives, that are not
 * already found in either {@link Long} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained">
 * primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
@GwtCompatible
public final class Longs {
  private Longs() {}

  /* 
   * The number of bytes required to represent a primitive {@code long}
   * value.
   */
  public static final int BYTES = Long.SIZE / Byte.SIZE;

  /* 
   * The largest power of two that can be represented as a {@code long}.
   *
   * @since 10.0
   */
  public static final long MAX_POWER_OF_TWO = 1L << (Long.SIZE - 2);

  /* 
   * Returns a hash code for {@code value}; equal to the result of invoking
   * {@code ((Long) value).hashCode()}.
   *
   * <p>This method always return the value specified by {@link
   * Long#hashCode()} in java, which might be different from
   * {@code ((Long) value).hashCode()} in GWT because {@link Long#hashCode()}
   * in GWT does not obey the JRE contract.
   *
   * @param value a primitive {@code long} value
   * @return a hash code for the value
   */
  public static int hashCode(long value) {
    return (int) (value ^ (value >>> 32));
  }

  /* 
   * Compares the two specified {@code long} values. The sign of the value
   * returned is the same as that of {@code ((Long) a).compareTo(b)}.
   *
   * @param a the first {@code long} to compare
   * @param b the second {@code long} to compare
   * @return a negative value if {@code a} is less than {@code b}; a positive
   *     value if {@code a} is greater than {@code b}; or zero if they are equal
   */
  public static int compare(long a, long b) {
    return (a < b) ? -1 : ((a > b) ? 1 : 0);
  }

  /* 
   * Returns {@code true} if {@code target} is present as an element anywhere in
   * {@code array}.
   *
   * @param array an array of {@code long} values, possibly empty
   * @param target a primitive {@code long} value
   * @return {@code true} if {@code array[i] == target} for some value of {@code
   *     i}
   */
  public static boolean contains(long[] array, long target) {
    for (long value : array) {
      if (value == target) {
        return true;
      }
    }
    return false;
  }

  /* 
   * Returns the index of the first appearance of the value {@code target} in
   * {@code array}.
   *
   * @param array an array of {@code long} values, possibly empty
   * @param target a primitive {@code long} value
   * @return the least index {@code i} for which {@code array[i] == target}, or
   *     {@code -1} if no such index exists.
   */
  public static int indexOf(long[] array, long target) {
    return indexOf(array, target, 0, array.length);
  }

  // TODO(kevinb): consider making this public
  private static int indexOf(
      long[] array, long target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target) {
        return i;
      }
    }
    return -1;
  }

  /* 
   * Returns the start position of the first occurrence of the specified {@code
   * target} within {@code array}, or {@code -1} if there is no such occurrence.
   *
   * <p>More formally, returns the lowest index {@code i} such that {@code
   * java.util.Arrays.copyOfRange(array, i, i + target.length)} contains exactly
   * the same elements as {@code target}.
   *
   * @param array the array to search for the sequence {@code target}
   * @param target the array to search for as a sub-sequence of {@code array}
   */
  public static int indexOf(long[] array, long[] target) {
    checkNotNull(array, "array");
    checkNotNull(target, "target");
    if (target.length == 0) {
      return 0;
    }

    outer:
    for (int i = 0; i < array.length - target.length + 1; i++) {
      for (int j = 0; j < target.length; j++) {
        if (array[i + j] != target[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }

  /* 
   * Returns the index of the last appearance of the value {@code target} in
   * {@code array}.
   *
   * @param array an array of {@code long} values, possibly empty
   * @param target a primitive {@code long} value
   * @return the greatest index {@code i} for which {@code array[i] == target},
   *     or {@code -1} if no such index exists.
   */
  public static int lastIndexOf(long[] array, long target) {
    return lastIndexOf(array, target, 0, array.length);
  }

  // TODO(kevinb): consider making this public
  private static int lastIndexOf(
      long[] array, long target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target) {
        return i;
      }
    }
    return -1;
  }

  /* 
   * Returns the least value present in {@code array}.
   *
   * @param array a <i>nonempty</i> array of {@code long} values
   * @return the value present in {@code array} that is less than or equal to
   *     every other value in the array
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static long min(long... array) {
    checkArgument(array.length > 0);
    long min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    }
    return min;
  }

  /* 
   * Returns the greatest value present in {@code array}.
   *
   * @param array a <i>nonempty</i> array of {@code long} values
   * @return the value present in {@code array} that is greater than or equal to
   *     every other value in the array
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static long max(long... array) {
    checkArgument(array.length > 0);
    long max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    }
    return max;
  }

  public static long[] concat(long[]... arrays) {
    int length = 0;
    for (long[] array : arrays) {
      length += array.length;
    }
    long[] result = new long[length];
    int pos = 0;
    for (long[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    }
    return result;
  }

  public static byte[] toByteArray(long value) {
    // Note that this code needs to stay compatible with GWT, which has known
    // bugs when narrowing byte casts of long values occur.
    byte[] result = new byte[8];
    for (int i = 7; i >= 0; i--) {
      result[i] = (byte) (value & 0xffL);
      value >>= 8;
    }
    return result;
  }

  public static long fromByteArray(byte[] bytes) {
    checkArgument(bytes.length >= BYTES,
        "array too small: %s < %s", bytes.length, BYTES);
    return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3],
        bytes[4], bytes[5], bytes[6], bytes[7]) ;
  }

  public static long fromBytes(byte b1, byte b2, byte b3, byte b4,
      byte b5, byte b6, byte b7, byte b8) {
    return (b1 & 0xFFL) << 56
        | (b2 & 0xFFL) << 48
        | (b3 & 0xFFL) << 40
        | (b4 & 0xFFL) << 32
        | (b5 & 0xFFL) << 24
        | (b6 & 0xFFL) << 16
        | (b7 & 0xFFL) << 8
        | (b8 & 0xFFL);
  }

  /* 
   * Parses the specified string as a signed decimal long value. The ASCII
   * character {@code '-'} (<code>'&#92;u002D'</code>) is recognized as the
   * minus sign.
   *
   * <p>Unlike {@link Long#parseLong(String)}, this method returns
   * {@code null} instead of throwing an exception if parsing fails.
   *
   * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even
   * under JDK 7, despite the change to {@link Long#parseLong(String)} for
   * that version.
   *
   * @param string the string representation of a long value
   * @return the long value represented by {@code string}, or {@code null} if
   *     {@code string} has a length of zero or cannot be parsed as a long
   *     value
   * @since 14.0
   */
  @Beta
  public static Long tryParse(String string) {
    if (checkNotNull(string).isEmpty()) {
      return null;
    }
    boolean negative = string.charAt(0) == '-';
    int index = negative ? 1 : 0;
    if (index == string.length()) {
      return null;
    }
    int digit = string.charAt(index++) - '0';
    if (digit < 0 || digit > 9) {
      return null;
    }
    long accum = -digit;
    while (index < string.length()) {
      digit = string.charAt(index++) - '0';
      if (digit < 0 || digit > 9 || accum < Long.MIN_VALUE / 10) {
        return null;
      }
      accum *= 10;
      if (accum < Long.MIN_VALUE + digit) {
        return null;
      }
      accum -= digit;
    }

    if (negative) {
      return accum;
    } else if (accum == Long.MIN_VALUE) {
      return null;
    } else {
      return -accum;
    }
  }

  /* 
   * Returns an array containing the same values as {@code array}, but
   * guaranteed to be of a specified minimum length. If {@code array} already
   * has a length of at least {@code minLength}, it is returned directly.
   * Otherwise, a new array of size {@code minLength + padding} is returned,
   * containing the values of {@code array}, and zeroes in the remaining places.
   *
   * @param array the source array
   * @param minLength the minimum length the returned array must guarantee
   * @param padding an extra amount to "grow" the array by if growth is
   *     necessary
   * @throws IllegalArgumentException if {@code minLength} or {@code padding} is
   *     negative
   * @return an array containing the values of {@code array}, with guaranteed
   *     minimum length {@code minLength}
   */
  public static long[] ensureCapacity(
      long[] array, int minLength, int padding) {
    checkArgument(minLength >= 0, "Invalid minLength: %s", minLength);
    checkArgument(padding >= 0, "Invalid padding: %s", padding);
    return (array.length < minLength)
        ? copyOf(array, minLength + padding)
        : array;
  }

  // Arrays.copyOf() requires Java 6
  private static long[] copyOf(long[] original, int length) {
    long[] copy = new long[length];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
    return copy;
  }

  /* 
   * Returns a string containing the supplied {@code long} values separated
   * by {@code separator}. For example, {@code join("-", 1L, 2L, 3L)} returns
   * the string {@code "1-2-3"}.
   *
   * @param separator the text that should appear between consecutive values in
   *     the resulting string (but not at the start or end)
   * @param array an array of {@code long} values, possibly empty
   */
  public static String join(String separator, long... array) {
    checkNotNull(separator);
    if (array.length == 0) {
      return "";
    }

    // For pre-sizing a builder, just get the right order of magnitude
    StringBuilder builder = new StringBuilder(array.length * 10);
    builder.append(array[0]);
    for (int i = 1; i < array.length; i++) {
      builder.append(separator).append(array[i]);
    }
    return builder.toString();
  }

  /* 
   * Returns a comparator that compares two {@code long} arrays
   * lexicographically. That is, it compares, using {@link
   * #compare(long, long)}), the first pair of values that follow any
   * common prefix, or when one array is a prefix of the other, treats the
   * shorter array as the lesser. For example,
   * {@code [] < [1L] < [1L, 2L] < [2L]}.
   *
   * <p>The returned comparator is inconsistent with {@link
   * Object#equals(Object)} (since arrays support only identity equality), but
   * it is consistent with {@link Arrays#equals(long[], long[])}.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Lexicographical_order">
   *     Lexicographical order article at Wikipedia</a>
   * @since 2.0
   */
  public static Comparator<long[]> lexicographicalComparator() {
    return LexicographicalComparator.INSTANCE;
  }

  private enum LexicographicalComparator implements Comparator<long[]> {
    INSTANCE;

    @Override
    public int compare(long[] left, long[] right) {
      int minLength = Math.min(left.length, right.length);
      for (int i = 0; i < minLength; i++) {
        int result = Longs.compare(left[i], right[i]);
        if (result != 0) {
          return result;
        }
      }
      return left.length - right.length;
    }
  }

  /* 
   * Returns an array containing each value of {@code collection}, converted to
   * a {@code long} value in the manner of {@link Number#longValue}.
   *
   * <p>Elements are copied from the argument collection as if by {@code
   * collection.toArray()}.  Calling this method is as thread-safe as calling
   * that method.
   *
   * @param collection a collection of {@code Number} instances
   * @return an array containing the same values as {@code collection}, in the
   *     same order, converted to primitives
   * @throws NullPointerException if {@code collection} or any of its elements
   *     is null
   * @since 1.0 (parameter was {@code Collection<Long>} before 12.0)
   */
  public static long[] toArray(Collection<? extends Number> collection) {
    if (collection instanceof LongArrayAsList) {
      return ((LongArrayAsList) collection).toLongArray();
    }

    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    long[] array = new long[len];
    for (int i = 0; i < len; i++) {
      // checkNotNull for GWT (do not optimize)
      array[i] = ((Number) checkNotNull(boxedArray[i])).longValue();
    }
    return array;
  }

  /* 
   * Returns a fixed-size list backed by the specified array, similar to {@link
   * Arrays#asList(Object[])}. The list supports {@link List#set(int, Object)},
   * but any attempt to set a value to {@code null} will result in a {@link
   * NullPointerException}.
   *
   * <p>The returned list maintains the values, but not the identities, of
   * {@code Long} objects written to or read from it.  For example, whether
   * {@code list.get(0) == list.get(0)} is true for the returned list is
   * unspecified.
   *
   * @param backingArray the array to back the list
   * @return a list view of the array
   */
  public static List<Long> asList(long... backingArray) {
    if (backingArray.length == 0) {
      return Collections.emptyList();
    }
    return new LongArrayAsList(backingArray);
  }

  @GwtCompatible
  private static class LongArrayAsList extends AbstractList<Long>
      implements RandomAccess, Serializable {
    final long[] array;
    final int start;
    final int end;

    LongArrayAsList(long[] array) {
      this(array, 0, array.length);
    }

    LongArrayAsList(long[] array, int start, int end) {
      this.array = array;
      this.start = start;
      this.end = end;
    }

    @Override public int size() {
      return end - start;
    }

    @Override public boolean isEmpty() {
      return false;
    }

    @Override public Long get(int index) {
      checkElementIndex(index, size());
      return array[start + index];
    }

    @Override public boolean contains(Object target) {
      // Overridden to prevent a ton of boxing
      return (target instanceof Long)
          && Longs.indexOf(array, (Long) target, start, end) != -1;
    }

    @Override public int indexOf(Object target) {
      // Overridden to prevent a ton of boxing
      if (target instanceof Long) {
        int i = Longs.indexOf(array, (Long) target, start, end);
        if (i >= 0) {
          return i - start;
        }
      }
      return -1;
    }

    @Override public int lastIndexOf(Object target) {
      // Overridden to prevent a ton of boxing
      if (target instanceof Long) {
        int i = Longs.lastIndexOf(array, (Long) target, start, end);
        if (i >= 0) {
          return i - start;
        }
      }
      return -1;
    }

    @Override public Long set(int index, Long element) {
      checkElementIndex(index, size());
      long oldValue = array[start + index];
      // checkNotNull for GWT (do not optimize)
      array[start + index] = checkNotNull(element);
      return oldValue;
    }

    @Override public List<Long> subList(int fromIndex, int toIndex) {
      int size = size();
      checkPositionIndexes(fromIndex, toIndex, size);
      if (fromIndex == toIndex) {
        return Collections.emptyList();
      }
      return new LongArrayAsList(array, start + fromIndex, start + toIndex);
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      }
      if (object instanceof LongArrayAsList) {
        LongArrayAsList that = (LongArrayAsList) object;
        int size = size();
        if (that.size() != size) {
          return false;
        }
        for (int i = 0; i < size; i++) {
          if (array[start + i] != that.array[that.start + i]) {
            return false;
          }
        }
        return true;
      }
      return super.equals(object);
    }

    @Override public int hashCode() {
      int result = 1;
      for (int i = start; i < end; i++) {
        result = 31 * result + Longs.hashCode(array[i]);
      }
      return result;
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder(size() * 10);
      builder.append('[').append(array[start]);
      for (int i = start + 1; i < end; i++) {
        builder.append(", ").append(array[i]);
      }
      return builder.append(']').toString();
    }

    long[] toLongArray() {
      // Arrays.copyOfRange() is not available under GWT
      int size = size();
      long[] result = new long[size];
      System.arraycopy(array, start, result, 0, size);
      return result;
    }

    private static final long serialVersionUID = 0;
  }
}
