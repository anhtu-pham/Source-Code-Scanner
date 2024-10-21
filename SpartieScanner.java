import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpartieScanner {
    private String source;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("return", TokenType.RETURN);
        keywords.put("var", TokenType.VAR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("null", TokenType.NULL);
    }

    public SpartieScanner(String source) {
        this.source = source;
    }

    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        Token token = null;

        while (!isAtEnd(current) && (token = getNextToken()) != null) {
            if (token.type != TokenType.IGNORE) tokens.add(token);
        }

        return tokens;
    }

    private Token getNextToken() {

        skipSeparators();

        Token token = null;
        // Try to get each type of token, starting with a simple token, and getting a little more complex
        token = getSingleCharacterToken();
        if (token == null) token = getComparisonToken();
        if (token == null) token = getDivideOrComment();
        if (token == null) token = getStringToken();
        if (token == null) token = getNumericToken();
        if (token == null) token = getIdentifierOrReservedWord();
        if (token == null) {
            error(line, String.format("Unexpected character '%c' at %d ", source.charAt(current), current));
        }
        // System.out.println(current);
        return token;
    }

    private void skipSeparators() {
        while (source.charAt(current) == ' ' || source.charAt(current) == '\r') {
            current++;
        }
        boolean isNextLine = false;
        while (source.charAt(current) == '\n') {
            isNextLine = true;
            current++;
        }
        if (isNextLine) line++;
    }

    // TODO: Complete implementation
    private Token getSingleCharacterToken() {
        // Hint: Examine the character, if you can get a token, return it, otherwise return null
        // Hint: Be careful with the divide, we have not known if it is a single character

        char nextCharacter = source.charAt(current);

        // Hint: Start of not knowing what the token is, if we can determine it, return it, otherwise, return null
        TokenType type = TokenType.UNDEFINED;

        return null;
    }

    // TODO: Complete implementation
    private Token getComparisonToken() {
        // Hint: Examine the character for a comparison but check the next character (as long as one is available)
        // For example: < or <=
        char nextCharacter = source.charAt(current);
        String text = null;
        switch (nextCharacter) {
            case '<':
                boolean leq = examine('=');
                text = leq ? "<=" : "<";
                current += text.length();
                return new Token(leq ? TokenType.LESS_EQUAL : TokenType.LESS_THAN, text, line);
            case '>':
                boolean geq = examine('=');
                text = geq ? ">=" : ">";
                current += text.length();
                return new Token(geq ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN, text, line);
            case '=':
                if (examine('=')) {
                    text = "==";
                    current += text.length();
                    return new Token(TokenType.EQUIVALENT, text, line);
                }
            case '!':
                if (examine('=')) {
                    text = "!=";
                    current += text.length();
                    return new Token(TokenType.NOT_EQUAL, text, line);
                }
        }
        return null;
    }

    // TODO: Complete implementation
    private Token getDivideOrComment() {
        // Hint: Examine the character for a comparison but check the next character (as long as one is available)
        char nextCharacter = source.charAt(current);
        if (nextCharacter == '/') {
            boolean isComment = examine('/');
            String text = isComment ? "//" : "/";
            current += text.length();
            return new Token(isComment ? TokenType.IGNORE : TokenType.DIVIDE, text, line);
        }
        return null;
    }

    // TODO: Complete implementation
    private Token getStringToken() {
        // Hint: Check if you have a double quote, then keep reading until you hit another double quote
        // But, if you do not hit another double quote, you should report an error

        String string = null;
        char nextCharacter = source.charAt(current);
        if (nextCharacter == '\"') {
            StringBuilder builder = new StringBuilder();
            boolean found = false;
            while (!isAtEnd(current + 1) && !examine('\n')) {
                if (examine('\"')) {
                    found = true;
                    break;
                } else {
                    char next = source.charAt(++current);
                    builder.append(next);
                }
            }
            if (found) {
                current += 2;
                string = builder.toString();
                return new Token(TokenType.STRING, string, line);
            }
        }
        return null;
    }

    // TODO: Complete implementation
    private Token getNumericToken() {
        // Hint: Follow similar idea of String, but in this case if it is a digit
        // You should only allow one period in your scanner
        return null;
    }

    // TODO: Complete implementation
    private Token getIdentifierOrReservedWord() {
        // Hint: Assume first it is an identifier and once you capture it, then check if it is a reserved word.
        return null;
    }
    
    // Helper Methods
    private boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

    private boolean isAlpha(char character) {
        return character >= 'a' && character <= 'z' ||
                character >= 'A' && character <= 'Z';
    }

    // This will check if a character is what you expect, if so, it will advance
    // Useful for checking <= or //
    private boolean examine(char expected) {
        if (isAtEnd(current + 1)) return false;
        if (source.charAt(current + 1) != expected) return false;

        // Otherwise, it matches it, so advance
        return true;
    }

    private boolean isAtEnd(int index) {
        return index >= source.length();
    }

    // Error handling
    private void error(int line, String message) {
        System.err.printf("Error occurred on line %d : %s\n", line, message);
        System.exit(ErrorCode.INTERPRET_ERROR);
    }
}
