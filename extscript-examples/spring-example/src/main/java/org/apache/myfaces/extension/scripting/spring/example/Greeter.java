package org.apache.myfaces.extension.scripting.spring.example;

/**
 *
 */
public class Greeter {

    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getGreeting() {
        return "Salut " + person.getName() + "!";
    }

    public String getPersonalGreeting() {
       return "Hi " + person.getFirstName() + "!";
    }

    public String getAnotherGreeting() {
        return "Hi World";
    }

}
