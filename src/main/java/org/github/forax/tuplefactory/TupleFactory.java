package org.github.forax.tuplefactory;

import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;

/**
 * A factory of tuples that all have the same shape (the component types).
 * For good performance, instance of {@link TupleFactory} should be stored as static final constant.
 *
 * @see #of(Class[])
 */
public interface TupleFactory {
  /**
   * Checks that a tuple is compatible the current factory
   * @param tuple a tuple
   * @return the tuple taken as parameter
   * @throws NullPointerException if the tuple is null
   * @throws ClassCastException if the tuple does not have the same erasure as the current factory
   */
  Object requireTuple(Object tuple);

  /**
   * Creates a tuple
   * @param args the tuple arguments
   * @return a new tuple
   * @throws java.lang.invoke.WrongMethodTypeException if the value are not compatible with the types
   *         used to create the current factory
   *
   * @see TupleFactory#of(Class[])
   */
  Object tuple(Object... args);

  /**
   * Extract an object value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @param <R> the type of the return value
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not an object
   */
  <R> R get(Object tuple, int componentIndex);

  /**
   * Extract a boolean value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a boolean
   */
  boolean getBoolean(Object tuple, int componentIndex);

  /**
   * Extract a byte value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a byte
   */
  byte getByte(Object tuple, int componentIndex);

  /**
   * Extract a short value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a short
   */
  short getShort(Object tuple, int componentIndex);

  /**
   * Extract a char value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a char
   */
  char getChar(Object tuple, int componentIndex);

  /**
   * Extract an int value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not an int
   */
  int getInt(Object tuple, int componentIndex);

  /**
   * Extract a float value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a float
   */
  float getFloat(Object tuple, int componentIndex);

  /**
   * Extract a long value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a long
   */
  long getLong(Object tuple, int componentIndex);

  /**
   * Extract a double value of a type given its component index
   * @param tuple a tuple created with the current factory
   * @param componentIndex an index
   * @return the value of the tuple at the index
   * @throws IndexOutOfBoundsException if the component index if not valid
   * @throws java.lang.invoke.WrongMethodTypeException if the component is not a double
   */
  double getDouble(Object tuple, int componentIndex);

  /**
   * Creates a factory of tuple
   * For good performance, instance of {@link TupleFactory} should be stored as static final constant.
   *
   * @param types the types of the tuple
   * @return a new factory
   * @throws NullPointerException if either types or one of the types is null
   * @see #tuple(Object...)
   */
  static TupleFactory of(Class<?>... types) {
    requireNonNull(types);
    return TupleFactoryImpl.createTupleFactory(methodType(Object.class, types));
  }
}
