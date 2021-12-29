package com.justinblank.classcompiler;

import com.justinblank.classcompiler.lang.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class Method {

    public final String methodName;
    private TypeInference typeInference;
    private Map<String, TypeVariable> typeEnvironment = new HashMap<>();
    private String className;
    final int modifiers;
    final List<String> arguments;
    final List<Block> blocks;
    final String returnType;
    private final Vars matchingVars;
    private final Map<String, Object> attributes = new HashMap<>();
    private List<CodeElement> elements = new ArrayList<>();

    private Stack<Block> currentBlock = new Stack<>();
    private Stack<Block> currentLoop = new Stack<>();

    public Method(String methodName, List<String> arguments, String returnType, Vars matchingVars) {
        this(methodName, arguments, returnType, matchingVars, ACC_PUBLIC);
    }

    Method(String methodName, List<String> arguments, String returnType, Vars matchingVars, int modifiers) {
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

    public Method returnValue(Expression expression) {
        this.elements.add(CodeElement.returnValue(expression));
        return this;
    }

    public Method call(String methodName, Expression... expressions) {
        this.elements.add(CodeElement.call(methodName, expressions));
        return this;
    }

    void resolve() {
        this.addBlock();
        for (var element : elements) {
            typeInference.analyze(element, typeEnvironment);
            resolve(element);
        }
        this.elements = new ArrayList<>();
    }

    void resolve(CodeElement element) {
        if (element instanceof Literal) {
            var lit = (Literal) element;
            var value = (Integer) lit.value;
            currentBlock().push(value);
        } else if (element instanceof ReturnExpression) {
            var returnExpression = (ReturnExpression) element;
            resolve(returnExpression.expression);
            currentBlock().addReturn(returnForType(returnExpression.expression));
        } else if (element instanceof VariableRead) {
            var read = (VariableRead) element;
            currentBlock().readVar(getMatchingVars().get().indexByName(read.variable), "I");
        } else if (element instanceof Assignment) {
            var assignment = (Assignment) element;
            resolve(assignment.expression);
            currentBlock().setVar(getMatchingVars().get().indexByName(assignment.variable), descriptorForExpression(assignment.expression));
        } else if (element instanceof Binary) {
            var operation = (Binary) element;
            switch (operation.operator) {
                case EQUALS:
                    var block = addBlock();
                    withBlock(block, () -> {
                        resolve(operation.left);
                        resolve(operation.right);
                        var eqBlock = addBlock().push(1);
                        var finalBlock = addBlock();

                        block.jump(eqBlock, operation.asmOP())
                                .push(0)
                                .jump(finalBlock, GOTO);
                    });

                    return;
                default:
                    resolve(operation.left);
                    resolve(operation.right);
                    currentBlock().operate(operation.asmOP());
                    return;
            }
        } else if (element instanceof Loop) {
            var loop = (Loop) element;
            var conditionsBlock = currentBlock.push(addBlock());
            currentLoop.push(conditionsBlock);
            resolve(loop.condition);
            var block = addBlock();

            withBlock(addBlock(), () -> {
                for (var codeElement : loop.body) {
                    resolve(codeElement);
                }
            });

            addBlock().jump(conditionsBlock, GOTO);
            var afterLoop = addBlock();
            block.jump(afterLoop, IFEQ);
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
            var className = getClassName(call.receiver());
            currentBlock().call(call.methodName, className, buildDescriptor(call));
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
    }

    private void withBlock(Block body, Runnable r) {
        currentBlock.push(body);
        r.run();
        currentBlock.pop();
    }

    // TODO relocate?
    public static Type typeOf(Expression expression) {
        return Builtin.I;
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
                    return resolved.toString();
                }
                throw new UnsupportedOperationException("TODO");
            }
        }
    }


    private String buildDescriptor(Call call) {
        var sb = new StringBuilder();
        sb.append('(');
        for (var i = 0; i < call.arguments.length - 1; i++) {
            var type = typeOf(call.arguments[i]);
            sb.append(CompilerUtil.descriptor(type));
        }
        sb.append(')');
        sb.append('I');
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

    private int returnForType(Expression expression) {
        return IRETURN;
    }

    private String descriptorForExpression(Expression expression) {
        return "I";
    }

    public void add(CodeElement element) {
        elements.add(element);
    }

    public void setClassName(String className) {
        this.className = className;
        this.typeInference = new TypeInference(className);
    }
}
