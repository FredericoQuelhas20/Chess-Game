package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.Piece;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final boolean color;// True for white, false for black
    private final String nome;
    private final ArrayList<Piece> pieces;
    private final ArrayList<Piece> pawnsEnPessant;
    private ArrayList<Piece> captures;
    private int maxMoves;
    public Player (boolean color, String nome, ArrayList<Piece> pieces){
        this.color = color;
        this.nome = nome;
        this.pieces = pieces;
        captures = new ArrayList<>();
        pawnsEnPessant = new ArrayList<>();
    }


    public String getNome() {
        return nome;
    }
    public boolean getColor(){
        return color;
    }
    public ArrayList<Piece> getPieces(){ return pieces;}
    public void newPiece(Piece p){pieces.add(p);}
    public void newCapture(Piece p){
        captures.add(p);
    }
    public void lostPiece(Piece p) {
        pieces.remove(p);
    }
    public boolean promotionPiece(Piece p){
        if (p instanceof Pawn){
            return false;
        }
        return pieces.add(p);
    }

    public ArrayList<Piece> getPawnsEnPessant (){
        return pawnsEnPessant;
    }
    public void addPawnsEnPessant(Piece p){
        pawnsEnPessant.add(p);
    }
    public void removePawnsEnPessant(){
        for(Piece p: pawnsEnPessant){
            if(((Pawn) p).isEnPessant()){
                ((Pawn) p).changeEnPessant(); // para ficar guardado internamente que não está en pessant
                pieces.remove(p);
            }
        }
    }
    public String[] getCaptures(){
        if(captures.isEmpty()){
            return null;
        }
        ArrayList<String> pieces = new ArrayList<>();
        for (Piece p: captures){
            pieces.add(p.getSimbolo());
        }
        String[] s = new String[pieces.size()];
        return pieces.toArray(s);
    }
    public void pawnMoved(){
        maxMoves = 0;
    }
    public void pawnNotMoved(){
        maxMoves++;
    }
    public int getMaxMoves(){
        return maxMoves;
    }
    public boolean hasMoves(){
        int nMoves = 0;
        for(Piece p:pieces){
            if(!(p instanceof Pawn)){
                return true;
            }
            nMoves += p.getPossibleMoves().size();
        }
        return nMoves>0;
    }

    public boolean isWhite() {
        return color;
    }

    public Player deepCopy() {
        Player copy = new Player(this.color, this.nome, new ArrayList<>(this.pieces));
        copy.captures = new ArrayList<>(this.captures);
        return copy;
    }

}
