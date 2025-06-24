import java.util.*;

public class Calculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // Scanner to read input from user
        System.out.println("Java Calculator is ready. Enter your expression or type 'quit' to exit.");

        try {
            while (true) {
                System.out.print("> ");
                String input = sc.nextLine().replaceAll("\\s+", ""); // Remove whitespace
                if (input.equalsIgnoreCase("quit")) break;

                try {
                    input = insertMultiplication(input);              // Handle implicit multiplication like 2(3)
                    if (!isBalanced(input)) throw new Exception("Unbalanced parentheses");
                    validateCharacters(input);                        // Ensure valid characters only

                    double result = evaluate(input);                  // Evaluate full expression
                    System.out.println("Result: " + result + "\n");

                } catch (ArithmeticException e) {
                    System.out.println("Math Error: " + e.getMessage() + "\n");
                } catch (Exception e) {
                    System.out.println("ERROR!\nError: " + e.getMessage() + "\n");
                }
            }
        } finally {
            sc.close(); // Always close the scanner
        }

        System.out.println("Calculator closed. Goodbye!");
    }

    // ✅ Inserts multiplication (*) where implicit multiplication is used (e.g., 2(3) → 2*(3))
    static String insertMultiplication(String expr) {
        return expr.replaceAll("(\\d)(\\()", "$1*(")
                   .replaceAll("(\\))(\\d)", ")*$2")
                   .replaceAll("(\\))(\\()", ")*(");
    }

    // ✅ Validates that only allowed characters are used
    static void validateCharacters(String expr) {
        if (!expr.matches("[0-9+\\-*/%^().]*"))
            throw new IllegalArgumentException("Invalid characters in expression.");
    }

    // ✅ Checks if parentheses are balanced
    static boolean isBalanced(String expr) {
        int count = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    // ✅ Evaluates a full expression, processing inner parentheses first
    static double evaluate(String expr) {
        while (expr.contains("(")) {
            int open = expr.lastIndexOf('(');
            int close = expr.indexOf(')', open);
            if (close == -1) throw new IllegalArgumentException("Mismatched parentheses");
            double sub = compute(expr.substring(open + 1, close)); // Evaluate inside ()
            expr = expr.substring(0, open) + sub + expr.substring(close + 1); // Replace with result
        }
        return compute(expr); // Final expression without parentheses
    }

    // ✅ Computes flat expression (without parentheses) respecting operator precedence
    static double compute(String expr) {
        List<Double> nums = new ArrayList<>();     // List of numbers
        List<Character> ops = new ArrayList<>();   // List of operators

        for (int i = 0; i < expr.length(); ) {
            char c = expr.charAt(i);

            // If character is an operator, add to ops
            if ("+-*/%^".indexOf(c) >= 0) {
                ops.add(c);
                i++;
            } else {
                // Extract number
                int j = i;
                if (c == '-') j++; // Handle negative number
                while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                nums.add(Double.parseDouble(expr.substring(i, j)));
                i = j;
            }
        }

        // Operator precedence: ^ > * / % > + -
        char[] precedence = {'^', '*', '/', '%', '+', '-'};
        for (char op : precedence) {
            for (int i = 0; i < ops.size(); ) {
                if (ops.get(i) == op) {
                    double a = nums.get(i), b = nums.get(i + 1);
                    double result = switch (op) {
                        case '+' -> add(a, b);
                        case '-' -> sub(a, b);
                        case '*' -> mul(a, b);
                        case '/' -> div(a, b);
                        case '%' -> mod(a, b);
                        case '^' -> pow(a, b);
                        default -> 0;
                    };
                    nums.set(i, result); // Replace with result
                    nums.remove(i + 1);  // Remove used number
                    ops.remove(i);       // Remove used operator
                } else {
                    i++; // Move to next operator
                }
            }
        }
        return nums.get(0); // Final result
    }

    // ➕ Addition
    static double add(double a, double b) { return a + b; }

    // ➖ Subtraction
    static double sub(double a, double b) { return a - b; }

    // ✖️ Multiplication
    static double mul(double a, double b) { return a * b; }

    // ➗ Division
    static double div(double a, double b) {
        if (b == 0) throw new ArithmeticException("Division by zero");
        return a / b;
    }

    // ➗ Modulo
    static double mod(double a, double b) { return a % b; }

    // 🧮 Power
    static double pow(double a, double b) { return Math.pow(a, b); }
}
