package com.justinblank.classcompiler;

import com.justinblank.classcompiler.lang.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.justinblank.classcompiler.lang.Literal.literal;
import static org.objectweb.asm.Opcodes.*;

public class Method {

    public final String methodName;
    private TypeInference typeInference;
    private final Map<String, TypeVariable> typeEnvironment = new HashMap<>();
    private String className;
    final int modifiers;
    final List<String> arguments;
    final List<Block> blocks;
    final String returnType;
    private final Vars matchingVars;
    private final Map<String, Object> attributes = new HashMap<>();
    private List<CodeElement> elements = new ArrayList<>();

    private final Stack<Block> currentBlock = new Stack<>();
    private final Stack<Block> currentLoop = new Stack<>();

    /**
     * Method constructor
     * @param methodName the method name
     * @param arguments the arguments, as class descriptors
     * @param returnType the return type, as a class descriptor
     * @param vars the variables the method will have
     */
    public Method(String methodName, List<String> arguments, String returnType, Vars vars) {
        this(methodName, arguments, returnType, vars, ACC_PUBLIC);
    }

    /**
     * Method constructor
     * @param methodName the method name
     * @param arguments the arguments, as class descriptors
     * @param cls the return type
     * @param vars the variables the method will have
     */
    public Method(String methodName, List<String> arguments, Class<?> cls, Vars vars) {
        this(methodName, arguments, ReferenceType.of(cls), vars);
    }

    /**
     * Method constructor
     * @param methodName the method name
     * @param arguments the arguments, as class descriptors
     * @param returnType the return type
     * @param vars the variables the method will have
     */
    public Method(String methodName, List<String> arguments, Type returnType, Vars vars) {
        this(methodName, arguments, CompilerUtil.descriptor(returnType), vars, ACC_PUBLIC);
    }

    public Method(String methodName, List<String> arguments, String returnType, Vars matchingVars, int modifiers) {
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(arguments);
        Objects.requireNonNull(returnType);
        this.modifiers = modifiers;
        this.methodName = methodName;
        this.arguments = arguments;
        this.blocks = new ArrayList<>();
        this.returnType = returnType;
        this.matchingVars = matchingVars;
    }

    public Block addBlock() {
        Block block = new Block(this.blocks.size(), new ArrayList<>());
        this.blocks.add(block);
        return block;
    }

    public void addBlock(Block block) {
        block.number = this.blocks.size();
        this.blocks.add(block);
    }

    /**
     * Create a new block and insert after the passed block
     *
     * @param block the block after which the new block will be inserted
     * @return the new block
     */
    public Block addBlockAfter(Block block) {
        var inserted = new Block(block.number, new ArrayList<>());
        this.blocks.add(block.number + 1, inserted);
        for (int i = block.number + 1; i < blocks.size(); i++) {
            this.blocks.get(i).number++;
        }
        return inserted;
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String descriptor() {
        return "(" + StringUtils.join(arguments, "") + ")" + returnType;
    }

    public Optional<Vars> getMatchingVars() {
        return Optional.ofNullable(matchingVars);
    }

    @Override
    public String toString() {
        return "Method{" +
                "methodName='" + methodName + '\'' +
                ", arguments=" + arguments +
                ", returnType='" + returnType + '\'' +
                ", blockCount=" + blocks.size() +
                '}';
    }

    public int operationCount() {
        int count = 0;
        for (var block : blocks) {
            count += block.operations.size();
        }
        return count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, modifiers);
    }

