package com.gogenius_api.Model.auth;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private String password;
    private String userType;        // MD5 de "tourist" ou "partner"
    private String  termsAccepted;  //en MD5
    private String company;         // Obligatoire si userType = "partner"

    // Constructeurs
    public RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, String login, String email,
                          String password, String userType, String  termsAccepted, String company) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.termsAccepted = termsAccepted;
        this.company = company;
    }

    // Getters et Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String  getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(String  termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
// ========== MÉTHODES DE DÉCODAGE MD5 ==========
    
    /**
     * Décode userType MD5 en valeur lisible
     * @return "tourist" ou "partner"
     * @throws IllegalArgumentException si MD5 invalide
     */
    public String getUserTypeDecoded() {
        if (userType == null) return null;
        
        // Valeurs MD5 attendues (à générer une fois)
        // MD5 de "tourist" = b3e7b2a8f1b9f5a8f1b9f5a8f1b9f5a8
        // MD5 de "partner" = a5f5c8f1b9f5a8f1b9f5a8f1b9f5a8f1
        String md5Tourist = "b3e7b2a8f1b9f5a8f1b9f5a8f1b9f5a8"; 
        String md5Partner = "a5f5c8f1b9f5a8f1b9f5a8f1b9f5a8f1";
        
        // Comparaison MD5
        if (userType.equals(md5Tourist)) {
            return "tourist";
        } else if (userType.equals(md5Partner)) {
            return "partner";
        }else {
        throw new IllegalArgumentException("userType MD5 invalide: " + userType);
    }}
    public boolean isTermsAcceptedDecoded() {
        if (termsAccepted == null) return false;
        
        // MD5 de "true" et "false"
        
        String md5True = "b326b5062b2f0e69046810717534cb09";  // "true" en MD5
        String md5False = "68934a3e9455fa72420237eb05902327"; // "false" en MD5
        
        return termsAccepted.equals(md5True);
    }
}