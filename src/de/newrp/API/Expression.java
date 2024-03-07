package de.newrp.API;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Expression {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.###", DecimalFormatSymbols.getInstance(Locale.GERMAN));
    private static final String[] TO_REPLACE = new String[]{"PI", "E", "ANS"};
    private static final String[] REPLACER = new String[]{String.valueOf(Math.PI), String.valueOf(Math.E), "0"};

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(5);
    }

    private double result = Double.NaN;
    private String expression;

    private int pos = -1;
    private int ch;

    public Expression(String expression) {
        this.expression = expression;
    }

    public double evaluate() {
        replaceVariables();
        nextChar();

        double x = parseExpression();
        if (pos < expression.length()) {
            throw new ExpressionException("Unerwartetes Zeichen: " + (char) ch);
        }

        this.result = x;
        return x;
    }

    private void replaceVariables() {
        expression = expression.replaceAll("PI", String.valueOf(Math.PI))
                .replaceAll("E", String.valueOf(Math.E))
                .replaceAll("ANS", "0"); // Hier müsste die tatsächliche Logik für "ANS" stehen, wenn es eine solche gibt
    }

    private double parseExpression() {
        double x = parseTerm();
        while (true) {
            if (eat('+')) x += parseTerm();
            else if (eat('-')) x -= parseTerm();
            else return x;
        }
    }

    private double parseTerm() {
        double x = parseFactor();
        while (true) {
            if (eat('*')) x *= parseFactor();
            else if (eat('/')) x /= parseFactor();
            else return x;
        }
    }

    private double parseFactor() {
        if (eat('+')) return parseFactor();
        if (eat('-')) return -parseFactor();

        double x;
        int startPos = this.pos;
        if (eat('(')) {
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(expression.substring(startPos, this.pos));
        } else {
            char wrongChar;
            if (ch == -1) {
                wrongChar = expression.charAt(expression.length() - 1);
            } else {
                wrongChar = (char) ch;
            }

            throw new ExpressionException("Unexpected character: " + wrongChar);
        }

        if (eat('^')) {
            double exponent = parseFactor();
            x = Math.pow(x, exponent);
        }

        return x;
    }

    private void nextChar() {
        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    public static class ExpressionException extends ArithmeticException {

        ExpressionException(String message) {
            super(message);
        }
    }
}
