package org.github.forax.tuplefactory;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.TypeKind;
import java.lang.classfile.attribute.RecordAttribute;
import java.lang.classfile.attribute.RecordComponentInfo;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PRIVATE;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.classfile.ClassFile.JAVA_23_VERSION;
import static java.lang.constant.ConstantDescs.CD_MethodHandle;
import static java.lang.constant.ConstantDescs.CD_MethodType;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.CD_boolean;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.constant.ConstantDescs.CD_void;
import static java.lang.constant.ConstantDescs.INIT_NAME;
import static java.lang.constant.DirectMethodHandleDesc.Kind.GETTER;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("preview")
record TupleFactoryImpl(Class<?> tupleClass, MethodHandle create, List<MethodHandle> components) implements TupleFactory {
  @Override
  public Object requireTuple(Object o) {
    requireNonNull(o);
    return tupleClass.cast(o);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T rethrow(Throwable t) throws T {
    throw (T) t;
  }

  @Override
  public Object tuple(Object... args) {
    try {
      return create.invokeExact(args);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R get(Object tuple, int componentIndex) {
    try {
      return (R) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public boolean getBoolean(Object tuple, int componentIndex) {
    try {
      return (boolean) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public byte getByte(Object tuple, int componentIndex) {
    try {
      return (byte) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public short getShort(Object tuple, int componentIndex) {
    try {
      return (short) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public char getChar(Object tuple, int componentIndex) {
    try {
      return (char) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public int getInt(Object tuple, int componentIndex) {
    try {
      return (int) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public float getFloat(Object tuple, int componentIndex) {
    try {
      return (float) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public long getLong(Object tuple, int componentIndex) {
    try {
      return (long) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  @Override
  public double getDouble(Object tuple, int componentIndex) {
    try {
      return (double) components.get(componentIndex).invokeExact(tuple);
    } catch (Throwable e) {
      throw rethrow(e);
    }
  }

  private static RecordComponentInfo[] recordComponentInfos(MethodTypeDesc methodTypedesc) {
    var parameterCount = methodTypedesc.parameterCount();
    var infos = new RecordComponentInfo[parameterCount];
    for(var i = 0; i < parameterCount; i++) {
      infos[i] = RecordComponentInfo.of("_" + i, methodTypedesc.parameterType(i));
    }
    return infos;
  }

  private static void withInit(ClassBuilder b, ClassDesc owner, MethodTypeDesc methodTypedesc) {
    b.withMethodBody(INIT_NAME, methodTypedesc, ACC_PUBLIC, cb -> {
      var slot = 1;
      for (var i = 0; i < methodTypedesc.parameterCount(); i++) {
        var desc = methodTypedesc.parameterType(i);
        var typeKind = TypeKind.from(desc);
        cb.aload(0);
        cb.loadLocal(typeKind, slot);
        cb.putfield(owner, "_" + i, desc);
        slot += typeKind.slotSize();
      }
      cb.aload(0);
      cb.invokespecial(ClassDesc.of("java.lang.Record"), INIT_NAME, MethodTypeDesc.of(CD_void), false);
      cb.return_();
    });
  }

  private static final DirectMethodHandleDesc OBJECT_METHODS_BOOTSTRAP;
  static {
    MethodHandle bootstrap;
    try {
      bootstrap = MethodHandles.publicLookup().findStatic(ObjectMethods.class, "bootstrap",
          methodType(Object.class, MethodHandles.Lookup.class, String.class, TypeDescriptor.class, Class.class, String.class, MethodHandle[].class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
    OBJECT_METHODS_BOOTSTRAP = (DirectMethodHandleDesc) bootstrap.describeConstable().orElseThrow();
  }

  private static void withTrampoline(ClassBuilder b, DirectMethodHandleDesc bootstrap) {
    b.withMethodBody(bootstrap.methodName(), bootstrap.invocationType(), ACC_PRIVATE|ACC_STATIC, cb -> {
      var constantCallSiteDesc = ConstantCallSite.class.describeConstable().orElseThrow();
      cb.new_(constantCallSiteDesc);
      cb.dup();
      cb.aload(0);
      cb.aload(1);
      cb.ldc(CD_MethodHandle);
      cb.aload(3);
      cb.aload(4);
      cb.aload(5);
      cb.invokestatic(bootstrap.owner(), bootstrap.methodName(), bootstrap.invocationType(), false);
      cb.checkcast(CD_MethodHandle);
      cb.aload(2);
      cb.checkcast(CD_MethodType);
      cb.invokevirtual(CD_MethodHandle, "asType", MethodTypeDesc.of(CD_MethodHandle, CD_MethodType));
      cb.invokespecial(constantCallSiteDesc, INIT_NAME, MethodTypeDesc.of(CD_void, CD_MethodHandle), false);
      cb.areturn();
    });
  }

  private static String recipe(int parameterCount) {
    var builder = new StringBuilder();
    var separator = "";
    for(var i = 0; i < parameterCount; i++) {
      builder.append(separator).append(i);
      separator = ";";
    }
    return builder.toString();
  }

  private static ConstantDesc[] bootstrapArgs(ClassDesc owner, MethodTypeDesc methodTypeDesc) {
    var parameterCount = methodTypeDesc.parameterCount();
    var bootstrapArgs = new ConstantDesc[2 + parameterCount];
    bootstrapArgs[0] = owner;
    bootstrapArgs[1] = recipe(parameterCount);
    for(var i = 0; i < parameterCount; i++) {
      var desc = methodTypeDesc.parameterType(i);
      bootstrapArgs[i + 2] = MethodHandleDesc.ofField(GETTER, owner, "_" + i, desc);
    }
    return bootstrapArgs;
  }

  private static void withEqual(ClassBuilder b, DirectMethodHandleDesc trampoline, ConstantDesc[] bootstrapArgs) {
    b.withMethodBody("equals", MethodTypeDesc.of(CD_boolean, CD_Object), ACC_PUBLIC, cb -> {
      cb.aload(0);
      cb.aload(1);
      cb.invokedynamic(DynamicCallSiteDesc.of(trampoline, "equals", MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object), bootstrapArgs));
      cb.ireturn();
    });
  }

  private static void withHashCode(ClassBuilder b, DirectMethodHandleDesc trampoline, ConstantDesc[] bootstrapArgs) {
    b.withMethodBody("hashCode", MethodTypeDesc.of(CD_int), ACC_PUBLIC, cb -> {
      cb.aload(0);
      cb.invokedynamic(DynamicCallSiteDesc.of(trampoline, "hashCode", MethodTypeDesc.of(CD_int, CD_Object), bootstrapArgs));
      cb.ireturn();
    });
  }

  private static void withToString(ClassBuilder b, DirectMethodHandleDesc trampoline, ConstantDesc[] bootstrapArgs) {
    b.withMethodBody("toString", MethodTypeDesc.of(CD_String), ACC_PUBLIC, cb -> {
      cb.aload(0);
      cb.invokedynamic(DynamicCallSiteDesc.of(trampoline, "toString", MethodTypeDesc.of(CD_String, CD_Object), bootstrapArgs));
      cb.areturn();
    });
  }

  private static byte[] createTupleClass(String packageName, MethodTypeDesc methodTypedesc) {
    var classfile =  ClassFile.of(ClassFile.StackMapsOption.GENERATE_STACK_MAPS);
    var owner = ClassDesc.of(packageName, "Tuple");
    return classfile.build(owner, b -> {
      b.withVersion(JAVA_23_VERSION, 0);
      b.withFlags(ACC_PUBLIC | ACC_FINAL);
      b.withSuperclass(ClassDesc.of("java.lang.Record"));
      b.with(RecordAttribute.of(recordComponentInfos(methodTypedesc)));
      for(var i = 0; i < methodTypedesc.parameterCount(); i++) {
        var desc = methodTypedesc.parameterType(i);
        b.withField("_" + i, desc, ACC_PRIVATE | ACC_FINAL);
      }
      withInit(b, owner, methodTypedesc);
      var bootstrap = OBJECT_METHODS_BOOTSTRAP;
      withTrampoline(b, bootstrap);
      var trampoline = MethodHandleDesc.ofMethod(bootstrap.kind(), owner, bootstrap.methodName(), bootstrap.invocationType());
      var bootstrapArgs = bootstrapArgs(owner, methodTypedesc);
      withEqual(b, trampoline, bootstrapArgs);
      withHashCode(b, trampoline, bootstrapArgs);
      withToString(b, trampoline, bootstrapArgs);
    });
  }

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  private static TupleFactoryImpl createTupleFactoryImpl(MethodType methodType) {
    var methodTypeDesc = methodType.describeConstable().orElseThrow();
    var data = createTupleClass(LOOKUP.lookupClass().getPackageName(), methodTypeDesc);
    MethodHandles.Lookup hiddenLookup;
    try {
      hiddenLookup = LOOKUP.defineHiddenClass(data, true, MethodHandles.Lookup.ClassOption.NESTMATE, MethodHandles.Lookup.ClassOption.STRONG);
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    }
    var tupleClass = hiddenLookup.lookupClass();
    MethodHandle constructor;
    try {
      constructor = hiddenLookup.findConstructor(tupleClass, methodType);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
    var create = constructor.asType(constructor.type().changeReturnType(Object.class));
    var components = new MethodHandle[methodType.parameterCount()];
    for(var i = 0; i < components.length; i++) {
      var parameterType = methodType.parameterType(i);
      try {
        components[i] = hiddenLookup.findGetter(tupleClass, "_" + i, parameterType)
            .asType(methodType(parameterType, Object.class));
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    return new TupleFactoryImpl(tupleClass, create, List.of(components));
  }

  private static final ConcurrentHashMap<String, TupleFactoryImpl> ERASED_MAP = new ConcurrentHashMap<>();

  static TupleFactoryImpl specialize(TupleFactoryImpl erased, MethodType methodType) {
    var create = erased.create
        .asSpreader(Object[].class, methodType.parameterCount());
    var components = new MethodHandle[erased.components.size()];
    for(var i = 0; i < components.length; i++) {
      var getterMethodType = methodType(methodType.parameterType(i), Object.class);
      components[i] = erased.components.get(i)
          .asType(getterMethodType)
          .asType(getterMethodType.erase());
    }
    return new TupleFactoryImpl(erased.tupleClass, create, List.of(components));
  }

  static TupleFactory createTupleFactory(MethodType methodType) {
    var erased= methodType.erase().changeReturnType(void.class);
    var descriptor = erased.descriptorString();
    var impl = ERASED_MAP.get(descriptor);
    if (impl == null) {
      impl = createTupleFactoryImpl(erased);
      ERASED_MAP.putIfAbsent(descriptor, impl);
    }
    return specialize(impl, methodType);
  }
}
