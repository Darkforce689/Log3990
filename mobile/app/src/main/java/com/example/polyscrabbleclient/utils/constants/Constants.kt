package com.example.polyscrabbleclient.utils.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.polyscrabbleclient.ui.theme.*

const val MAX_NAME_LENGTH = 30
const val MIN_NAME_LENGTH = 3
const val MIN_PASSWORD_LENGTH = 5
const val MAX_PASSWORD_LENGTH = 32

const val NoAvatar = "avatardefault"
const val BotAvatar = NoAvatar // TODO CHANGE ?
val botNames = listOf(
    "Jimmy",
    "Sasha",
    "BeepBoop",
    "Martin",
    "Wayne",
    "Fabian",
    "Juan",
    "Oliver",
    "Maria",
    "Wilson",
    "Laura",
    "Noah",
    "James",
    "Benjamin",
    "Lucas",
    "Henry",
    "Alexander",
    "Mason",
    "Michael",
    "Ethan",
    "Daniel",
    "Jacob",
    "Terminator",
    "Skynet",
    "Stockfish",
    "AlphaZero",
    "DeepBlue",
    "Mario",
    "Spooky",
    "Sonic",
    "Lynne",
    "Optix",
    "Machina",
)

val BOT_AVATARS = listOf(
    "easybotavatar1",
    "easybotavatar2",
    "easybotavatar3",
    "easybotavatar4",
    "easybotavatar5",
    "hardbotavatar1",
    "hardbotavatar2",
    "hardbotavatar3",
    "hardbotavatar4",
    "hardbotavatar5",
)

const val WIN_EXP_BONUS = 100;
const val LOSS_EXP_BONUS = 50;
const val EXP_PER_LEVEL = 10;
const val MAX_LEVEL = 10;
const val MAX_LEVEL_EXP = 1000;

// Magic Cards Ids
const val exchange_a_letter_id = "MC_EXCHANGE_LETTER"
const val split_points_id = "MC_SPLIT_POINTS"
const val place_random_bonus_id = "MC_PLACE_RANDOM_BONUS"
const val exchange_horse_id = "MC_EXCHANGE_HORSE"
const val exchange_horse_all_id = "MC_EXCHANGE_HORSE_ALL"
const val skip_next_turn_id = "MC_SKIP_NEXT_TURN"
const val extra_turn_id = "MC_EXTRA_TURN"
const val reduce_timer_id = "MC_REDUCE_TIMER"

val magic_card_map: Map<String, String> = mapOf(
    exchange_a_letter_id to exchange_a_letter_name,
    split_points_id to split_points_name,
    place_random_bonus_id to place_random_bonus_name,
    exchange_horse_id to exchange_horse_name,
    exchange_horse_all_id to exchange_horse_all_name,
    skip_next_turn_id to skip_next_turn_name,
    extra_turn_id to extra_turn_name,
    reduce_timer_id to reduce_timer_name
)

val magic_card_icon: Map<String, ImageVector> = mapOf(
    exchange_a_letter_id to Icons.Filled.FontDownload,
    split_points_id to Icons.Filled.OpenWith,
    place_random_bonus_id to Icons.Filled.AddBox,
    exchange_horse_id to Icons.Filled.SwapHorizontalCircle,
    exchange_horse_all_id to Icons.Filled.Shuffle,
    skip_next_turn_id to Icons.Filled.HideSource,
    extra_turn_id to Icons.Filled.PlusOne,
    reduce_timer_id to Icons.Filled.Timelapse
)

fun getName(id: String): String {
    return magic_card_map.getOrDefault(id, "")
}

fun getIcon(id: String): ImageVector? {
    return magic_card_icon[id]
}
