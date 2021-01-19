package com.nodecollege.test;

import java.util.Arrays;

/**
 * @author LC
 * @date 2020/12/2 16:33
 */
public class SolutionKP {
    public static void main(String[] args) {
        quickSort(new int[]{39,28,55,87,66,3,17,39,21,311,232,32,11,2,3,66,7,8,55,55,2,3});
    }

    public static void quickSort(int[] arr){
        quickSort(arr,0,arr.length-1);
        System.out.println(Arrays.toString(arr));
    }
    public static void quickSort(int[] arr,int left,int right){
        int middle;
        if(left < right){
            middle = partition(arr,left,right);
            quickSort(arr,left,middle-1);
            quickSort(arr,middle+1,right);
        }
    }

    public static int partition(int[] arr,int left,int right){
        int piovt = arr[left];
        while (left < right) {
            while (left < right && arr[right] >= piovt) {
                right--;
            }
            arr[left] = arr[right];
            while (left < right && arr[left] <= piovt) {
                left++;
            }
            arr[right] = arr[left];
        }
        arr[left] = piovt;
        return left;
    }
}
