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
const val gamePage = "Page de jeu Scrabble"
const val lobbyPage = "Créer/Rejoindre une partie"
const val no_Account = "Vous n'avez pas de compte?"

// Errors
const val missing_field = "Veuillez remplir tous les champs."
const val invalid_password = "Mot de passe invalide."
const val invalid_email = "Aucun utilisateur n'a cet email."
const val wrong_form_email = "Veuillez saisir un e-mail valide."
class Invalid_username_creation(minLength : Int, maxLength: Int){ val message = "Entrer un pseudonyme entre $minLength et $maxLength caractères"}
const val userName_not_unique = "Un compte utilise déjà ce pseudonyme"
const val email_not_unique = "Un compte utilise déjà cet e-mail."
class Invalid_password_creation(minLength : Int, maxLength: Int){ val message = "Entrer un mot de passe entre $minLength et $maxLength caractères"}
const val already_auth = "Vous êtes déjà connecté sur un autre client"

// GameScreen
val lettersRemainingFR: (count: Int) -> String = { count -> "$count lettres restantes"}
val lettersRemainingEN: (count: Int) -> String = { count -> "$count letters remaining"}

