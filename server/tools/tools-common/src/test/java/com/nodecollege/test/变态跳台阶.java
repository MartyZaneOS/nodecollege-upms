package com.nodecollege.test;

import java.util.Scanner;

/**
 * @author LC
 * @date 2020/12/28 16:24
 */
public class 变态跳台阶 {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        // 从键盘接收数据

        // next方式接收字符串
        System.out.println("next方式接收：");
        // 判断是否还有输入
        if (scan.hasNext()) {
            String str1 = scan.next();
            System.out.println("输入的数据为：" + str1);
        }
        scan.close();
    }
}
