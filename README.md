## A factory of tuples in Java

Tuples
- can be exchanged with different part of the program (if they have the same shape)
- tuples are erased so a tuple that takes a (String, int) and a tuple (Object, int) are comptible if the Object is a String at runtime
- the implementation avoid boxing if possible
- the implementation works hard so the JIT can remove unnecessary boxing

WARNING! to work correctly in terms of performance, all `TupleFactory` must be stored in a static final field 

### Example of usage

```java
class Demo {
  private static final TupleFactory FACTORY = TupleFactory.of(String.class, int.class);
  
  void foo() {
    var tuple = FACTORY.tuple("foo", 2);
    
    System.out.println((String) factory.get(tuple, 0));  // foo
    System.out.println(factory.getInt(tuple, 1));  // 2
    
    bar(tuple);
  }
  
  void bar(Object tuple) {
    FACTORY.requireTuple(tuple);  // dynamic check
    ...
  }
}  
```
