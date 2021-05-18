public abstract class Expression {
    public abstract void swap(Variable replace, Expression exp);
}

class Variable extends Expression{
    String name;

    public Variable(String name){
        this.name = name;
    }

    public String toString(){
        return this.name;
    }

    public void swap(Variable replace, Expression exp) {
        if (exp.getClass() == Variable.class){
            this.name = ((Variable) exp).name;
        }
    }
}

class Function extends Expression{
    Variable var;
    Expression exp;

    public Function(Variable var, Expression exp){
        this.var = var;
        this.exp = exp;
    }

    public Expression run(Expression exp){
        if (this.exp.toString().equals(this.var.toString())){
            this.exp = exp;
        }
        else{
            this.exp.swap(this.var, exp);
        }
        return this.exp;
    }

    public void swap(Variable replace, Expression exp){
        this.exp.swap(replace, exp);
    }

    public String toString() {
        return "(λ" + var.toString() + "." + exp.toString() + ")";
    }
}

class Application extends Expression {
    Expression lExp;
    Expression rExp;

    public Application(Expression lExp, Expression rExp){
        this.lExp = lExp;
        this.rExp = rExp;
    }

    public void swap(Variable replace, Expression exp){
        if (this.lExp.toString().equals(replace.toString())){
            lExp = exp;
        }
        else if (this.rExp.toString().equals(replace.toString())){
            rExp = exp;
        }
        else {
            this.lExp.swap(replace, exp);
            this.rExp.swap(replace, exp);
        }
    }

    public String toString() {
        return "(" + lExp + " " + rExp + ")";
    }
}
