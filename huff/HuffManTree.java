package huff;

import binaryTree.Basic_Tree;

public class HuffManTree<T> extends Basic_Tree<T> {

    public HuffManTree(float key, T value) {
        super(key, value);
    }

    public HuffManTree(float key, Basic_Tree<?> left, Basic_Tree<?> right, Basic_Tree<?> parent) {
        super(key, left, right, parent);
    }

    public HuffManTree(float key, Basic_Tree<?> left, Basic_Tree<?> right) {
        super(key, left, right);
    }

    public HuffManTree(float key) {
        super(key);
    }

    String repreBits(){
        if (parent==null)
            throw new RuntimeException("there is no parent to the code {self.parent} {self}");
        return parent(this,"");
    }

    String repreByte(){
        String bits=repreBits();
        return "00000000".substring(bits.length())+bits;
    }
        

    private static String parent(Basic_Tree<?> node,String orgByte){
        if (node.parent==null)
            return orgByte;
        if (node.parent.left==node)
            return parent(node.parent,"1"+orgByte);
        if (node.parent.right==node)
            return parent(node.parent,"0"+orgByte);
        throw new RuntimeException("WRONGED TREE");  
    }
        

}
