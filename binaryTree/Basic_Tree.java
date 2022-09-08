package binaryTree;


import java.util.Arrays;

public class Basic_Tree<T> implements Comparable<Basic_Tree<?>> {
    public final float key;
    public Basic_Tree<?> left;
    public Basic_Tree<?> right;
    public Basic_Tree<?> parent;
    T value;

    public T getValue() {
        return value;
    }
    @Override
    public int compareTo(Basic_Tree<?> o) {
        // usually toString should not be used,
        // instead one of the attributes or more in a comparator chain
        float diff=this.key-o.key;
        if (diff==0){
            return 0;
        }else if(diff>0){
            return 1;
        }
        return -1;
    }
    public void setValue(T value) {
        this.value = value;
    }

    public Basic_Tree(float key, T value) {
        this.key = key;
        this.value = value;
    }

    public Basic_Tree(float key, Basic_Tree<?> left, Basic_Tree<?> right, Basic_Tree<?> parent) {
        this.key = key;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    public Basic_Tree(float key, Basic_Tree<?> left, Basic_Tree<?> right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }

    public Basic_Tree(float key) {
        this.key = key;
    }

    public Object to_tuple() {
        return to_tuple(this);
    }

    public Object to_tuple(Basic_Tree<?> object) {
        if (object.left != null && object.right != null)
            return object.key;
        return Arrays.asList(to_tuple(object.left), this.key, to_tuple(object.right));
    }
    public float diff(Basic_Tree<?> obj1,Basic_Tree<?> obj2){
        if(obj1.key>obj2.key)
            return 1;
        if(obj1.key<obj2.key)
            return -1;
        return 0;
    }
    @Override
    public String toString() {
        return "BinaryTree <" + this.to_tuple() + ">";

    }
}
