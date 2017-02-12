package com.maxaderiha.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.String;
import java.util.Stack;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.print("Enter expression: ");
            Scanner input = new Scanner(System.in);
            String expression = input.next();

            if (checkBrackets(expression)) {
                System.out.println("Error!!! Brackets placed incorrectly. Try it again.");
                main(null);
            }
            if (expression.equals("0")) {
                System.exit(0);
            }

            expression = replaceNegative(expression);
            List<String> tokens = getTokens(expression);
            expression = replaceNumbers(expression);

            Stack ss = new Stack(), fs = new Stack();
            int countNum = 0, countExp = 0;

            while (countExp < expression.length()) {
                char alt = expression.charAt(countExp);
                String sub = String.valueOf(expression.charAt(countExp));
                if (alt == ')') {
                    pushCloseBracket(ss, fs);
                }
                if (alt == '(') {
                    fs.push(String.valueOf(expression.charAt(countExp)));
                }
                if (getType(sub) != -1 && getType(sub) != -0 && getType(sub) != 3) {
                    pushArithmeticSign(ss, fs, expression, countExp);
                }
                if (sub.equals("0")) {
                    ss.push(tokens.get(countNum));
                    countNum++;
                }
                countExp++;
            }

            while (!fs.empty()) {
                ss.push(fs.peek());
                fs.pop();
            }

            fs = swapStack(ss);
            System.out.print("Answer: ");
            System.out.println(getResult(fs, ss));
            main(null);
        } catch (Exception nfe) {
            System.out.println("Error!!! Try it again");
            main(null);
        }
    }

    public static int getType(String simbol) {
        switch (simbol) {
            case "(":
                return 0;
            case "+":
                return 1;
            case "-":
                return 1;
            case "*":
                return 2;
            case "/":
                return 2;
            case ")":
                return 3;
            default:
                return -1;
        }
    }

    public static void pushCloseBracket(Stack ss, Stack fs) {
        String element = (String) fs.peek();//если если очередной элемент равен ")", то
        while (!element.equals("(") && !element.equals("")) {//выталкиваем из стека в выходную строку-стек все операциии до ближайшей "(" и удаляем "("
            ss.push(element);
            element = "";
            if (!fs.empty())
                fs.pop();
            if (!fs.empty())
                element = (String) fs.peek();
        }
        if (!fs.empty())
            fs.pop();
    }

    public static void pushArithmeticSign(Stack ss, Stack fs, String expression, int index) {
        String temp = String.valueOf(expression.charAt(index));
        if (fs.empty()) {
            fs.push(temp);
        } else {
            if (getType(temp) > getType((String) fs.peek())) {
                fs.push(temp);
            } else {
                String element = (String) fs.peek();
                while (!fs.empty() && !element.equals("(")) {
                    if (getType(temp) <= getType(element)) {
                        ss.push(element);
                    }
                    if (!element.equals("("))
                        fs.pop();
                    if (!fs.empty())
                        element = (String) fs.peek();
                }
                fs.push(temp);
            }
        }
    }

    public static Stack swapStack(Stack fs) {
        String temp;
        Stack result = new Stack();
        while (!fs.empty()) {
            temp = (String) fs.peek();
            result.push(temp);
            fs.pop();
        }
        return result;
    }

    public static double getResult(Stack ss, Stack fs) {
        double result, tmpOne, tmpTwo;
        while (!ss.empty()) {
            if (getType((String) ss.peek()) == -1) {
                fs.push(Double.valueOf((String) ss.peek()));
            }

            switch ((String) ss.peek()) {
                case "*": {
                    tmpOne = (double) fs.peek();
                    fs.pop();
                    tmpTwo = (double) fs.peek();
                    fs.pop();
                    fs.push(tmpOne * tmpTwo);
                    ss.pop();
                }
                break;
                case "-": {
                    tmpOne = (double) fs.peek();
                    fs.pop();
                    tmpTwo = (double) fs.peek();
                    fs.pop();
                    fs.push(tmpTwo - tmpOne);
                    ss.pop();
                }
                break;
                case "+": {
                    tmpOne = (double) fs.peek();
                    fs.pop();
                    tmpTwo = (double) fs.peek();
                    fs.pop();
                    fs.push(tmpOne + tmpTwo);
                    ss.pop();
                }
                break;
                case "/": {
                    try {
                        tmpOne = (double) fs.peek();
                        fs.pop();
                        tmpTwo = (double) fs.peek();
                        fs.pop();
                        ss.pop();
                        fs.push(tmpTwo / tmpOne);
                    } catch (ArithmeticException nfe) {
                        System.out.println("Error!!! Divide by Zero. Try it again.");
                        main(null);
                    }
                }
                break;
                default:
                    ss.pop();
                    break;
            }

        }
        result = (double) fs.peek();
        fs.pop();
        return result;
    }

    public static List<String> getTokens(String word) {
        List<String> doubleNum = new ArrayList<>();
        Pattern p = Pattern.compile("(\\(-)?\\d*\\.?\\d+\\)?");
        Matcher m = p.matcher(word);
        while (m.find()) {
            Pattern p1 = Pattern.compile("-?\\d*\\.?\\d+");
            Matcher m1 = p1.matcher(m.group());
            if (m1.find()) {
                doubleNum.add(m1.group());
            }
        }
        return doubleNum;
    }

    public static boolean checkBrackets(String str) {
        Stack st = new Stack();
        boolean result;
        for (int i = 0; i < str.length(); i++) {
            if (st.empty() && str.charAt(i) == ')') {
                result = true;
                return result;
            }
            if (str.charAt(i) == '(') {
                st.push(str.charAt(i));
            }
            if (str.charAt(i) == ')') {
                st.pop();
            }
        }
        if (st.empty()) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    public static String replaceNumbers(String exp) {
        return exp.replaceAll("(\\(-\\d*\\.?\\d+\\))|(\\d*\\.?\\d+)", "0");
    }

    public static String replaceNegative(String exp) {
        String replacement, token, sign, temp;
        while (true) {
            Pattern p = Pattern.compile("[*/]-\\d*\\.?\\d+");
            Matcher m = p.matcher(exp);
            if (m.find()) {
                token = m.group();
                sign = Character.toString(token.charAt(0));
                temp = "(" + token.substring(1) + ")";
                replacement = sign + temp;

            } else {
                break;
            }
            exp = exp.replaceFirst("[*/]-\\d*\\.?\\d+", replacement);
        }
        return exp;
    }
}
