package pt.isec.pa.chess.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.pieces.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    // Test 1: Verify board position validation
    @Test
    void testPositionValidation() {
        // Valid positions
        assertTrue(board.posicaoValida(0, 0));
        assertTrue(board.posicaoValida(7, 7));
        assertTrue(board.posicaoValida(3, 4));

        // Invalid positions
        assertFalse(board.posicaoValida(-1, 0));
        assertFalse(board.posicaoValida(8, 0));
        assertFalse(board.posicaoValida(0, -1));
        assertFalse(board.posicaoValida(0, 8));
    }

    // Test 2: Verify piece addition and removal
    @Test
    void testAddRemovePiece() {
        // Test adding a new piece
        Piece testRook = new Rook(true, 3, 3, board);
        assertTrue(board.addPiece(testRook));
        assertEquals(testRook, board.getPiece(3, 3));

        // Test adding to occupied square
        assertFalse(board.addPiece(new Rook(true, 3, 3, board)));

        // Test adding to invalid position
        assertFalse(board.addPiece(new Rook(true, -1, 3, board)));

        // Test removing a piece
        board.removePiece(3, 3);
        assertNull(board.getPiece(3, 3));

        // Test removing from empty square
        board.removePiece(4, 4); // Should not throw exception
    }

    // Test 3: Verify king location methods
    @Test
    void testKingLocation() {
        // Test white king position
        Piece whiteKing = board.getKing(true);
        assertNotNull(whiteKing);
        assertEquals(7, whiteKing.getLine());
        assertEquals(4, whiteKing.getColumn());

        // Test black king position
        Piece blackKing = board.getKing(false);
        assertNotNull(blackKing);
        assertEquals(0, blackKing.getLine());
        assertEquals(4, blackKing.getColumn());

        // Test with no king on board (simulated)
        board.removePiece(7, 4);
        assertNull(board.getKing(true));
    }

    // Test 4: Verify check detection
    @Test
    void testCheckDetection() {
        // Initial state - no check
        assertFalse(board.isCheck(true));
        assertFalse(board.isCheck(false));

        // Simulate check on black king
        board.removePiece(1, 4); // Remove pawn in front of black king
        board.addPiece(new Queen(true, 3, 4, board)); // White queen threatening black king
        assertTrue(board.isCheck(false));
        assertFalse(board.isCheck(true));

        // Simulate check on white king
        board.removePiece(6, 4); // Remove pawn in front of white king
        board.addPiece(new Queen(false, 5, 4, board)); // Black queen threatening white king
        assertTrue(board.isCheck(true));
    }

    // Test 5: Verify piece movement validation
    @Test
    void testMoveValidation() {
        // Test valid pawn move
        Piece whitePawn = board.getPiece(6, 0);
        assertNotNull(whitePawn);
        assertTrue(whitePawn.canMove(4, 0));

        // Test invalid pawn move (through another piece)
        assertFalse(whitePawn.canMove(3, 0));

        // Test valid knight move
        Piece whiteKnight = board.getPiece(7, 1);
        assertNotNull(whiteKnight);
        assertTrue(whiteKnight.canMove(5, 0));
        assertTrue(whiteKnight.canMove(5, 2));

        // Test invalid knight move
        assertFalse(whiteKnight.canMove(6, 1));
    }

    // Test 6: Verify castling movement
    @Test
    void testCastlingMovement() {
        // Clear path for white kingside castling
        board.removePiece(7, 1); // knight
        board.removePiece(7, 2); // bishop
        board.removePiece(7, 3); // queen

        Piece king = board.getPiece(7, 4);
        Piece rook = board.getPiece(7, 7);

        // Perform castling
        board.movePiecesForCastle((King)king, (Rook)rook);

        // Verify new positions
        assertEquals(7, king.getLine());
        assertEquals(6, king.getColumn());
        assertEquals(7, rook.getLine());
        assertEquals(5, rook.getColumn());

        // Verify old positions are empty
        assertNull(board.getPiece(7, 4));
        assertNull(board.getPiece(7, 7));
    }

    // Test 7: Verify board state copying in constructor
    @Test
    void testBoardCopyConstructor() {
        // Make some changes to the original board
        board.removePiece(6, 0);
        board.addPiece(new Queen(true, 5, 0, board));

        // Create a copy
        Board copy = new Board(board.board);

        // Verify pieces are the same
        assertNull(copy.getPiece(6, 0));
        assertNotNull(copy.getPiece(5, 0));
        assertEquals(Board.type.QUEEN, copy.getPiece(5, 0).getType());

        // Verify changes to copy don't affect original
        copy.removePiece(5, 0);
        assertNotNull(board.getPiece(5, 0));
    }

    // Test 8: Verify threat detection
    @Test
    void testThreatDetection() {
        // Test square under threat
        board.removePiece(1, 4); // Remove pawn in front of black king
        assertTrue(board.isUnderThreat(false, 2, 4)); // Square now threatened by white pawn

        // Test square not under threat
        assertFalse(board.isUnderThreat(false, 3, 3));

        // Test with queen threatening multiple squares
        board.addPiece(new Queen(true, 3, 3, board));
        assertTrue(board.isUnderThreat(false, 0, 3)); // Vertical threat
        assertTrue(board.isUnderThreat(false, 3, 0)); // Horizontal threat
        assertTrue(board.isUnderThreat(false, 0, 0)); // Diagonal threat
    }

    // Test 9: Verify movePieceForUndo functionality
    @Test
    void testMovePieceForUndo() {
        Piece pawn = board.getPiece(6, 0);

        // Perform undo move without validation
        Piece moved = board.movePieceForUndo(6, 0, 4, 0);

        // Verify move was executed
        assertEquals(pawn, moved);
        assertNull(board.getPiece(6, 0));
        assertEquals(pawn, board.getPiece(4, 0));
        assertEquals(4, pawn.getLine());
        assertEquals(0, pawn.getColumn());

        // Verify invalid moves return null
        assertNull(board.movePieceForUndo(-1, 0, 0, 0));
        assertNull(board.movePieceForUndo(0, 0, 0, 0)); // From empty square
    }

    // Test 10: Verify kingStillUnderThreat logic
    @Test
    void testKingStillUnderThreat() {
        // Setup scenario where moving would leave king in check
        board.removePiece(6, 4); // Remove white pawn
        board.removePiece(7, 5); // Remove white bishop
        board.addPiece(new Queen(false, 5, 4, board)); // Black queen threatening

        // Try to move white king - should still be under threat
        assertTrue(board.kingStillUnderThreat(true, 7, 4, 7, 5));

        // Try valid move that gets out of check
        board.addPiece(new Rook(true, 6, 5, board)); // Blocking piece
        assertFalse(board.kingStillUnderThreat(true, 7, 4, 7, 5));
    }
}
