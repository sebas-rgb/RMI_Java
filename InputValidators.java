import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Filtros de entrada reutilizables para restringir caracteres.
 */
public final class InputValidators {
    private InputValidators() {
    }

    public static DocumentFilter createAlphaNumericFilter() {
        return new RegexDocumentFilter("[a-zA-Z0-9]*");
    }

    public static DocumentFilter createLettersFilter() {
        return new RegexDocumentFilter("[a-zA-Z]*");
    }

    public static DocumentFilter createNumericFilter() {
        return new RegexDocumentFilter("[0-9]*");
    }

    private static class RegexDocumentFilter extends DocumentFilter {
        private final String regex;

        private RegexDocumentFilter(String regex) {
            this.regex = regex;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
            if (string != null && isValid(fb, offset, 0, string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
            if (text == null || isValid(fb, offset, length, text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(FilterBypass fb, int offset, int length, String text) throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder builder = new StringBuilder(currentText);
            builder.replace(offset, offset + length, text);
            return builder.toString().matches(regex);
        }
    }
}
