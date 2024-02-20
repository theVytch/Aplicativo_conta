package br.edu.utfpr.eduardomelentovytch.contas.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsDateMaskWatcher implements TextWatcher {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final EditText editText;
    private String previousText = "";

    public UtilsDateMaskWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        previousText = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String currentText = s.toString();
        if (!currentText.equals(previousText)) {
            String formattedDate = formatString(currentText);
            editText.removeTextChangedListener(this);
            editText.setText(formattedDate);
            editText.setSelection(formattedDate.length());
            editText.addTextChangedListener(this);
        }
    }

    private String formatString(String input) {
        // Format your input string here according to your desired date format
        // Example: DD/MM/YYYY
        // Here, we add "/" after the 2nd and 5th characters
        StringBuilder formatted = new StringBuilder();
        int maxLength = Math.min(input.length(), 10); // Limit to 8 characters (DD/MM/YYYY)
        for (int i = 0; i < maxLength; i++) {
            char c = input.charAt(i);
            if ((i == 2 || i == 5) && c != '/') {
                formatted.append('/');
            }
            formatted.append(c);
        }
        return formatted.toString();
    }
}
