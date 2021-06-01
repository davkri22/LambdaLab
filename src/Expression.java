import java.util.ArrayList;

public abstract class Expression{
    public abstract Expression swap(Variable replace, Expression exp);

    public abstract void addBound(ArrayList<Variable> list);

    public abstract void addFree(ArrayList<Variable> list);

    public abstract void swapVars(ArrayList<Variable> replace, ArrayList<Variable> swap);

    public abstract Expression deepCopy();
}

class Variable extends Expression{
    String name;
    int conversions = 0;

    public Variable(String name){
        this.name = name;
    }

    public Expression swap(Variable replace, Expression exp) {
        if (this.equals(replace)){
            this.name = exp.toString();
        }
        return this;
    }

    public void addBound(ArrayList<Variable> list){
        if (list.contains(this))
        list.add(this);
    }

    public void addFree(ArrayList<Variable> list){
        list.add(this);
    }

    public Variable deepCopy() {
        return new Variable(this.name);
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    public void swapVars(ArrayList<Variable> replace, ArrayList<Variable> set){
        if (this.equals(replace.get(0))) {
            this.name = set.get(0).name;
            replace.remove(0);
            set.remove(0);
        }

    }

    public void alpha(){
        if (conversions++ == 0)
            name += conversions;
        else
            name = name.substring(0, name.length()-1) + conversions;
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
        this.addBound(boundVars);
        exp.addFree(freeVars);
        for (Variable var: boundVars) {
            if(freeVars.contains(var))
            var.alpha();
        }
        this.exp = this.exp.swap(this.var, exp);
        return this.exp;
    }

    public Expression swap(Variable replace, Expression exp){
        if (this.exp.equals(replace)){
            this.exp = exp;
        }
        this.exp = this.exp.swap(replace, exp);
        return this;
    }

    public void addBound(ArrayList<Variable> list){
        list.add(this.var);
        this.exp.addBound(list);
    }

    public void addFree(ArrayList<Variable> list){
        list.add(this.var);
        this.exp.addBound(list);
    }

    public Function deepCopy() {
        return new Function(this.var.deepCopy(), this.exp.deepCopy());
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Function.class)
            return false;
        ArrayList<Variable> oldVars = new ArrayList<>();
        ArrayList<Variable> newVars = new ArrayList<>();
        Function func = ((Function) o).deepCopy();
        this.var.addFree(newVars);
        this.exp.addFree(newVars);
        func.var.addFree(oldVars);
        func.exp.addFree(oldVars);
        func.swapVars(oldVars, newVars);
        return this.toString().equals(func.toString());
    }

    public void swapVars(ArrayList<Variable> replace, ArrayList<Variable> set){
        if (this.var.equals(replace.get(0))) {
            this.var = set.get(0);
            replace.remove(0);
            set.remove(0);
        }
        this.exp.swapVars(replace, set);
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
        if (this.lExp.equals(replace)){
            lExp = exp;
        }
        if (this.rExp.equals(replace)){
            rExp = exp;
        }
        this.lExp = this.lExp.swap(replace, exp);
        this.rExp = this.rExp.swap(replace, exp);
        return this;
    }

    public void addBound(ArrayList<Variable> list){
        lExp.addBound(list);
        rExp.addBound(list);
    }

    public void addFree(ArrayList<Variable> list){
        lExp.addFree(list);
        rExp.addFree(list);
    }

    public Application deepCopy() {
        return new Application(this.lExp.deepCopy(), this.rExp.deepCopy());
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Application.class)
            return false;
        ArrayList<Variable> oldVars = new ArrayList<>();
        ArrayList<Variable> newVars = new ArrayList<>();
        Application app = ((Application) o).deepCopy();
        this.lExp.addFree(newVars);
        this.rExp.addFree(newVars);
        app.lExp.addFree(oldVars);
        app.rExp.addFree(oldVars);
        app.swapVars(oldVars, newVars);
        return this.toString().equals(app.toString());
    }

    public void swapVars(ArrayList<Variable> replace, ArrayList<Variable> set){
        if (this.lExp.equals(replace.get(0))) {
            this.lExp = set.get(0);
            replace.remove(0);
            set.remove(0);
        }
        if (this.rExp.equals(replace.get(0))) {
            this.rExp = set.get(0);
            replace.remove(0);
            set.remove(0);
        }
        this.lExp.swapVars(replace, set);
        this.rExp.swapVars(replace, set);
    }

    public String toString() {
        return "(" + lExp + " " + rExp + ")";
    }
}
