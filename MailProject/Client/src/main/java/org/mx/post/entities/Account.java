package org.mx.post.entities;

public class Account implements java.io.Serializable {
    String email;

    String password;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Account){
           return this.password.equals(((Account)obj).getPassword()) &&  this.email.equals(((Account)obj).getEmail());
        }
        return super.equals(obj);
    }
}
