A factory of tuples in Java

Tuples
- can be exchanged with different part of the program (if they have the same shape)
- tuples are erased so a tuple that takes a (String, int) and a tuple (Object, int) are comptible if the Object is a String at runtime
- the implementation avoid boxing if possible
- the implementation works hard so the JIT can remove unecessary boxing
