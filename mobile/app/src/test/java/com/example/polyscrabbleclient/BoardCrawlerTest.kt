package com.example.polyscrabbleclient

import com.example.polyscrabbleclient.game.domain.BoardCrawler
import com.example.polyscrabbleclient.game.domain.PlaceLetterSetting
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.model.CenterIndex
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.Direction
import com.example.polyscrabbleclient.game.sources.PlacementSetting
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates
import org.junit.*
import org.junit.Assert.*

class BoardCrawlerTest {
    companion object {
        val crawler = BoardCrawler

        lateinit var coordinatesAndTiles:  List<Pair<Int, TileModel>>

        @BeforeClass
        @JvmStatic
        fun setup() {
            coordinatesAndTiles = listOf(
                Pair(3,  TileModel('A', 1)),
                Pair(4,  TileModel('B', 1)),
                Pair(5,  TileModel('C', 1)),
                Pair(6,  TileModel('D', 1)),
                Pair(7,  TileModel('E', 1)),
                Pair(CenterIndex,  TileModel('F', 1)),
                Pair(9,  TileModel('G', 1))
            )
        }

        @AfterClass
        @JvmStatic
        fun teardown() {}
    }

    lateinit var board: BoardModel

    @Before
    fun beforeTest() {
        board = BoardModel()
        crawler.reset(board)
    }

