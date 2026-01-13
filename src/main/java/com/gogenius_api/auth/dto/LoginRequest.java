// Fichier: LoginRequest.java
package com.gogenius_api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
  

	@Override
	public String toString() {
		return "LoginRequest [login=" + login + ", password=" + password + ", email=" + email + ", keepSession="
				+ keepSession + "]";
	}

	private String login;           //  login (pseudonyme)
    private String password;
    private String email;      // optionnel

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
    private String keepSession ;    // Utiliser Boolean (objet) au lieu de boolean (primitif)

    // Constructeurs
    public LoginRequest() {}

    public LoginRequest(String login, String password, String keepSession) {
        this.login = login;
        this.password = password;
        this.keepSession = keepSession; // Directement String, pas de conversion
    }
 // Validation : au moins un des deux (email OU login)
    public boolean isValid() {
        return (email != null && !email.trim().isEmpty()) || 
               (login != null && !login.trim().isEmpty());
    }
    // Getters et Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeepSession() {
        return keepSession;
    }

    public void setKeepSession(String keepSession) {
        this.keepSession = keepSession;
    }
    public boolean decodeKeepSession() {
        if (keepSession == null) {
            return false; // Valeur par défaut
        }
        
        // MD5 de "true" et "false"
        String md5True = "b326b5062b2f0e69046810717534cb09";   // "true" en MD5
        String md5False = "68934a3e9455fa72420237eb05902327";  // "false" en MD5
        
        if (keepSession.equals(md5True)) {
            return true;
        } else if (keepSession.equals(md5False)) {
            return false;
        } else {
            // MD5 invalide, on prend false par défaut
            System.out.println("⚠️ MD5 keepSession invalide: " + keepSession);
            return false;
        }
    }
}