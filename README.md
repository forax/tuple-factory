## A factory of tuples in Java

Tuples
- tuples are record generated at runtime
- a tuple can be exchanged with different part of the program (if they are compatible)
- tuples are erased so a tuple that takes a (String, int) and a tuple (Object, int) are compatible at runtime
  (tuples are covariant)
- the implementation avoid boxing if possible
- the implementation works hard so the JIT can remove unnecessary boxing

WARNING! to work correctly in terms of performance, all `TupleFactory` must be stored in a static final field 

### Examples

A simple example
```java
class Demo {
  private static final TupleFactory FACTORY = TupleFactory.of(String.class, int.class);
  
  void foo() {
    var tuple = FACTORY.tuple("foo", 2);
    
    System.out.println((String) factory.get(tuple, 0));  // foo
    System.out.println(factory.getInt(tuple, 1));  // 2
    System.out.println(tuple.getClass().isRecord());  // true
  }
}  
```

An example of covariance
```java
class Foo {
  private static final TupleFactory FACTORY = TupleFactory.of(String.class, int.class);
  
  void foo() {
    var tuple = FACTORY.tuple("foo", 2);
    new Bar().bar(tuple);
  }
}

class Bar {
  private static final TupleFactory FACTORY = TupleFactory.of(Object.class, int.class);
  
  void bar(Object tuple) {
    FACTORY.requireTuple(tuple);  // dynamic check
    System.out.println(tuple);  // Tuple/0x000003e0011148e0[0=foo, 1=2]
  } 
}
```
