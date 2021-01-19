package com.nodecollege.test;

import java.util.Stack;

/**
 * 设计一个返回最小值的栈
 * 定义栈的数据结构，请在该类型中实现一个能够得到栈最小元素的min函数。
 */
public class MinStack {
    Stack<Integer> stack = new Stack<>();
    Stack<Integer> minStack = new Stack<>();

    public void push(int node) {
        if (stack.isEmpty()) {
            stack.push(node);
            minStack.push(node);
        } else if (node < minStack.peek()) {
            stack.push(node);
            minStack.push(node);
        } else {
            stack.push(node);
        }
    }

    public void pop() {
        if (stack.isEmpty()) return;
        if (stack.peek() == minStack.peek()) {
            stack.pop();
            minStack.pop();
        } else {
            stack.pop();
        }
    }

    public int top() {
        return stack.peek();
    }

    public int min() {
        if (minStack.isEmpty()) return 0;
        return minStack.peek();
    }

    public static void main(String[] args) {
        MinStack test = new MinStack();
        test.push(10);
        test.push(12);
        test.push(3);
        test.push(4);
        test.push(1);
        test.push(10);
        test.pop();
        System.out.println(test.min());
        test.pop();
        System.out.println(test.min());
        test.pop();
        System.out.println(test.min());
        test.pop();
        System.out.println(test.min());
    }
}