import java.util.*;

public class Calculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("""
╔═════════════════════════════════════════════════════════╗
║               🔢 Java Console Calculator                ║
║                                                         ║
║ Supported operations:                                   ║
║   ➤ Addition:           5 + 3                           ║
║   ➤ Subtraction:        8 - 2                           ║
║   ➤ Multiplication:     4 * 6 or 2(5) (implicit)        ║
║   ➤ Division:           9 / 3                           ║
║   ➤ Modulo:             10 % 3                          ║
║   ➤ Power:              2 ^ 3                           ║
║   ➤ Square root:        sqrt(25)                        ║
║   ➤ Log base 10:        log(1000)                       ║
║                                                         ║
║ Type 'quit' to exit.                                    ║
╚═════════════════════════════════════════════════════════╝
""");

        while (true) {
            System.out.print("> ");
            String input = sc.nextLine().replaceAll("\\s", "");
            if (input.equalsIgnoreCase("quit")) break;

            input = insertImplicitMultiplication(input);
            input = evaluateFunctions(input);

            if (!isBalanced(input)) {
                System.out.println("Invalid input: Unbalanced parentheses\n");
                continue;
            }

            String error = getValidationError(input);
            if (error != null) {
                System.out.println("Invalid input: " + error + "\n");
                continue;
            }

            try {
                double result = evaluate(input);
                System.out.println("Result: " + result + "\n");
            } catch (ArithmeticException ae) {
                System.out.println("Math Error: " + ae.getMessage() + "\n");
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input: Improper number format\n");
            } catch (Exception e) {
                System.out.println("Invalid input\n");
            }
        }
        sc.close();
    }

    static String insertImplicitMultiplication(String expr) {
        return expr.replaceAll("(\\d)(\\()", "$1*(")
                    .replaceAll("(\\))(\\d)", ")*$2")
                    .replaceAll("(\\))(\\()", ")*(");
    }

    static String evaluateFunctions(String expr) {
        expr = evalFunc(expr, "sqrt", Math::sqrt);
        expr = evalFunc(expr, "log", Math::log10);
        return expr;
    }

    static String evalFunc(String expr, String name, java.util.function.DoubleUnaryOperator op) {
        while (expr.contains(name + "(")) {
            int start = expr.indexOf(name + "(");
            int l = start + name.length() + 1;
            int r = matchParen(expr, l - 1);
            double val = evaluate(expr.substring(l, r));
            expr = expr.substring(0, start) + op.applyAsDouble(val) + expr.substring(r + 1);
        }
        return expr;
    }

    static int matchParen(String expr, int open) {
        int count = 1;
        for (int i = open + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') count++;
            else if (expr.charAt(i) == ')') count--;
            if (count == 0) return i;
        }
        throw new IllegalArgumentException("Unmatched parentheses");
    }

    static boolean isBalanced(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    static String getValidationError(String s) {
        if (!s.matches("[0-9+\\-*/^%().]*")) return "Invalid characters.";
        if ("*/^%".indexOf(s.charAt(0)) != -1) return "Cannot start with operator '" + s.charAt(0) + "'";
        if ("+\\-*/^%".indexOf(s.charAt(s.length() - 1)) != -1) return "Cannot end with operator.";
        for (int i = 1; i < s.length(); i++) {
            char prev = s.charAt(i - 1), curr = s.charAt(i);
            if (isOp(prev) && isOp(curr) && !(curr == '-' && prev != ')'))
                return "Double operators: '" + prev + curr + "'";
        }
        return null;
    }

    static boolean isOp(char c) { return "+-*/^%".indexOf(c) != -1; }

    static double evaluate(String expr) {
        while (expr.contains("(")) {
            int l = expr.lastIndexOf('(');
            int r = expr.indexOf(')', l);
            double val = evaluateFlat(expr.substring(l + 1, r));
            expr = expr.substring(0, l) + val + expr.substring(r + 1);
        }
        return evaluateFlat(expr);
    }

    static double evaluateFlat(String s) {
        s = compute(s, '^');
        s = compute(s, '*', '/', '%');
        s = compute(s, '+', '-');
        return Double.parseDouble(s);
    }

    static String compute(String s, char... ops) {
        for (int i = s.length() - 2; i >= 0; i--) {
            char c = s.charAt(i);
            for (char op : ops) {
                if (c == op) {
                    int l = findLeft(s, i);
                    int r = findRight(s, i);
                    String leftStr = s.substring(l, i);
                    String rightStr = s.substring(i + 1, r);

                    if (leftStr.isEmpty() || rightStr.isEmpty()) return s;

                    double a = Double.parseDouble(leftStr);
                    double b = Double.parseDouble(rightStr);
                    double res = switch (op) {
                        case '+' -> add(a, b);
                        case '-' -> sub(a, b);
                        case '*' -> mul(a, b);
                        case '/' -> div(a, b);
                        case '^' -> pow(a, b);
                        case '%' -> mod(a, b);
                        default -> throw new IllegalArgumentException("Invalid op");
                    };
                    s = s.substring(0, l) + res + s.substring(r);
                    return compute(s, ops);
                }
            }
        }
        return s;
    }

    static int findLeft(String s, int i) {
        int j = i - 1;
        while (j > 0 && "0123456789.".indexOf(s.charAt(j - 1)) != -1) j--;
        if (j > 0 && s.charAt(j - 1) == '-' && (j - 2 < 0 || isOp(s.charAt(j - 2)))) j--;
        if (j == 0 && s.charAt(0) == '-') j = 0;
        return j >= 0 ? j : 0;
    }

    static int findRight(String s, int i) {
        int j = i + 1;
        while (j < s.length() && "0123456789.-".indexOf(s.charAt(j)) != -1) j++;
        return j;
    }

    static double add(double a, double b) { return a + b; }
    static double sub(double a, double b) { return a - b; }
    static double mul(double a, double b) { return a * b; }
    static double div(double a, double b) {
        if (b == 0) throw new ArithmeticException("Divide by zero");
        return a / b;
    }
    static double pow(double a, double b) { return Math.pow(a, b); }
    static double mod(double a, double b) {
        if (b == 0) throw new ArithmeticException("Modulo by zero");
        return a % b;
    }
}
