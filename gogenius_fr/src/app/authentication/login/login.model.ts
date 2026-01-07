export interface Login {
  login: string;        // login/pseudoName ou adresse mail de l'utilisateur
  password: string;     // Le mot de passe crypté de l'utilisateur
  keepSession: string;  // Indique si l'utilisateur souhaite garder sa session active (true/false en MD5)
}