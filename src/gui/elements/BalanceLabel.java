package gui.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BalanceLabel extends Label {
    public BalanceLabel(int number) {
        this(number, false);
    }

    // Constructor with both parameters
    public BalanceLabel(int number, boolean isGrey) {
        super(); 

        setFont(Font.font("Montserrat", FontWeight.BOLD, 12));

        if (isGrey) {
            setTextFill(Color.GRAY);
        } else if (number > 0) {
            setTextFill(Color.BLUE);
        } else {
        	number = -number;
            setTextFill(Color.RED);
        }
        
        setAlignment(Pos.CENTER_RIGHT);
        setText(String.valueOf(number));
    }
}