package com.justinblank.classcompiler;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.*;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class ClassCompilerTest {

    public static final AtomicInteger CLASS_NAME_COUNTER = new AtomicInteger();

    @Test
    public void testCall() throws Exception {
        String testClassName = testClassName();
        ClassBuilder builder = new ClassBuilder(testClassName, "", "java/lang/Object", new String[]{});
        builder.addEmptyConstructor();
        var vars = new GenericVars();
        vars.addVar("string");
        var method = builder.mkMethod("foo", List.of(CompilerUtil.STRING_DESCRIPTOR), "I", vars);

        var body = method.addBlock();

        body.readVar(vars, "string", CompilerUtil.STRING_DESCRIPTOR);
        body.call("length","java/lang/String","()I");
        body.addReturn(Opcodes.IRETURN);

        Class<?> c = new ClassCompiler(builder).generateClass();
        Object o = c.getConstructors()[0].newInstance();
    }

    @Test
    public void testEliminatesDeadPrivateMethods() throws Exception {
        var testClassName = testClassName();
        ClassBuilder builder = new ClassBuilder(testClassName, "", "java/lang/Object", new String[]{});
        builder.addEmptyConstructor();
        var vars = new GenericVars();
        vars.addVar("string");

        var method = builder.mkMethod("foo", List.of(CompilerUtil.STRING_DESCRIPTOR), "I", vars, ACC_PRIVATE);

        var body = method.addBlock();

        body.readVar(vars,"string", CompilerUtil.STRING_DESCRIPTOR);
        body.call("length","java/lang/String","()I");
        body.addReturn(Opcodes.IRETURN);

        Class<?> c = new ClassCompiler(builder).generateClass();
        Object o = c.getConstructors()[0].newInstance();
        assertThrows(NoSuchMethodException.class, () -> o.getClass().getMethod("foo"));
    }

    @Test
    public void testCanCompileWithoutInterfaces() throws Exception {
        var testClassName = testClassName();
        ClassBuilder builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        builder.addEmptyConstructor();

        Class<?> c = new ClassCompiler(builder).generateClass();
        Object o = c.getConstructors()[0].newInstance();
    }

    @Test
    public void testConstructorSkeleton() {
        var testClassName = testClassName();
        ClassBuilder builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);

    }

    @Test
    public void test_addBooleanArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        builder.addArrayConstant("BOOLS", Opcodes.ACC_PUBLIC, new boolean[] { true, true, false });
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField("BOOLS"));
        boolean[] array = (boolean[]) c.getDeclaredField("BOOLS").get(null);
        assertEquals(3, array.length);
        assertTrue(array[0]);
        assertTrue(array[1]);
        assertFalse(array[2]);
    }

    @Test
    public void test_addLargeBooleanArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        boolean[] bools = new boolean[Block.MAX_ARRAY_LITERAL_LENGTH];
        bools[129] = true;
        builder.addArrayConstant("BOOLS", Opcodes.ACC_PUBLIC, bools);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField("BOOLS"));
        boolean[] array = (boolean[]) c.getDeclaredField("BOOLS").get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            if (i != 129) {
                assertFalse(array[i]);
            }
            else {
                assertTrue(array[i]);
            }
        }
    }

    @Test
    public void test_addLargeIntArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        int[] ints = new int[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            ints[i] = i * 31;
        }

        String arrayName = "INTS";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, ints);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        int[] array = (int[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            assertEquals(i * 31, array[i]);
        }
    }


    @Test
    public void test_addLargeLongArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        long[] longs = new long[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            longs[i] = ((long) i) * 31;
        }

        String arrayName = "LONGS";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, longs);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        long[] array = (long[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            assertEquals(i * 31, array[i]);
        }
    }

    @Test
    public void test_addLargeFloatArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        float[] floats = new float[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            floats[i] = (float) (i * 31.1);
        }

        String arrayName = "FLOATS";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, floats);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        float[] array = (float[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            float delta = (float) (i * 31.1 - array[i]);
            assertTrue(delta < .1);
        }
    }


    @Test
    public void test_addLargeDoubleArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        double[] floats = new double[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            floats[i] = i * 31.1;
        }

        String arrayName = "FLOATS";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, floats);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        double[] array = (double[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            double delta = i * 31.1 - array[i];
            assertTrue(delta < .1);
        }
    }

    @Test
    public void test_addLargeCharArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        char[] chars = new char[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            chars[i] = (char) i;
        }

        String arrayName = "CHARS";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, chars);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        char[] array = (char[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            assertEquals((char) i, chars[i]);
        }
    }

    @Test
    public void test_addLargeByteArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        byte[] bytes = new byte[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            bytes[i] = (byte) (i % Byte.MAX_VALUE);
        }

        String arrayName = "BYTES";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, bytes);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        byte[] array = (byte[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            assertEquals((byte) (i % Byte.MAX_VALUE), bytes[i]);
        }
    }

    @Test
    public void test_addLargeShortArrayConstant() throws Exception {
        var testClassName = testClassName();
        var builder = new ClassBuilder(testClassName, "", "java/lang/Object", null);
        short[] shorts = new short[Block.MAX_ARRAY_LITERAL_LENGTH];
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            shorts[i] = (short) i;
        }

        String arrayName = "SHORTS";
        builder.addArrayConstant(arrayName, Opcodes.ACC_PUBLIC, shorts);
        Class<?> c = new ClassCompiler(builder, true, System.out).generateClass();
        assertNotNull(c.getDeclaredField(arrayName));
        short[] array = (short[]) c.getDeclaredField(arrayName).get(null);
        assertEquals(Block.MAX_ARRAY_LITERAL_LENGTH, array.length);
        for (int i = 0; i < Block.MAX_ARRAY_LITERAL_LENGTH; i++) {
            assertEquals((short) i, shorts[i]);
        }
    }

    public static String testClassName() {
        return "TestClass" + CLASS_NAME_COUNTER.incrementAndGet();
    }
}
