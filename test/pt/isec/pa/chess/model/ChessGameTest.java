package pt.isec.pa.chess.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.pieces.*;

class ChessGameTest {

    private ChessGame chessGame;
    private Board board;

    @BeforeEach
    void setUp() {
        chessGame = new ChessGame();
        board = new Board();
    }

    // Test 1: Verify initial board setup
    @Test
    void testInitialBoardSetup() {
        // Check pawn positions
        for (int i = 0; i < 8; i++) {
            assertNotNull(board.getPiece(1, i));
            assertEquals(Board.type.PAWN, board.getPiece(1, i).getType());
            assertFalse(board.getPiece(1, i).getColor());

            assertNotNull(board.getPiece(6, i));
            assertEquals(Board.type.PAWN, board.getPiece(6, i).getType());
            assertTrue(board.getPiece(6, i).getColor());
        }

        // Check black pieces
        assertNotNull(board.getPiece(0, 0));
        assertEquals(Board.type.ROOK, board.getPiece(0, 0).getType());
        assertNotNull(board.getPiece(0, 7));
        assertEquals(Board.type.ROOK, board.getPiece(0, 7).getType());
        assertNotNull(board.getPiece(0, 4));
        assertEquals(Board.type.KING, board.getPiece(0, 4).getType());

        // Check white pieces
        assertNotNull(board.getPiece(7, 0));
        assertEquals(Board.type.ROOK, board.getPiece(7, 0).getType());
        assertNotNull(board.getPiece(7, 7));
        assertEquals(Board.type.ROOK, board.getPiece(7, 7).getType());
        assertNotNull(board.getPiece(7, 4));
        assertEquals(Board.type.KING, board.getPiece(7, 4).getType());
    }

    // Test 2: Verify valid pawn moves
    @Test
    void testPawnMovement() {
        // Test white pawn initial two-square move
        assertEquals(ChessGame.acontecimentoMovimento.NORMAL,
                chessGame.movePiece(6, 0, 4, 0));

        // Test black pawn initial two-square move
        assertEquals(ChessGame.acontecimentoMovimento.NORMAL,
                chessGame.movePiece(1, 0, 3, 0));

        // Test invalid pawn move (backwards)
        assertEquals(ChessGame.acontecimentoMovimento.FALHIDO,
                chessGame.movePiece(4, 0, 5, 0));

        // Test invalid pawn move (diagonal without capture)
        assertEquals(ChessGame.acontecimentoMovimento.FALHIDO,
                chessGame.movePiece(4, 0, 3, 1));
    }

    // Test 3: Verify piece capture logic
    @Test
    void testPieceCapture() {
        // Setup a capture scenario
        chessGame.movePiece(6, 4, 4, 4); // White pawn
        chessGame.movePiece(1, 3, 3, 3); // Black pawn
        chessGame.movePiece(4, 4, 3, 3); // White captures black

        // Verify capture was successful
        assertEquals(ChessGame.acontecimentoMovimento.NORMAL,
                chessGame.movePiece(6, 3, 4, 3));
        assertEquals(ChessGame.acontecimentoMovimento.NORMAL,
                chessGame.movePiece(1, 4, 3, 4));
        assertEquals(ChessGame.acontecimentoMovimento.NORMAL,
                chessGame.movePiece(4, 3, 3, 4)); // Black captures white
    }

    // Test 4: Verify check detection
    @Test
    void testCheckDetection() {
        // Setup a check scenario
        chessGame.movePiece(6, 4, 4, 4); // White pawn
        chessGame.movePiece(1, 4, 3, 4); // Black pawn
        chessGame.movePiece(7, 3, 3, 7); // White queen to threaten black king

        // Verify check is detected
        assertTrue(chessGame.KingIsInCheck(false));
        assertFalse(chessGame.KingIsInCheck(true));
    }

    // Test 5: Verify pawn promotion
    @Test
    void testPawnPromotion() {
        // Setup promotion scenario
        chessGame.movePiece(6, 0, 0, 0); // White pawn to promotion square

        // Verify promotion state is triggered
        assertEquals(ChessGame.acontecimentoMovimento.PROMOTION,
                chessGame.movePiece(6, 0, 0, 0));

        // Verify promotion works
        assertTrue(chessGame.changePawnPromotion(true, 0)); // Promote to queen
        assertNotNull(board.getPiece(0, 0));
        assertEquals(Board.type.QUEEN, board.getPiece(0, 0).getType());
    }

    // Test 6: Verify castling logic
    @Test
    void testCastling() {
        // Clear path for castling
        chessGame.movePiece(7, 1, 5, 2); // White knight
        chessGame.movePiece(7, 2, 5, 3); // White bishop
        chessGame.movePiece(7, 3, 5, 4); // White queen

        // Verify kingside castling
        assertEquals(ChessGame.acontecimentoMovimento.NORMAL,
                chessGame.movePiece(7, 4, 7, 6)); // King moves to castling position

        // Verify new positions
        Piece king = board.getPiece(7, 6);
        Piece rook = board.getPiece(7, 5);
        assertNotNull(king);
        assertNotNull(rook);
        assertEquals(Board.type.KING, king.getType());
        assertEquals(Board.type.ROOK, rook.getType());
    }
}
