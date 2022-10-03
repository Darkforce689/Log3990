package com.example.polyscrabbleclient.ui.theme

// Authentication System
const val email_string = "Courriel"
const val userName_string = "Pseudonyme"
const val password_string = "Mot de passe"
const val create_account = "Créer un compte"
const val connection = "Connexion"
const val connect = "Se connecter"
const val signUp = "S'inscrire"
const val disconnection = "Déconnexion"
const val no_Account = "Vous n'avez pas de compte?"

// Errors
const val missing_field = "Veuillez remplir tous les champs."
const val invalid_password = "Mot de passe invalide."
const val invalid_email = "Aucun utilisateur n'a cet email."
const val wrong_form_email = "Veuillez saisir un e-mail valide."
class Invalid_username_creation(minPasswordLength : Int, maxPasswordLength: Int){ val message = "Veuillez saisir un pseudonyme entre $minPasswordLength et $maxPasswordLength caractères"}
const val userName_not_unique = "Un compte utilise déjà ce pseudonyme"
const val email_not_unique = "Un compte utilise déjà cet e-mail."
class Invalid_password_creation(minPasswordLength : Int, maxPasswordLength: Int){ val message = "Veuillez saisir un mot de passe entre $minPasswordLength et $maxPasswordLength caractères"}
const val already_auth = "Vous êtes déjà connecté sur un autre client"
