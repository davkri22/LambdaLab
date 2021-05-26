import java.util.ArrayList;

public abstract class Expression{
    public abstract Expression swap(Variable replace, Expression exp);

    public abstract void addVars(ArrayList<Variable> list);

    public abstract Expression deepCopy();
}

class Variable extends Expression{
    String name;

    public Variable(String name){
        this.name = name;
    }

    public Expression swap(Variable replace, Expression exp) {
        if (this.toString().equals(replace.toString())){
            this.name = exp.toString();
        }
        return this;
    }

    public void addVars(ArrayList<Variable> list){
        list.add(this);
    }

    public Variable deepCopy() {
        return new Variable(this.name);
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
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

    public Expression run(Expression exp){
        ArrayList<Variable> boundVars = new ArrayList<>();
        ArrayList<Variable> freeVars = new ArrayList<>();
        ArrayList<String> freeVarStrings = new ArrayList<>();
        boundVars.add(this.var);
        this.exp.addVars(boundVars);
        exp.addVars(freeVars);
        for (Variable var: freeVars) {
            freeVarStrings.add(var.toString());
        }
        for (Variable var: boundVars) {
            if(freeVarStrings.contains(var.toString()))
            var.name += "1";
        }
        this.exp = this.exp.swap(this.var, exp);
        return this.exp;
    }

    public Expression swap(Variable replace, Expression exp){
        if (this.exp.getClass() != Variable.class)
            this.exp = this.exp.swap(replace, exp);
        return this;
    }

    public void addVars(ArrayList<Variable> list){
        list.add(this.var);
        this.exp.addVars(list);
    }

    public Function deepCopy() {
        return new Function(this.var.deepCopy(), this.exp.deepCopy());
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
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

    public Expression swap(Variable replace, Expression exp){
        if (this.lExp.toString().equals(replace.toString())){
            lExp = exp;
        }
        if (this.rExp.toString().equals(replace.toString())){
            rExp = exp;
        }
        this.lExp = this.lExp.swap(replace, exp);
        this.rExp = this.rExp.swap(replace, exp);
        return this;
    }

    public void addVars(ArrayList<Variable> list){
        lExp.addVars(list);
        rExp.addVars(list);
    }

    public Application deepCopy() {
        return new Application(this.lExp.deepCopy(), this.rExp.deepCopy());
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    public String toString() {
        return "(" + lExp + " " + rExp + ")";
    }
}
