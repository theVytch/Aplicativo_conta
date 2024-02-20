package br.edu.utfpr.eduardomelentovytch.contas.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.text.DecimalFormat;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null && !text.toString().isEmpty()) {
            try {
                // Converter o texto para double
                double value = Double.parseDouble(text.toString());
                // Formatar o valor com duas casas decimais
                DecimalFormat df = new DecimalFormat("#,##0.00");
                String formattedValue = df.format(value);
                super.setText(formattedValue, type);
            } catch (NumberFormatException e) {
                super.setText(text, type);
            }
        } else {
            super.setText(text, type);
        }
    }
}
