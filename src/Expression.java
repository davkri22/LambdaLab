public abstract class Expression {
}

class Variable extends Expression{
    String name;

    public Variable(String name){
        this.name = name;
    }

    public String toString(){
        return this.name;
    }
}

class Function extends Expression{
    Variable var;
    Expression exp;

    public Function(Variable var, Expression exp){
        this.var = var;
        this.exp = exp;
    }
}

class Application extends Expression{
    Expression lExp;
    Expression rExp;

    public Application(Expression lExp, Expression rExp){
        this.lExp = lExp;
        this.rExp = rExp;
    }

    public String toString() {
        return "(" + lExp + " " + rExp + ")";
    }
}
