package cc.pchospital.app.util;


public class Card {
    private String ticketID;
    private String ticketDate;
    private String ticketLocation;
    private String ticketNote;
    private String ticketStates;

    public Card(int id, String date, String location, String note, String states){
        ticketID = Integer.toString(id);
        ticketDate = date;
        ticketLocation = location;
        ticketNote = note;
        ticketStates = states;
    }

    String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    String getTicketLocation() {
        return ticketLocation;
    }

    public void setTicketLocation(String ticketLocation) {
        this.ticketLocation = ticketLocation;
    }

    String getTicketNote() {
        return ticketNote;
    }

    public void setTicketNote(String ticketNote) {
        this.ticketNote = ticketNote;
    }

    String getTicketStates() {
        return ticketStates;
    }

    public void setTicketStates(String ticketStates) {
        this.ticketStates = ticketStates;
    }
}
