import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classloader.MyClassLoader;
import org.objectweb.asm.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class Sample {

    public static byte[] asmFibonacci() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "fibonacci", null, "java/lang/Object", null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "fibonacci", "(I)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(ICONST_1);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l1);
            mv.visitLabel(l0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISUB);
            mv.visitMethodInsn(INVOKEVIRTUAL, "fibonacci", "fibonacci", "(I)I", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISUB);
            mv.visitMethodInsn(INVOKEVIRTUAL, "fibonacci", "fibonacci", "(I)I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    public static Class<?> compilerMark1Fibonacci() {
        ClassBuilder cb = new ClassBuilder("fibonacci1", "java/lang/Object", new String[]{});
        cb.addEmptyConstructor();
        var vars = new GenericVars("x");
        var method = cb.mkMethod("fibonacci", List.of("I"), "I", vars);
        var initialBlock = method.addBlock();
        var return1Block = method.addBlock();

        initialBlock.readVar(vars, "x", "I").jump(return1Block, IFEQ);
        initialBlock.readVar(vars, "x", "I").push(1).jump(return1Block, IF_ICMPEQ);
        initialBlock.readThis().readVar(vars, "x", "I").push(1).operate(ISUB).
                call("fibonacci", "fibonacci1", "(I)I");
        initialBlock.readThis().readVar(vars, "x", "I").push(2).operate(ISUB).
                call("fibonacci", "fibonacci1", "(I)I");
        initialBlock.operate(IADD).addReturn(IRETURN);

        return1Block.push(1).addReturn(IRETURN);
        return new ClassCompiler(cb).generateClass();
    }

    public static void main(String[] args) {
        try {
            Class<?> cls = MyClassLoader.getInstance().loadClass("fibonacci", asmFibonacci());
            var o = cls.getDeclaredConstructors()[0].newInstance();
            System.out.println(o.getClass().getDeclaredMethod("fibonacci", int.class).invoke(o, 5));

            cls = compilerMark1Fibonacci();
            o = cls.getDeclaredConstructors()[0].newInstance();
            System.out.println(o.getClass().getDeclaredMethod("fibonacci", int.class).invoke(o, 5));
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}
