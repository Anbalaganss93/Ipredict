package com.ipredictfantasy.dto;

/**
 * Created by Android on 24-Sep-16.
 */
public class WinnerAnalistModel {
    private String question;
    private String useranswer;
    private String correct_answer;
    private String opponent_useranswer;
    private String usercolor;
    private String opponentcolor;
    private String username;
    private String opponentname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpponentname() {
        return opponentname;
    }

    public void setOpponentname(String opponentname) {
        this.opponentname = opponentname;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUseranswer() {
        return useranswer;
    }

    public void setUseranswer(String useranswer) {
        this.useranswer = useranswer;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getOpponent_useranswer() {
        return opponent_useranswer;
    }

    public void setOpponent_useranswer(String opponent_useranswer) {
        this.opponent_useranswer = opponent_useranswer;
    }

    public String getUsercolor() {
        return usercolor;
    }

    public void setUsercolor(String usercolor) {
        this.usercolor = usercolor;
    }

    public String getOpponentcolor() {
        return opponentcolor;
    }

    public void setOpponentcolor(String opponentcolor) {
        this.opponentcolor = opponentcolor;
    }
}
