package model;

/**
 * Created by usuario on 23/03/2015.
 */
public class User {

    private int  Id;
    private String Nombre,Password;

    public User() {
    }

    public User( int Id, String Nombre, String Password) {
        this.Id= Id;
        this.Nombre = Nombre;
        this.Password = Password;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
}
