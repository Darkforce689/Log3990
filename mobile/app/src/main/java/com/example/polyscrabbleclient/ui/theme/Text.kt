package com.example.polyscrabbleclient.ui.theme

import com.example.polyscrabbleclient.account.viewmodel.MILLI_TO_SECONDS
import com.example.polyscrabbleclient.account.viewmodel.SEC_IN_MIN


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
const val game_winner = "Partie gagnée"
const val game_loser = "Partie perdue"
const val game_forfeiter = "Partie abandonnée"

// Account
const val my_profil = "Mon profil"
const val my_account = "Mon compte"
const val save = "Sauvegarder"
const val avatars = "Avatar"
const val statistics = "Statistiques"
const val game_statistics = "Statistiques de jeux"
const val connection_history = "Historique de connexions"
const val my_statistics = "Mes statistiques"
const val gamePlayed = "Parties jouées"
const val gameWon = "Parties gagnées"
const val averagePointsPerGame = "Moyenne de points par partie"
const val averageTimePerGame = "Moyenne de temps par partie"
const val date = "Date"
const val connection_type = "Type de connexions"
const val my_games = "Mon historique de jeux"
const val game_history = "Historique des parties"
const val game_played = "Parties jouées"
const val game_result = "Status"

// Notifications
const val RemainderTitleNotificationFR = "Reviens jouer à PolyScrabble!"
val RemainderContentNotificationFR: (Int) -> String =
    { time -> "Déjà $time minutes depuis votre dernière connexion" }

// Errors
const val missing_field = "Veuillez remplir tous les champs."
const val invalid_password = "Mot de passe invalide."
const val invalid_email = "Aucun utilisateur n'a cet email."
const val wrong_form_email = "Veuillez saisir un e-mail valide."
val invalid_username_creation: (minLength: Int, maxLength: Int) -> String =
    { minLength, maxLength -> "Entrer un pseudonyme entre $minLength et $maxLength caractères" }
const val userName_not_unique = "Un compte utilise déjà ce pseudonyme"
const val email_not_unique = "Un compte utilise déjà cet e-mail."
val invalid_password_creation: (minLength: Int, maxLength: Int) -> String =
    { minLength, maxLength -> "Entrer un mot de passe entre $minLength et $maxLength caractères" }
const val already_auth = "Vous êtes déjà connecté sur un autre client"

// Abstract
const val Activated = "Activé"
const val Deactivated = "Désactivé"
const val Yes = "Oui"
const val No = "Non"
val formatTurnTime: (Long) -> String =
    { ms ->
        val absoluteSeconds = ms / MILLI_TO_SECONDS
        val minutes = absoluteSeconds / SEC_IN_MIN
        val seconds = absoluteSeconds - minutes * SEC_IN_MIN
        String.format("%01d:%02d", minutes, seconds)
    }

// GameScreen
val lettersRemainingFR: (count: Int) -> String = { count -> "$count lettres restantes" }
val lettersRemainingEN: (count: Int) -> String = { count -> "$count letters remaining" }
const val passButtonFR = "Passer"
const val placeButtonFR = "Placer"
const val exchangeButtonFR = "Échanger"
const val cancelButtonFR = "Annuler"
const val quitGameButtonFR = "Abandonner la partie"
const val quitButtonFR = "Abandonner"
const val leaveGameButtonFR = "Quitter"
const val chooseJokerFR = "Quelle sera cette tuile ?"
const val confirmButtonFR = "Confirmer"
const val endGameWinnerFR = "Félicitation!"
const val endGameLoserFR = "Dommage..."
const val winnersAreFR = "Les gagnants de la partie sont"
const val winnerIsFR = "Le gagnant de la partie est"
const val andFR = "et"
const val confirmQuitGameFR = "Voulez-vous abandonner la partie ?"
const val warningQuitGameFR = "Vous seriez alors déclaré perdant."
const val disconnectedFromServerFR = "Vous avez été déconnecté du serveur"
const val backToMainPageButtonFR = "Revenir au menu principal"

