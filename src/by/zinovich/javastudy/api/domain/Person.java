package by.zinovich.javastudy.api.domain;

public class Person {

    private Integer personId;
    private String personNameFirst;
    private String personNameSecond;
    private String personLogin;
    private String personPassword;

    public Person(Integer personId, String personNameFirst, String personNameSecond, String personLogin, String personPassword) {
        this.personId = personId;
        this.personNameFirst = personNameFirst;
        this.personNameSecond = personNameSecond;
        this.personLogin = personLogin;
        this.personPassword = personPassword;
    }

    public Person(String personNameFirst, String personNameSecond, String personLogin) {
        this.personNameFirst = personNameFirst;
        this.personNameSecond = personNameSecond;
        this.personLogin = personLogin;
    }


    public Person() {
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonNameFirst() {
        return personNameFirst;
    }

    public void setPersonNameFirst(String personNameFirst) {
        this.personNameFirst = personNameFirst;
    }

    public String getPersonNameSecond() {
        return personNameSecond;
    }

    public void setPersonNameSecond(String personNameSecond) {
        this.personNameSecond = personNameSecond;
    }

    public String getPersonLogin() {
        return personLogin;
    }

    public void setPersonLogin(String personLogin) {
        this.personLogin = personLogin;
    }

    public String getPersonPassword() {
        return personPassword;
    }

    public void setPersonPassword(String personPassword) {
        this.personPassword = personPassword;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personId=" + personId +
                ", personNameFirst='" + personNameFirst + '\'' +
                ", personNameSecond='" + personNameSecond + '\'' +
                ", personLogin='" + personLogin + '\'' +
                ", personPassword='" + personPassword + '\'' +
                '}';
    }
}
//////////////////////////////////////////////