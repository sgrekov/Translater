package com.grekov.translate.presentation;

public class Calculator {
    public float calculate(float first, float second, Operator operator) {
        float result;
        switch (operator) {
            case PLUS:
                result = add(first, second);
                break;
            case MINUS:
                result = subtract(first, second);
                break;
            case MULTIPLY:
                result = multiply(first, second);
                break;
            case DIVIDE:
                result = divide(first, second);
                break;
            case MODULO:
                result = modulo(first, second);
                break;
            default:
                result = 0;
        }
        return result;
    }

    private float add(float first, float second) {
        return first + second;
    }

    private float subtract(float first, float second) {
        return first - second;
    }

    private float multiply(float first, float second) {
        return first * second;
    }

    private float divide(float first, float second) {
        return first / second;
    }

    private float modulo(float first, float second) {
        return first % second;
    }
}
