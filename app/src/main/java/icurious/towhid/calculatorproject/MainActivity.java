package icurious.towhid.calculatorproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    DecimalFormat nf;
    TextView display;
    StringBuilder exp;
    boolean equalFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = (TextView) findViewById(R.id.display);

        nf = new DecimalFormat("#.########");
        display = (TextView) findViewById(R.id.display);
        exp = new StringBuilder();

        findViewById(R.id.delChar).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                exp.setLength(0);
                display.setText("");
                return true;
            }
        });
    }

    public void keyPressed(View view) {
        char btnKey = ((Button) view).getText().charAt(0);
        if (btnKey == '=') calculateExp();
        else if (btnKey == 'D' && exp.length() > 0) {
            exp.setLength(exp.length() - 1);
            refreshDisplay();
        } else if (btnKey == 'C') {
            exp.setLength(0);
            display.setText("");
        } else if (display.getLineCount() > 3) {
            exp.setLength(exp.length() - 1);
            refreshDisplay();
            Toast.makeText(MainActivity.this, "exp is way too large!", Toast.LENGTH_SHORT).show();
        } else if (btnKey == '+' || btnKey == '-' || btnKey == '*' || btnKey == '/')
            insertOperator(btnKey);
        else insertDigit(btnKey);
    }

    private void insertOperator(char op) {
        if (equalFlag) equalFlag = false;

        if (exp.length() == 0 && op == '-') exp.append(op);
        else if (exp.length() == 0) return;
        else {
            removeEmptyDot();
            if (isOperator()) exp.setLength(exp.length() - 1);
            if (exp.length() > 0) exp.append(op);
            putZeroBeforeDot();
        }
        refreshDisplay();
    }

    private void insertDigit(char digit) {
        if ((digit == '0' && exp.toString().equals("0")) || (digit == '.' && isThereAnyDot()))
            return;

        if (equalFlag) {
            equalFlag = false;
            display.setText("");
            exp.setLength(0);
        }
        exp.append(digit);
        refreshDisplay();

    }

    private void calculateExp() {
        cleanUpExpression();
        refreshDisplay();

        if (exp.length() < 3) return; //1+1 --- minimum 3 character

        Expression e = new ExpressionBuilder(this.exp.toString()).build();
        this.exp.setLength(0);
        this.exp.append(nf.format(e.evaluate()));
        refreshDisplay();
        equalFlag = true;
    }

    private boolean isOperator() {
        char lastChar = (exp.length() > 0) ? exp.charAt(exp.length() - 1) : ' ';
        return (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/');
    }

    private void removeEmptyDot() {
        if (exp.length() > 0 && exp.charAt(exp.length() - 1) == '.') {
            exp.setLength(exp.length() - 1);
        }
    }

    private void cleanUpExpression() {
        char lastChar = (exp.length() > 0) ? exp.charAt(exp.length() - 1) : ' ';
        while (lastChar == '.' || isOperator()) {
            exp.setLength(exp.length() - 1);
            lastChar = (exp.length() > 0) ? exp.charAt(exp.length() - 1) : ' ';
        }
    }

    private void refreshDisplay() {
        display.setText(exp);
        display.setTextSize(35);
        if (expLimit()) display.setTextSize(20);
    }

    private boolean expLimit() {
        float textWidth = display.getPaint().measureText(exp.toString());
        return (textWidth >= display.getMeasuredWidth());
    }

    private boolean isThereAnyDot() {
        // view isn't empty and last char isn't a DOT
        if (exp.length() == 0) return false;
        if (exp.charAt(exp.length() - 1) == '.') return true;

        // loop through char's to find an operator or DOT
        char c;
        for (int i = exp.length() - 1; i >= 0; i--) {
            c = exp.charAt(i);
            if (c == '.') return true;
            else if (c == '+' || c == '-' || c == '*' || c == '/') return false;
        }
        return false;
    }

    private void putZeroBeforeDot() {
        for (int i = exp.length() - 1; i >= 0; i--) {
            char c = (i > 0) ? exp.charAt(i - 1) : ' ';
            if ((i == 0 && exp.charAt(i) == '.') || (exp.charAt(i) == '.' && (c == '+' || c == '-' || c == '*' || c == '/')))
                exp.insert(i, '0');
        }
    }
}
