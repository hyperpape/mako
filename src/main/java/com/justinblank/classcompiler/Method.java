package com.justinblank.classcompiler;

import com.justinblank.classcompiler.lang.*;
import com.justinblank.classcompiler.lang.Void;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.justinblank.classcompiler.Operation.Inst.JUMP;
import static com.justinblank.classcompiler.lang.Literal.literal;
import static com.justinblank.classcompiler.lang.Void.VOID;
import static org.objectweb.asm.Opcodes.*;

public class Method {

    public final String methodName;
    private TypeInference typeInference;
    private final Map<String, TypeVariable> typeEnvironment = new HashMap<>();
    private String classPackage;
    private String className;
    final int modifiers;
    final List<String> arguments;
    private List<Block> blocks;

    public final String returnType;

    // TODO: should matchingVars really be nullable?
    private final Vars matchingVars;
    private final Map<String, Object> attributes = new HashMap<>();
    private List<CodeElement> elements = new ArrayList<>();

    private final Stack<Block> currentBlock = new Stack<>();

    // The blocks in this stack are the blocks where conditions of the loop are stored
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

    // TODO: consider removing these methods, not obvious they're useful anymore
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String descriptor() {
        return CompilerUtil.descriptor(arguments, returnType);
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

    public void setBlocks(List<Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public void addElement(CodeElement codeElement) {
        Objects.requireNonNull(codeElement, "Cannot add null element to Method");
        this.elements.add(codeElement);
    }

    public Conditional cond(Expression condition) {
        Objects.requireNonNull(condition, "Condition cannot be non-null");
        var conditional = new Conditional(condition);
        this.addElement(conditional);
        return conditional;
    }

    public Switch addSwitch(Expression expression) {
        var switchElement = new Switch(expression);
        this.addElement(switchElement);
        return switchElement;
    }

    public Method loop(Expression condition, List<CodeElement> body) {
        var loop = new Loop(condition, body);
        this.addElement(loop);
        return this;
    }

    public Method set(String variable, Expression expression) {
        this.addElement(CodeElement.set(variable, expression));
        return this;
    }

    public Method set(String variable, Number expression) {
        this.addElement(CodeElement.set(variable, literal(expression)));
        return this;
    }

    public Method fieldSet(FieldReference ref, Expression expression) {
        this.addElement(CodeElement.fieldSet(ref, expression));
        return this;
    }

    public Method arraySet(Expression arrayRef, Expression index, Expression value) {
        this.addElement(ArraySet.arraySet(arrayRef, index, value));
        return this;
    }

    public Method arrayRead(Expression arrayRef, Expression index) {
        this.addElement(ArrayRead.arrayRead(arrayRef, index));
        return this;
    }

    public Method returnValue(Expression expression) {
        this.addElement(CodeElement.returnValue(expression));
        return this;
    }

    public Method returnValue(Number number) {
        this.addElement(CodeElement.returnValue(number));
        return this;
    }

    public Method returnVoid() {
        this.addElement(CodeElement.returnVoid());
        return this;
    }

    public Method call(String methodName, Type type, Expression... expressions) {
        this.addElement(CodeElement.call(methodName, type, expressions));
        return this;
    }

    public Method call(String methodName, Class<?> type, Expression... expressions) {
        this.addElement(CodeElement.call(methodName, type, expressions));
        return this;
    }

    public Method callInterface(String methodName, Type type, Expression... expressions) {
        this.addElement(CodeElement.callInterface(methodName, type, expressions));
        return this;
    }

    public Method callInterface(String methodName, Class<?> type, Expression... expressions) {
        this.addElement(CodeElement.callInterface(methodName, type, expressions));
        return this;
    }

    public Method callStatic(String className, String methodName, Type type, Expression...expressions) {
        this.addElement(CodeElement.callStatic(className, methodName, type, expressions));
        return this;
    }

    public Method callStatic(String className, String methodName, Class<?> type, Expression...expressions) {
        this.addElement(CodeElement.callStatic(className, methodName, type, expressions));
        return this;
    }

    void resolve() {
        try {
            doTypeInference();

            this.addBlock();
            for (var element : elements) {
                resolveTopLevelElement(element);
            }
            pruneBlocks();
            // TODO: did I have a good reason for this? Tests pass without it
            this.elements = new ArrayList<>();
        }
        catch (Exception e) {
            var thrown = new ClassCompilationException("Error resolving method", e);
            thrown.setMethodName(methodName);
            throw thrown;
        }
    }

    private void doTypeInference() {
        if (this.matchingVars != null) {
            for (var v : this.matchingVars.allVars()) {
                if (v.getRight() <= this.arguments.size()) {
                    typeEnvironment.put(v.getLeft(), typeVariableFor(this.arguments.get(v.getRight() - 1)));
                }
            }
        }
        for (var element : elements) {
            typeInference.analyze(element, typeEnvironment);
        }
    }

    private void pruneBlocks() {
        // TODO: still a lot of ways to improve the pruning here
        // TODO: not sure how to test that this is working--can test that it doesn't break code, but
        // testing that we're actually pruning is going to be obnoxious/brittle
        Set<Integer> blocksToTake = new HashSet<>();
        for (var b : blocks) {
            if (!b.isEmpty()) {
                if (blocksToTake.isEmpty() || mustTake(b)) {
                    blocksToTake.add(b.number);
                }
                else {
                    var lastBlock = b.number;
                    while (!blocksToTake.contains(lastBlock)) {
                        lastBlock--;
                    }
                    var priorBlock = blocks.get(lastBlock);
                    priorBlock.operations.addAll(b.operations);
                }
                for (var op : b.operations) {
                    if (op.inst == JUMP) {
                        var target = op.target;
                        op.target = GraphUtil.actualTarget(this, target.number);
                        blocksToTake.add(op.target.number);
                    }
                }
            }
        }
        var listBlocksToTake = new ArrayList<>(blocksToTake);
        Collections.sort(listBlocksToTake);

        var newBlocks = new ArrayList<Block>();
        for (var n : listBlocksToTake) {
            newBlocks.add(blocks.get(n));
        }

        var i = 0;
        for (var block : newBlocks) {
            block.number = i++;
        }
        this.blocks = newBlocks;
    }

    private boolean mustTake(Block b) {
        if (b.number == 0) {
            return true;
        }
        else {
            for (var otherBlock : blocks) {
                for (var op : otherBlock.operations) {
                    if (op.isJump() && op.target == b) {
                        return true;
                    }
                    else if (op.isSwitch() && op.blockTargets.contains(b) || op.target == b) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private TypeVariable typeVariableFor(String s) {
        switch (s) {
            case "I":
                return TypeVariable.of(Builtin.I);
            case "S":
                return TypeVariable.of(Builtin.S);
            case "C":
                return TypeVariable.of(Builtin.C);
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
                var internalName = CompilerUtil.internalName(CompilerUtil.extractDiscriptorInnards(s));
                return TypeVariable.of(ReferenceType.of(internalName));
        }
    }

    void resolveTopLevelElement(CodeElement element) {
        resolve(element, false);
    }

    void resolve(CodeElement element) {
        resolve(element, true);
    }

    void resolve(CodeElement element, boolean recursive) {
        if (element instanceof Literal) {
            var lit = (Literal) element;
            if (lit.value instanceof Integer) {
                var value = (Integer) lit.value;
                currentBlock().push(value);
            } else if (lit.value instanceof Double) {
                var value = (Double) lit.value;
                currentBlock().push(value);
            } else if (lit.value instanceof Float) {
                var value = (Float) lit.value;
                currentBlock().push(value);
            } else if (lit.value instanceof Long) {
                var value = (Long) lit.value;
                currentBlock().push(value);
            } else if (lit.value instanceof Byte) {
                var value = (Byte) lit.value;
                currentBlock().push(value);
            } else if (lit.value instanceof Short) {
                var value = (Short) lit.value;
                currentBlock().push(value);
            }
        } else if (element instanceof Cast) {
            var cast = (Cast) element;
            resolve(cast.expression);
            var opcodes = cast.op(typeInference.analyze(cast.expression, typeEnvironment));
            for (var opcode : opcodes) {
                currentBlock().operate(opcode);
            }
        } else if (element instanceof ReturnExpression) {
            var returnExpression = (ReturnExpression) element;
            Expression expression = returnExpression.expression;
            resolve(expression, true);
            var type = typeInference.analyze(expression, typeEnvironment);
            Builtin.from(returnType).ifPresent(returning -> {
                applyCast(type, returning);
            });
            currentBlock().addReturn(CompilerUtil.returnForType(returnType));
        } else if (element instanceof ReturnVoid) {
            currentBlock().addReturn(RETURN);
        } else if (element instanceof VariableRead) {
            var read = (VariableRead) element;
            try {
                currentBlock().readVar(getMatchingVars().get().indexByName(read.variable), descriptorForExpression(read));
            }
            catch (Exception e) {
                throw new IllegalStateException("Unable to read variable='" + read.variable + "'", e);
            }
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
        } else if (element instanceof FieldSet) {
            var set = (FieldSet) element;
            var fieldReference = set.fieldReference;
            var expression = set.expression;
            resolve(fieldReference.expression);
            resolve(expression);
            currentBlock().addOperation(
                    Operation.mkSetField(fieldReference.fieldName,
                            CompilerUtil.internalName(typeInference.analyze(fieldReference.expression, typeEnvironment)),
                            descriptorForExpression(expression)));
        } else if (element instanceof Assignment) {
            var assignment = (Assignment) element;
            resolve(assignment.expression);
            currentBlock().setVar(getMatchingVars().get().indexByName(assignment.variable), descriptorForExpression(assignment.expression));
        } else if (element instanceof Binary) {
            var operation = (Binary) element;
            switch (operation.operator) {
                case LESS_THAN:
                case LESS_THAN_OR_EQUALS:
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUALS:
                    var block = addBlock();
                    withBlock(block, () -> {
                        resolve(operation.left);
                        resolve(operation.right);
                        var eqBlock = addBlock().push(1);
                        var finalBlock = addBlock();

                        var analyzed = typeInference.analyze(operation.left, typeEnvironment).type();
                        // TODO: need specific test case for characters
                        if (analyzed == Builtin.I || analyzed == Builtin.C) {
                            block.jump(eqBlock, operation.asmOP(this))
                                    .push(0)
                                    .jump(finalBlock, GOTO);
                        }
                        else {
                            operation.comparisonOperation(this).ifPresent(block::operate);
                            block.jump(eqBlock, operation.asmOP(this))
                                    .push(0)
                                    .jump(finalBlock, GOTO);
                        }

                    });
                    return;
                case EQUALS:
                case NOT_EQUALS:
                    block = addBlock();
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
                case OR:
                    // TODO: this generates significantly more verbose bytecode than javac
                    var firstConditionBlock = addBlock();

                    withBlock(firstConditionBlock, () -> {
                        resolve(operation.left);
                    });
                    firstConditionBlock = addBlock();

                    var secondConditionBlock = addBlock();
                    withBlock(secondConditionBlock, () -> {
                        resolve(operation.right);
                    });
                    secondConditionBlock = addBlock();

                    var successBlock = addBlock();
                    successBlock.push(1);
                    var failureBlock = addBlock();
                    failureBlock.push(0);

                    var postBlock = addBlock();
                    successBlock.jump(postBlock, GOTO);

                    firstConditionBlock.jump(successBlock, IFNE);
                    secondConditionBlock.jump(failureBlock, IFEQ);

                    return;
                case AND:
                    firstConditionBlock = addBlock();

                    withBlock(firstConditionBlock, () -> {
                        resolve(operation.left);
                    });
                    firstConditionBlock = addBlock();

                    secondConditionBlock = addBlock();
                    withBlock(secondConditionBlock, () -> {
                        resolve(operation.right);
                    });
                    secondConditionBlock = addBlock();

                    successBlock = addBlock();
                    successBlock.push(1);
                    failureBlock = addBlock();
                    failureBlock.push(0);

                    postBlock = addBlock();
                    successBlock.jump(postBlock, GOTO);

                    firstConditionBlock.jump(failureBlock, IFEQ);
                    secondConditionBlock.jump(failureBlock, IFEQ);
                    return;
                default:
                    resolve(operation.left);
                    resolve(operation.right);
                    currentBlock().operate(operation.asmOP(this));
                    return;
            }
        } else if (element instanceof Unary) {
            var unary = (Unary) element;
            resolve(unary.expression);
            switch (unary.operator) {
                case NOT:
                    addBlock().push(1).operate(IXOR);
                    break;
                default:
                    currentBlock().operate(unary.operator.asmOP(typeInference.analyze(unary.expression, typeEnvironment)));
            }
        } else if (element instanceof Loop) {
            var loop = (Loop) element;
            var conditionsBlock = currentBlock.push(addBlock());
            currentLoop.push(conditionsBlock);
            if (loop.condition != null) {
                resolve(loop.condition);
            }
            var postConditionsBlock = addBlock();

            // This adds a block for the body of the loop
            resolveBody(loop);
            addBlock().jump(conditionsBlock, GOTO);

            var afterLoop = addBlock();
            if (loop.condition != null) {
                postConditionsBlock.jump(afterLoop, IFEQ);
            }
            currentLoop.pop();
            currentBlock.push(afterLoop);
            for (var x : this.blocks) {
                for (var op : x.operations) {
                    if (op.inst == JUMP && op.target == Block.POSTLOOP) {
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
            else if (call.isInterface) {
                currentBlock().callInterface(call.methodName, className, buildDescriptor(call));
            }
            else {
                currentBlock().call(call.methodName, className, buildDescriptor(call));
            }
            if (!recursive && producesValue(call)) {
                currentBlock().operate(POP);
            }
        }
        else if (element instanceof Constructor) {
            var constructor = (Constructor) element;
            List<String> argumentTypes = new ArrayList<>();
            currentBlock().addOperation(Operation.mkConstructor(CompilerUtil.internalName(constructor.returnType)));
            currentBlock().operate(DUP);

            for (var arg : constructor.arguments) {
                resolve(arg);
                argumentTypes.add(typeInference.analyze(arg, typeEnvironment).typeString());
            }
            var constructorDescriptor = CompilerUtil.descriptor(argumentTypes, VOID.typeString());
            currentBlock().call("<init>", CompilerUtil.internalName(constructor.returnType), constructorDescriptor, true);
        }
        else if (element instanceof Switch) {
            var s = (Switch) element;
            if (!s.isComplete()) {
                throw new IllegalStateException("Trying to compile an incomplete switch statement");
            }
            resolve(s.getExpression());
            if (s.getIntegerSwitch()) {
                var integerKeys = s.intCases();
                if (Switch.isDense(integerKeys)) {
                    var switchBlock = addBlock();
                    List<Block> switchBlocks = new ArrayList<>();
                    for (var key : integerKeys) {
                        var caseBlock = addBlock();
                        withBlock(caseBlock, () -> {
                            for (var subElement : s.getElements(key)) {
                                resolve(subElement);
                            }
                        });
                        switchBlocks.add(caseBlock);
                    }
                    var defaultBlock = addBlock();
                    withBlock(defaultBlock, () -> {
                        for (var codeElement : s.getDefaultCase()) {
                            resolve(codeElement);
                        }
                    });
                    int start = integerKeys.get(0);
                    int stop = integerKeys.get(integerKeys.size() - 1);
                    switchBlock.addOperation(Operation.mkTableSwitch(switchBlocks, defaultBlock, start, stop));
                    var postSwitchBlock = addBlock();
                    for (var b : switchBlocks) {
                        b.jump(postSwitchBlock, GOTO);
                    }
                }
                else {
                    throw new UnsupportedOperationException("TODO: Non-dense switches are not yet handled");
                }
            }
            else {
                throw new UnsupportedOperationException("TODO: String switches are not yet handled");
            }
        }
        else if (element instanceof Conditional) {
            var cond = (Conditional) element;
            currentBlock.push(addBlock());
            resolve(cond.condition);

            List<Block> visitedBlocks = new ArrayList<>();
            List<Block> booleanJumpBlocks = new ArrayList<>();
            booleanJumpBlocks.add(addBlock());

            resolveBody(cond);
            visitedBlocks.add(this.blocks.get(this.blocks.size() - 1));

            Block elseBlock = null;
            for (var alternate : cond.alternates) {
                if (alternate.condition != null) {
                    currentBlock.push(addBlock());
                    resolve(alternate.condition);
                    booleanJumpBlocks.add(addBlock());
                    resolveBody(alternate);
                    visitedBlocks.add(this.blocks.get(this.blocks.size() - 1));
                }
                else {
                    elseBlock = addBlock();
                    currentBlock.push(elseBlock);
                    resolveBody(alternate);
                }
            }

            var afterLoopBlock = addBlock();
            if (elseBlock == null) {
                elseBlock = afterLoopBlock;
            }

            for (var booleanJumpBlock : booleanJumpBlocks) {
                booleanJumpBlock.jump(elseBlock, IFEQ);
            }
            for (var visitedBlock : visitedBlocks) {
                visitedBlock.jump(afterLoopBlock, GOTO);
            }
            currentBlock.push(afterLoopBlock);
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
                    case S:
                        currentBlock().newArray(T_SHORT);
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
                    case S:
                        currentBlock().operate(SALOAD);
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
                    case S:
                        currentBlock().operate(SASTORE);
                }
            }
            else {
                currentBlock().operate(AASTORE);
            }
        } else if (element instanceof ArrayLength) {
            var arrayLength = (ArrayLength) element;
            resolve(arrayLength.expression);
            currentBlock().operate(ARRAYLENGTH);
        } else if (element instanceof NoOpStatement) {
            // do nothing
        }
    }

    private void applyCast(Type origin, Type target) {
        var type1 = target.type();
        var type2 = origin.type();
        if (!(type1 instanceof Builtin && type2 instanceof Builtin)) {
            return;
        }
        applyCastToBuiltins((Builtin) type2, (Builtin) type1);
    }

    private void applyCastToBuiltins(Builtin source, Builtin target) {
        if (!target.equals(source)) {
            var cast = source.cast(target);
            if (cast > 0) {
                currentBlock().addOperation(Operation.mkOperation(cast));
            }
        }
    }

    private void resolveBody(ElementContainer container) {
        withBlock(addBlock(), () -> {
            for (var codeElement : container.getBody()) {
                resolve(codeElement);
                if (producesValue(codeElement)) {
                    currentBlock().operate(POP);
                }
            }
        });
    }

    private ArrayType determineArrayType(Expression arrayRef) {
        var analyzedType = typeInference.analyze(arrayRef, typeEnvironment);
        if (analyzedType instanceof TypeVariable) {
            analyzedType = analyzedType.type();
        }
        if (!(analyzedType instanceof ArrayType)) {
            throw new TypeInference.TypeCheckException("Attempting to get array type from non-array expression");
        }
        var arrayType = (ArrayType) analyzedType;
        return arrayType;
    }

    private boolean producesValue(CodeElement codeElement) {
        if (codeElement instanceof Expression) {
            if (codeElement instanceof Call) {
                var call = (Call) codeElement;
                var returnType = call.returnType;
                return returnType != null && !returnType.type().equals(Void.VOID);
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
            if (StringUtils.isNotBlank(classPackage)) {
                return classPackage + "." + className;
            }
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
        List<Type> concreteTypes = new ArrayList<>();
        for (var i = initialIndex; i < call.arguments.length; i++) {
            concreteTypes.add(typeOf(call.arguments[i]));
        }
        var invocationTypes = determineInvocationTypes(call, concreteTypes);
        for (var type : invocationTypes) {
            sb.append(CompilerUtil.descriptor(type));
        }

        sb.append(')');
        sb.append(CompilerUtil.descriptor(call.returnType));
        return sb.toString();
    }

    private List<Type> determineInvocationTypes(Call call, List<Type> concreteTypes) {
        List<MethodSignature> methods = getMethodsForType(call);
        for (var method : methods) {
            boolean matches = true;
            for (var i = 0; i < concreteTypes.size(); i++) {
                var argumentType = method.argumentTypes.get(i);
                var concreteType = concreteTypes.get(i);
                if (!argumentType.equals(concreteType)) {
                    if (!assignable(argumentType, concreteType) || hasNumericCast(argumentType, concreteType)) {
                        matches = false;
                    }
                }
            }
            if (matches) {
                return method.argumentTypes;
            }
        }
        return concreteTypes;
    }

    private boolean hasNumericCast(Type argumentType, Type concreteType) {
        return argumentType instanceof Builtin && concreteType instanceof Builtin;
    }

    private boolean assignable(Type paramType, Type type) {
        try {
            Class<?> paramClass = Class.forName(paramType.typeString());
            Class<?> actualClass = Class.forName(type.typeString());
            return paramClass.isAssignableFrom(actualClass);
        }
        catch (Exception e) {
            return false;
        }
    }

    private List<MethodSignature> getMethodsForType(Call call) {
        Type receiverType;
        if (!call.isStatic) {
            receiverType = typeInference.analyze(call.receiver(), typeEnvironment);
        }
        else {
            receiverType = ReferenceType.of(call.className);
        }
        // YOLO: handle static vs. instance methods
        List<MethodSignature> methods = new ArrayList<>();
        var receiverClass = receiverType.type().typeString();
        try {
            var cls = Class.forName(CompilerUtil.internalNameToCanonicalName(receiverClass));
            for (var method : cls.getDeclaredMethods()) {
                if (method.getName().equals(call.methodName) && method.getParameterTypes().length == call.arguments.length - 1) {
                    var methodSignature = new MethodSignature();
                    for (var paramType : method.getParameterTypes()) {
                        methodSignature.argumentTypes.add(Type.of(paramType));
                    }
                    methods.add(methodSignature);
                }
            }
        }
        catch (Exception e) {
            // YOLO: ignore for now, future approach is to distinguish user-defined types from builtins
        }
        return methods;
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
        return CompilerUtil.effectiveDescriptorForType(type);
    }

    public void setClass(String className, String classPackage) {
        this.className = className;
        this.classPackage = classPackage;
        this.typeInference = new TypeInference(className, this);
    }
}