// GameCreation
const val new_game_creation = "Nouvelle Partie Multijoueurs"
const val time_per_turn = "Temps par tour"
const val random_bonus = "Bonus aléatoire"
const val private_game = "Partie privée"
const val protected_game = "Partie protégée"
const val password_text = "Mot de passe"
const val cancel = "Annuler"
const val create_game = "Créer la partie"
const val number_of_player = "Nombre de joueurs"
const val create_game_multiplayers = "Créer une partie multijoueurs"
const val join_game_multiplayers = "Joindre une partie multijoueurs"
const val watch_game_multiplayers = "Observer une partie multijoueurs"
const val new_game = "Nouvelle Partie"
const val choose_bot_difficulty = "Difficulté du joueur virtuel"
const val game_mode = "Mode de jeu"
const val classic = "Classique"
const val magic_cards = "Carte de pouvoirs"
const val game_settings = "Visibilité de la partie"
const val choose_game_settings = "Choisir les paramètres de la partie"
const val game_parameters = "Paramètres de jeu"
const val choose_game_parameters = "Choisir les paramètres de jeu"
const val choose_magic_card = "Choisir les cartes de pouvoirs"
const val select_all = "Toutes"

// Magic Card
const val exchange_a_letter_name = "Échanger une lettre"
const val split_points_name = "Vole des points"
const val place_random_bonus_name = "Place un bonus"
const val exchange_horse_name = "Échanger son chevalet"
const val exchange_horse_all_name = "Échanger tous les chevalets"
const val skip_next_turn_name = "Passe le prochain tour"
const val extra_turn_name = "Prend un tour supplémentaire"
const val reduce_timer_name = "Réduit le temps des autres joueurs"

// Lobby
const val joinGameButtonFR = "Rejoindre la partie"
const val joinGameButtonEN = "Join game"
const val launchGameButtonFR = "Démarrer la partie"
const val waitingForOtherPlayersFR = "En attente d'autres joueurs"
const val joinAGameFR = "Joindre une partie"
const val watchAGameFR = "Observer une partie"
const val hostQuitGameFR = "L'hôte a quitté la partie"
const val Ok = "Ok"
val pendingGameIdFR: (id: String?) -> String = { id -> "Id de la partie: ${id ?: ""}" }
const val players_in_game = "Joueurs dans la partie:"

// Public/Private/Observables
const val Private = "Privée"
const val Public = "Publique"
const val WAIT_STATUS = "En attente"
const val ACTIVE_STATUS = "En cours"
const val PendingGameSubTitle = "Partie en attente"
const val ObservableGameSubTitle = "Partie en cours (observable)"
const val NextPlayer = "Joueur suivant"
const val PreviousPlayer = "Joueur précédent"
const val CandidatePlayers = "Joueurs en attente d'approbation:"

// Message
const val create_convo_button = "Créer"
const val create_convo_title = "Nouvelle Conversation"
const val create_already_exist = "Cette conversation existe déjà"
const val create_name_forbiden = "Ce nom est interdit"

const val join_convo_button = "Rejoindre"
const val join_convo_title = "Joindre une conversation"
const val delete_menu_option = "Supprimer"
const val create_menu_option = "Créer"
const val join_menu_option = "Rejoindre"

const val delete_convo_title = "Supprimer des conversations"

const val no_conversation_to_delete = "Vous n'avez pas créé de conversations"

// Invitations

const val new_invite_title = "Nouvelle invitation"
const val accept_invite_button = "Accepter"
const val refuse_invite_button = "Refuser"
val new_invite_body: (userName: String) -> String =
    { userName: String -> "$userName veut vous inviter à sa partie" }

const val close_invite_user = "Fermer"
const val user_search = "Chercher un utilisateur"
const val invite_expired = "Votre invitation a expirée."
const val invite_invalid = "L'invitation est invalide."
const val invite_game_full = "La partie que vous essayez de rejoindre est pleine."
const val user_status_online = "En ligne"
const val user_status_offline = "Hors ligne"
