package org.mx.post.center;

import org.mx.post.entities.Account;

import java.util.ArrayList;

public class AccountVault implements java.io.Serializable{
    private ArrayList<Account> accounts;
    AccountVault() {
        accounts = new ArrayList<Account>();
        accounts.add(new Account("hiitsmax@post.com", "1234"));
    }
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public Account getAccountByEmail(String email){
        for (Account account : accounts) {
            if (account.getEmail().equals(email)) {
                return account;
            }
        }
        return null;
    }
}
