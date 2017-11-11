package cc.pchospital.app.util;


public class Card {
    String label;
    int number;

    public Card(String label, int number){
        this.label = label;
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public int getNumber() {
        return number;
    }
}
