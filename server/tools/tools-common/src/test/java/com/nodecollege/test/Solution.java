package com.nodecollege.test;

import java.util.*;


class TreeNode {
  int val = 0;
  TreeNode left = null;
  TreeNode right = null;
  public TreeNode(int val){
      this.val = val;
  }
}


public class Solution {
    private static ArrayList<Integer> first = new ArrayList();
    private static ArrayList<Integer> middle = new ArrayList();
    private static ArrayList<Integer> then = new ArrayList();
    /**
     * 
     * @param root TreeNode类 the root of binary tree
     * @return int整型二维数组
     */
    public int[][] threeOrders (TreeNode root) {
        // 二叉树 分为根节点、左节点、右节点，
        // 深度遍历分为 先序 中序 后序三种遍历方式，目的是将二叉树这种复杂结构，转换成一维数组，便于计算机处理。
        // 首先记住这三种遍历方式起名是根据根节点的记录顺序来的。记住一个前提顺序，左右节点一定先左后右
        // write code here
        int[][] orders = new int[3][];
        firstOrder(root);
        orders[0] = toIntArray(first);
        middleOrder(root);
        orders[1] = toIntArray(middle);
        thenOrder(root);
        orders[2] = toIntArray(then);
        return orders;
    }
    private static final int[] toIntArray(ArrayList<Integer> list) {
        int[] intArray = list.stream().mapToInt(Integer::intValue).toArray();
        return intArray;
    }
    // 先序遍历，记录根节点的值在记录左右子节点的值
    private static void firstOrder(TreeNode root) {
        if (root != null) {
            first.add(root.val);
            firstOrder(root.left);
            firstOrder(root.right);
        }
    }
    // 中序遍历，先记录左节点的值，在记录根节点的值，最后右节点
    private static void middleOrder(TreeNode root) {
        if (root != null) {
            middleOrder(root.left);
            middle.add(root.val);
            middleOrder(root.right);
        }
    }
    // 后序遍历，先记录左右子节点的值，在记录跟节点的值
    private static void thenOrder(TreeNode root) {
        if (root != null) {
            thenOrder(root.left);
            thenOrder(root.right);
            then.add(root.val);
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        TreeNode root = new TreeNode(14);
        TreeNode left1 = new TreeNode(24);
        TreeNode right1 = new TreeNode(22);
        root.left = left1;
        root.right = right1;
        int res[][] = solution.threeOrders(root);
        for (int i = 0 ; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                System.out.print(res[i][j] + ", ");
            }
            System.out.println();
        }
    }
}