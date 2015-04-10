package model;

/**
 * Created by usuario on 07/04/2015.
 */
public class Encuesta {
    private String  Question ;
    private int Id;
    //private ArrayList<String> genre;

    public Encuesta() {
    }

    public Encuesta(String Question, int Id) {
        this.Question = Question;
        this.Id= Id;
    }
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String Nombre) {
        this.Question = Nombre;
    }



}
