package binaryTree;

import java.util.Arrays;

public class BST<T> extends Basic_Tree<T> {
    public BST(Basic_Tree<T> obj) {
        super(obj.key, obj.left, obj.right, obj.parent);
        super.setValue(obj.value);
    }

    public BST(int key, T value) {
        super(key, value);
    }

    public BST(int key, Basic_Tree<?> left, Basic_Tree<?> right, Basic_Tree<?> parent) {
        super(key, left, right, parent);
    }

    public BST(int key, Basic_Tree<?> left, Basic_Tree<?> right) {
        super(key, left, right);
    }

    public BST(int key) {
        super(key);
    }

    static int size(Basic_Tree<?> obj) {
        if (obj == null)
            return 0;
        return 1 + BST.size(obj.left) + BST.size(obj.right);
    }

    int size() {
        return size(this);
    }
    Object traverse_in_order(){
        return traverse_in_order(this);
    }
    static Object traverse_in_order(Basic_Tree<?> obj) {
        if (obj == null)
            return null;
        return  Arrays.asList(BST.traverse_in_order(obj.left),obj.key,BST.traverse_in_order(obj.right));
    }

}
