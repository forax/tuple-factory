package org.github.forax.tuplefactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TupleFactoryTest {
  //@Test
  public void example() {
    var factory = TupleFactory.of(String.class, int.class);
    var tuple = factory.tuple("foo", 2);
    System.out.println((String)factory.get(tuple, 0));  // foo
    System.out.println(factory.getInt(tuple, 1));  // 2
    System.out.println(tuple.getClass().isRecord());  // true
  }

  //@Test
  public void example2() {
    class Bar {
      private static final TupleFactory FACTORY = TupleFactory.of(Object.class, int.class);

      void bar(Object tuple) {
        FACTORY.requireTuple(tuple);  // dynamic check
        System.out.println(tuple);  // Tuple/0x000003e0011148e0[0=foo, 1=2]
      }
    }

    class Foo {
      private static final TupleFactory FACTORY = TupleFactory.of(String.class, int.class);

      void foo() {
        var tuple = FACTORY.tuple("foo", 2);
        new Bar().bar(tuple);
      }
    }

    new Foo().foo();
  }

  @Test
  public void tuple() {
    var factory = TupleFactory.of(String.class, int.class);
    var tuple = factory.tuple("foo", 2);
    assertAll(
        () -> assertEquals("foo", factory.get(tuple, 0)),
        () -> assertEquals(2, factory.getInt(tuple, 1))
    );
  }

  @Test
  public void tupleIsARecord() {
    var factory = TupleFactory.of(double.class, double.class);
    var tuple = factory.tuple(2.0, 4.0);
    assertTrue(tuple.getClass().isRecord());
  }

  @Test
  public void tupleEquals() {
    var factory = TupleFactory.of(String.class, int.class);
    var tuple = factory.tuple("foo", 2);
    var tuple2 = factory.tuple("foo", 2);
    assertEquals(tuple, tuple2);
  }

  @Test
  public void tupleHashCode() {
    var factory = TupleFactory.of(String.class, int.class);
    var tuple = factory.tuple("foo", 2);
    var tuple2 = factory.tuple("foo", 2);
    assertEquals(tuple.hashCode(), tuple2.hashCode());
  }

  @Test
  public void tupleString() {
    var factory = TupleFactory.of(String.class, int.class);
    var tuple = factory.tuple("foo", 2);
    var tuple2 = factory.tuple("foo", 2);
    assertEquals(tuple.toString(), tuple2.toString());
  }

  @Test
  public void requireTuple() {
    var factory = TupleFactory.of(String.class, double.class, int.class);
    var tuple = factory.tuple("foo", 3.0, 2);
    assertDoesNotThrow(() -> factory.requireTuple(tuple));
  }

  @Test
  public void requireTupleAndErasure() {
    var factory = TupleFactory.of(String.class, double.class, int.class);
    var tuple = factory.tuple("foo", 3.0, 2);
    var factory2 = TupleFactory.of(Object.class, double.class, int.class);
    assertDoesNotThrow(() -> factory2.requireTuple(tuple));
  }

  @Test
  public void get() {
    var factory = TupleFactory.of(String.class);
    var tuple = factory.tuple("foo");
    assertEquals("foo", factory.get(tuple, 0));
  }

  @Test
  public void getBooleanTrue() {
    var factory = TupleFactory.of(boolean.class);
    var tuple = factory.tuple(true);
    assertTrue(factory.getBoolean(tuple, 0));
  }

  @Test
  public void getBooleanFalse() {
    var factory = TupleFactory.of(boolean.class);
    var tuple = factory.tuple(false);
    assertFalse(factory.getBoolean(tuple, 0));
  }

  @Test
  public void getByte() {
    var factory = TupleFactory.of(byte.class);
    var tuple = factory.tuple((byte) 192);
    assertEquals((byte) 192, factory.getByte(tuple, 0));
  }

  @Test
  public void getShort() {
    var factory = TupleFactory.of(short.class);
    var tuple = factory.tuple((short) 32_000);
    assertEquals((short) 32_000, factory.getShort(tuple, 0));
  }

  @Test
  public void getChar() {
    var factory = TupleFactory.of(char.class);
    var tuple = factory.tuple('Z');
    assertEquals('Z', factory.getChar(tuple, 0));
  }

  @Test
  public void getInt() {
    var factory = TupleFactory.of(int.class);
    var tuple = factory.tuple(42);
    assertEquals(42, factory.getInt(tuple, 0));
  }

  @Test
  public void getFloat() {
    var factory = TupleFactory.of(float.class);
    var tuple = factory.tuple(2.f);
    assertEquals(2.f, factory.getFloat(tuple, 0));
  }

  @Test
  public void getLong() {
    var factory = TupleFactory.of(long.class);
    var tuple = factory.tuple(1234567890123L);
    assertEquals(1234567890123L, factory.getLong(tuple, 0));
  }

  @Test
  public void getDouble() {
    var factory = TupleFactory.of(double.class);
    var tuple = factory.tuple(0.123456789);
    assertEquals(0.123456789, factory.getDouble(tuple, 0));
  }
}