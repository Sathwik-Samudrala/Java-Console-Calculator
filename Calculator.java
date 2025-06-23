import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Input reader
        System.out.println("Enter a math expression (like 2+3*4) or type 'quit' to exit:");

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine().replaceAll("\\s+", ""); // Remove spaces

            if (userInput.equalsIgnoreCase("quit")) break; // Exit if "quit"

            try {
                double result = evaluateExpression(userInput); // Solve expression
                System.out.println("Result: " + result + "\n");
            } catch (Exception error) {
                System.out.println("Invalid input! Please try again.\n");
            }
        }

        scanner.close(); // Close input
        System.out.println("Calculator closed. Goodbye!");
    }

    // Evaluates a math expression by handling operator precedence
    static double evaluateExpression(String expression) {
        if (!expression.matches("[0-9+\\-*/.]+")) throw new RuntimeException(); // Valid characters check

        expression = calculateOperators(expression, '*', '/'); // First *, /
        expression = calculateOperators(expression, '+', '-'); // Then +, -

        return Double.parseDouble(expression); // Return final result
    }

    // Solves the first matching operator pair in expression and repeats
    static String calculateOperators(String expression, char operator1, char operator2) {
        for (int i = 1; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (currentChar == operator1 || currentChar == operator2) {
                // Find full left number
                int leftStart = i - 1;
                while (leftStart > 0 &&
                        (Character.isDigit(expression.charAt(leftStart - 1)) || expression.charAt(leftStart - 1) == '.')) {
                    leftStart--;
                }

                // Find full right number
                int rightEnd = i + 1;
                while (rightEnd < expression.length() &&
                        (Character.isDigit(expression.charAt(rightEnd)) || expression.charAt(rightEnd) == '.')) {
                    rightEnd++;
                }

                // Extract numbers and perform operation
                double leftNumber = Double.parseDouble(expression.substring(leftStart, i));
                double rightNumber = Double.parseDouble(expression.substring(i + 1, rightEnd));
                double result;

                if (currentChar == '+') result = leftNumber + rightNumber;
                else if (currentChar == '-') result = leftNumber - rightNumber;
                else if (currentChar == '*') result = leftNumber * rightNumber;
                else result = leftNumber / rightNumber;

                // Replace part of expression with result
                expression = expression.substring(0, leftStart) + result + expression.substring(rightEnd);

                return calculateOperators(expression, operator1, operator2); // Repeat for remaining
            }
        }
        return expression;
    }
}
