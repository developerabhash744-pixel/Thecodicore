package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, Java Application compiled with GitHub Actions!");
        App app = new App();
        System.out.println("Sum of 5 and 7 is: " + app.add(5, 7));
    }

    public int add(int a, int b) {
        return a + b;
    }
}