    @After
    fun afterTest() {
        board.removeTransientTilesContent()
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_NoTransientTileAdded_THEN_CannotPlaceLetter() {
        assertFalse(crawler.canPlaceLetter())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_OneTransientTileAdded_THEN_CanPlaceLetter() {
        board.setTransient(TileCoordinates(1,1), TileModel('X', 1))
        assertTrue(crawler.canPlaceLetter())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_NoTransientTileAdded_THEN_FormedWordThrowsError() {
        assertThrows(Exception::class.java) {
            crawler.getPlacement()
        }
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_OneTransientTileAddedNotOnCenterTile_THEN_FormedWordIsNull() {
        board.setTransient(TileCoordinates(1,1), TileModel('X', 1))
        assertNull(crawler.getPlacement())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_OneTransientTileAddedOnCenterTile_THEN_FormedWordIsLetter() {
        board.setTransient(TileCoordinates(CenterIndex, CenterIndex), TileModel('X', 1))
        assertNull(crawler.getPlacement())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_HorizontallyConsecutiveTransientTilesAddedTile_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board.setTransient(TileCoordinates(CenterIndex, coordinate), tile)
        }
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting( coordinatesAndTiles.first().first, CenterIndex, Direction.Horizontal),
            word = "ABCDEFG"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_HorizontallyScrambledConsecutiveTransientTilesAddedTile_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.shuffled().forEach { (coordinate, tile) ->
            board.setTransient(TileCoordinates(CenterIndex,coordinate), tile)
        }
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting( coordinatesAndTiles.first().first, CenterIndex, Direction.Horizontal),
            word = "ABCDEFG"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_VerticallyConsecutiveTransientTilesAddedTile_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board.setTransient(TileCoordinates(coordinate, CenterIndex), tile)
        }
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(CenterIndex, coordinatesAndTiles.first().first, Direction.Vertical),
            word = "ABCDEFG"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_EmptyBoard_WHEN_VerticallyScrambledConsecutiveTransientTilesAddedTile_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.shuffled().forEach { (coordinate, tile) ->
            board.setTransient(TileCoordinates(coordinate, CenterIndex), tile)
        }
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(CenterIndex, coordinatesAndTiles.first().first, Direction.Vertical),
            word = "ABCDEFG"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithHorizontalWord_WHEN_VerticallyConsecutiveTransientTilesAdded_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[coordinate, CenterIndex] = tile
        }
        board.setTransient(TileCoordinates(CenterIndex - 2, coordinatesAndTiles.first().first), TileModel('X', 1))
        board.setTransient(TileCoordinates(CenterIndex - 1, coordinatesAndTiles.first().first), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(coordinatesAndTiles.first().first, CenterIndex - 2, Direction.Vertical),
            word = "XYA"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithVerticalWord_WHEN_HorizontallyConsecutiveTransientTilesAdded_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[CenterIndex, coordinate] = tile
        }
        board.setTransient(TileCoordinates(coordinatesAndTiles.first().first, CenterIndex - 2), TileModel('X', 1))
        board.setTransient(TileCoordinates(coordinatesAndTiles.first().first, CenterIndex - 1), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting( CenterIndex - 2, coordinatesAndTiles.first().first, Direction.Horizontal),
            word = "XYA"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithHorizontalWord_WHEN_VerticallyTransientTileAdded_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[coordinate, CenterIndex] = tile
        }
        board.setTransient(TileCoordinates(CenterIndex - 1, coordinatesAndTiles.first().first), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(coordinatesAndTiles.first().first, CenterIndex - 1, Direction.Vertical),
            word = "YA"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithVerticalWord_WHEN_HorizontallyTransientTileAdded_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[CenterIndex, coordinate] = tile
        }
        board.setTransient(TileCoordinates(coordinatesAndTiles.first().first, CenterIndex - 1), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting( CenterIndex - 1, coordinatesAndTiles.first().first, Direction.Horizontal),
            word = "YA"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithHorizontalWord_WHEN_HorizontallyTransientTileAddedBefore_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[coordinate, CenterIndex] = tile
        }
        board.setTransient(TileCoordinates(CenterIndex, coordinatesAndTiles.first().first - 1), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(coordinatesAndTiles.first().first - 1, CenterIndex, Direction.Horizontal),
            word = "YABCDEFG"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithVerticalWord_WHEN_VerticallyTransientTileAddedBefore_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[CenterIndex, coordinate] = tile
        }
        board.setTransient(TileCoordinates(coordinatesAndTiles.first().first - 1, CenterIndex), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(CenterIndex, coordinatesAndTiles.first().first - 1,  Direction.Vertical),
            word = "YABCDEFG"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithHorizontalWord_WHEN_HorizontallyTransientTileAddedAfter_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[coordinate, CenterIndex] = tile
        }
        board.setTransient(TileCoordinates(CenterIndex, coordinatesAndTiles.last().first + 1), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(coordinatesAndTiles.first().first, CenterIndex, Direction.Horizontal),
            word = "ABCDEFGY"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithVerticalWord_WHEN_VerticallyTransientTileAddedAfter_THEN_FormedWordIsSumOfLetters() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[CenterIndex, coordinate] = tile
        }
        board.setTransient(TileCoordinates(coordinatesAndTiles.last().first + 1, CenterIndex), TileModel('Y', 1))
        val expected = PlaceLetterSetting(
            placementSetting = PlacementSetting(CenterIndex, coordinatesAndTiles.first().first,  Direction.Vertical),
            word = "ABCDEFGY"
        )
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithHorizontalWord_WHEN_TransientTileAddedDiagonallyBefore_THEN_FormedWordIsNull() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[coordinate, CenterIndex] = tile
        }
        board.setTransient(TileCoordinates(CenterIndex + 1, coordinatesAndTiles.first().first - 1), TileModel('Y', 1))
        val expected = null
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithVerticalWord_WHEN_TransientTileAddedDiagonallyBefore_THEN_FormedWordIsNull() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[CenterIndex, coordinate] = tile
        }
        board.setTransient(TileCoordinates(coordinatesAndTiles.first().first - 1, CenterIndex + 1), TileModel('Y', 1))
        val expected = null
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithHorizontalWord_WHEN_TransientTileAddedDiagonallyAfter_THEN_FormedWordIsNull() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[coordinate, CenterIndex] = tile
        }
        board.setTransient(TileCoordinates(CenterIndex + 1, coordinatesAndTiles.last().first + 1), TileModel('Y', 1))
        val expected = null
        assertEquals(expected, crawler.getPlacement())
    }

    @Test
    fun GIVEN_BoardWithVerticalWord_WHEN_TransientTileAddedDiagonallyAfter_THEN_FormedWordIsNull() {
        coordinatesAndTiles.forEach { (coordinate, tile) ->
            board[CenterIndex, coordinate] = tile
        }
        board.setTransient(TileCoordinates(coordinatesAndTiles.last().first + 1, CenterIndex + 1), TileModel('Y', 1))
        val expected = null
        assertEquals(expected, crawler.getPlacement())
    }
}