    // TODO: fix n-squared
    public void insertBlocks(Block anchor, List<Block> newBlocks) {
        var i = blocks.indexOf(anchor) + 1;
        for (var block : newBlocks) {
            block.number = i;
            blocks.add(i++, block);
            for (var j = i; j < blocks.size(); j++) {
                blocks.get(j).number++;
            }
        }

    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public Conditional cond(Expression condition) {
        var conditional = new Conditional(condition);
        this.elements.add(conditional);
        return conditional;
    }

    public Method loop(Expression condition, List<CodeElement> body) {
        var loop = new Loop(condition, body);
        this.elements.add(loop);
        return this;
    }

    public Method set(String variable, Expression expression) {
        this.elements.add(CodeElement.set(variable, expression));
        return this;
    }

    public Method set(String variable, Number expression) {
        this.elements.add(CodeElement.set(variable, literal(expression)));
        return this;
    }

    public Method arraySet(Expression arrayRef, Expression index, Expression value) {
        this.elements.add(ArraySet.arraySet(arrayRef, index, value));
        return this;
    }

    public Method arrayRead(Expression arrayRef, Expression index) {
        this.elements.add(ArrayRead.arrayRead(arrayRef, index));
        return this;
    }

    public Method returnValue(Expression expression) {
        this.elements.add(CodeElement.returnValue(expression));
        return this;
    }

    public Method returnValue(Number number) {
        this.elements.add(CodeElement.returnValue(number));
        return this;
    }

    public Method call(String methodName, Type type, Expression... expressions) {
        this.elements.add(CodeElement.call(methodName, type, expressions));
        return this;
    }

    public Method call(String methodName, Class<?> type, Expression... expressions) {
        this.elements.add(CodeElement.call(methodName, type, expressions));
        return this;
    }

    public Method callStatic(String className, String methodName, Type type, Expression...expressions) {
        this.elements.add(CodeElement.callStatic(className, methodName, type, expressions));
        return this;
    }

    public Method callStatic(String className, String methodName, Class<?> type, Expression...expressions) {
        this.elements.add(CodeElement.callStatic(className, methodName, type, expressions));
        return this;
    }

    void resolve() {
        this.addBlock();
        if (this.matchingVars != null) {
            for (var v : this.matchingVars.allVars()) {
                if (v.getRight() < this.arguments.size() + 1) {
                    typeEnvironment.put(v.getLeft(), typeVariableFor(this.arguments.get(v.getRight() - 1)));
                }
            }
        }
        for (var element : elements) {
            typeInference.analyze(element, typeEnvironment);
            resolve(element);
        }
        this.elements = new ArrayList<>();
    }

    private TypeVariable typeVariableFor(String s) {
        switch (s) {
            case "I":
                return TypeVariable.of(Builtin.I);
            case "F":
                return TypeVariable.of(Builtin.F);
            case "L":
                return TypeVariable.of(Builtin.L);
            case "D":
                return TypeVariable.of(Builtin.D);
            case "Z":
                return TypeVariable.of(Builtin.BOOL);
            case "B":
                return TypeVariable.of(Builtin.OCTET);
            default:
                return TypeVariable.of(ReferenceType.of(CompilerUtil.internalName(s)));
        }
    }

    void resolve(CodeElement element) {
        if (element instanceof Literal) {
            var lit = (Literal) element;
            if (lit.value instanceof Integer) {
                var value = (Integer) lit.value;
                currentBlock().push(value);
            }
            else if (lit.value instanceof Double) {
                var value = (Double) lit.value;
                currentBlock().push(value);
            }
            else if (lit.value instanceof Float) {
                var value = (Float) lit.value;
                currentBlock().push(value);
            }
            else if (lit.value instanceof Long) {
                var value = (Long) lit.value;
                currentBlock().push(value);
            }
        } else if (element instanceof ReturnExpression) {
            var returnExpression = (ReturnExpression) element;
            resolve(returnExpression.expression);
            currentBlock().addReturn(CompilerUtil.returnForType(returnType));
        } else if (element instanceof VariableRead) {
            var read = (VariableRead) element;
            currentBlock().readVar(getMatchingVars().get().indexByName(read.variable), descriptorForExpression(read));
        } else if (element instanceof FieldReference) {
            var fieldReference = (FieldReference) element;
            resolve(fieldReference.expression);
            var type = typeInference.analyze(fieldReference.expression, typeEnvironment);
            currentBlock().readField(fieldReference.fieldName, CompilerUtil.internalName(type),
                    CompilerUtil.descriptor(fieldReference.type));
        } else if (element instanceof StaticFieldReference) {
            var staticFieldReference = (StaticFieldReference) element;
            currentBlock().readStatic(staticFieldReference.fieldName,
                    CompilerUtil.internalName(staticFieldReference.receiver),
                    CompilerUtil.descriptor(staticFieldReference.type));
        } else if (element instanceof Assignment) {
            var assignment = (Assignment) element;
            resolve(assignment.expression);
            currentBlock().setVar(getMatchingVars().get().indexByName(assignment.variable), descriptorForExpression(assignment.expression));
        } else if (element instanceof Binary) {
            var operation = (Binary) element;
            switch (operation.operator) {
                case EQUALS:
                case LESS_THAN:
                case GREATER_THAN:
                case NOT_EQUALS:
                    var block = addBlock();
                    withBlock(block, () -> {
                        resolve(operation.left);
                        resolve(operation.right);
                        var eqBlock = addBlock().push(1);
                        var finalBlock = addBlock();

                        block.jump(eqBlock, operation.asmOP(this))
                                .push(0)
                                .jump(finalBlock, GOTO);
                    });

                    return;
                default:
                    resolve(operation.left);
                    resolve(operation.right);
                    currentBlock().operate(operation.asmOP(this));
                    return;
            }
        } else if (element instanceof Loop) {
            var loop = (Loop) element;
            var conditionsBlock = currentBlock.push(addBlock());
            currentLoop.push(conditionsBlock);
            if (loop.condition != null) {
                resolve(loop.condition);
            }
            var block = addBlock();

            withBlock(addBlock(), () -> {
                for (var codeElement : loop.body) {
                    resolve(codeElement);
                    if (producesValue(codeElement)) {
                        currentBlock().operate(POP);
                    }
                }
            });

            addBlock().jump(conditionsBlock, GOTO);
            var afterLoop = addBlock();
            if (loop.condition != null) {
                block.jump(afterLoop, IFEQ);
            }
            currentLoop.pop();
            currentBlock.push(afterLoop);
            for (var x : this.blocks) {
                for (var op : x.operations) {
                    if (op.inst == Operation.Inst.JUMP && op.target == Block.POSTLOOP) {
                        op.target = afterLoop;
                    }
                }
            }
        }
        else if (element instanceof Skip) {
            var loopBlock = currentLoop.peek();
            currentBlock().jump(loopBlock, GOTO);
        }
        else if (element instanceof Escape) {
            currentBlock().jump(Block.POSTLOOP, GOTO);
        }
        else if (element instanceof ThisRef) {
            currentBlock().readThis();
        }
        else if (element instanceof Call) {
            var call = (Call) element;
            for (var i = 0; i <= call.arguments.length - 1; i++) {
                resolve(call.arguments[i]);
            }
            var className = call.isStatic ? call.className : CompilerUtil.internalName(getClassName(call.receiver()));
            if (call.isStatic) {
                currentBlock().callStatic(call.methodName, className, buildDescriptor(call));
            }
            else {
                currentBlock().call(call.methodName, className, buildDescriptor(call));
            }
        }
        else if (element instanceof Constructor) {
            var constructor = (Constructor) element;
            for (var arg : constructor.arguments) {
                resolve(arg);
            }
            currentBlock().addOperation(Operation.mkConstructor(CompilerUtil.internalName(constructor.returnType)));
            currentBlock().operate(DUP);
            currentBlock().call("<init>", CompilerUtil.internalName(constructor.returnType), "()V", true);
        }
        else if (element instanceof Conditional) {
            var cond = (Conditional) element;
            currentBlock.push(addBlock());
            resolve(cond.condition);
            var block = addBlock();

            withBlock(addBlock(), () -> {
                for (var codeElement : cond.body) {
                    resolve(codeElement);
                }
            });
            var afterLoop = addBlock();
            block.jump(afterLoop, IFEQ);
            currentBlock.push(afterLoop);
        }
        else if (element instanceof NewArray) {
            currentBlock.push(this.addBlock());
            var newArray = (NewArray) element;
            resolve(newArray.size);
            if (newArray.type instanceof Builtin) {
                switch ((Builtin) newArray.type) {
                    case I:
                        currentBlock().newArray(T_INT);
                        break;
                    case F:
                        currentBlock().newArray(T_FLOAT);
                        break;
                    case D:
                        currentBlock().newArray(T_DOUBLE);
                        break;
                    case L:
                        currentBlock().newArray(T_LONG);
                        break;
                    case BOOL:
                        currentBlock().newArray(T_BOOLEAN);
                        break;
                    case OCTET:
                        currentBlock().newArray(T_BYTE);
                        break;
                }
                currentBlock.pop();
            }
            else if (newArray.type instanceof ReferenceType) {
                currentBlock.peek().newArray(CompilerUtil.internalName(newArray.type.typeString()));
                currentBlock.pop();
            }
            else if (newArray.type instanceof ArrayType) {
                currentBlock.peek().newArray(CompilerUtil.internalName(newArray.type.typeString()));
                currentBlock.pop();
            }
            else {
                throw new IllegalStateException("Unhandled variant of newArray type" + newArray.type);
            }
        }
        else if (element instanceof ArrayRead) {
            var arrayRead = (ArrayRead) element;
            resolve(arrayRead.arrayRef);
            resolve(arrayRead.index);
            var arrayType = determineArrayType(arrayRead.arrayRef);
            if (arrayType.elementType instanceof Builtin) {
                switch ((Builtin) arrayType.elementType) {
                    case I:
                        currentBlock().operate(IALOAD);
                        return;
                    case F:
                        currentBlock().operate(FALOAD);
                        return;
                    case L:
                        currentBlock().operate(LALOAD);
                        return;
                    case D:
                        currentBlock().operate(DALOAD);
                        return;
                    case BOOL:
                    case OCTET:
                        currentBlock().operate(BALOAD);
                        return;
                }
            }
            else {
                currentBlock().operate(AALOAD);
            }
        }
        else if (element instanceof ArraySet) {
            var arraySet = (ArraySet) element;
            resolve(arraySet.arrayRef);
            resolve(arraySet.index);
            resolve(arraySet.value);
            ArrayType arrayType = determineArrayType(arraySet.arrayRef);
            if (arrayType.elementType instanceof Builtin) {
                switch ((Builtin) arrayType.elementType) {
                    case I:
                        currentBlock().operate(IASTORE);
                        return;
                    case F:
                        currentBlock().operate(FASTORE);
                        return;
                    case L:
                        currentBlock().operate(LASTORE);
                        return;
                    case D:
                        currentBlock().operate(DASTORE);
                        return;
                    case BOOL:
                    case OCTET:
                        currentBlock().operate(BASTORE);
                        return;
                }
            }
            else {
                currentBlock().operate(AASTORE);
            }
        } else if (element instanceof ArrayLength) {
            var arrayLength = (ArrayLength) element;
            resolve(arrayLength.expression);
            currentBlock().operate(ARRAYLENGTH);

        }
    }

    private ArrayType determineArrayType(Expression arrayRef) {
        var analyzedType = typeInference.analyze(arrayRef, typeEnvironment);
        if (analyzedType instanceof TypeVariable) {
            analyzedType = analyzedType.type();
        }
        var arrayType = (ArrayType) analyzedType;
        return arrayType;
    }

    private boolean producesValue(CodeElement codeElement) {
        // TODO: void call?
        if (codeElement instanceof Expression) {
            if (codeElement instanceof Call) {
                var call = (Call) codeElement;
                if (call.returnType != null) {
                    return true;
                }
            }
            else {
                return true;
            }
        }
        return false;
    }

    private void withBlock(Block body, Runnable r) {
        currentBlock.push(body);
        r.run();
        currentBlock.pop();
    }

    // TODO relocate?
    public Type typeOf(Expression expression) {
        return typeInference.analyze(expression, typeEnvironment);
    }

    private String getClassName(Expression argument) {
        if (argument instanceof ThisRef) {
            return className;
        }
        else {
            var type = typeInference.analyze(argument, typeEnvironment);
            if (type instanceof Builtin) {
                return type.toString();
            }
            else if (type instanceof ReferenceType) {
                return ((ReferenceType) type).typeString;
            }
            else {
                var typeVar = (TypeVariable) type;
                var resolved = typeVar.type();
                if (resolved != null) {
                    return resolved.typeString();
                }
                throw new UnsupportedOperationException("TODO");
            }
        }
    }


    private String buildDescriptor(Call call) {
        var sb = new StringBuilder();
        sb.append('(');
        int initialIndex = call.isStatic ? 0 : 1;
        for (var i = initialIndex; i < call.arguments.length; i++) {
            var type = typeOf(call.arguments[i]);
            sb.append(CompilerUtil.descriptor(type));
        }
        sb.append(')');
        sb.append(CompilerUtil.descriptor(call.returnType));
        return sb.toString();
    }


    private Block currentBlock() {
        if (!currentBlock.isEmpty()) {
            return currentBlock.peek();
        }
        else {
            return this.blocks.get(this.blocks.size() - 1);
        }
    }

    private String descriptorForExpression(Expression expression) {
        var type = typeInference.analyze(expression, typeEnvironment);
        return CompilerUtil.descriptorForType(type);
    }

    public void add(CodeElement element) {
        elements.add(element);
    }

    public void setClassName(String className) {
        this.className = className;
        this.typeInference = new TypeInference(className);
    }
}
