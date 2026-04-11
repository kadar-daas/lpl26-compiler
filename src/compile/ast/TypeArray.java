package compile.ast;

public class TypeArray extends Type {
    public final Type elementType;

    public TypeArray(Type elementType) {
        this.elementType = elementType;
    }
}