package pt.isec.pa.chess.model.memento;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Memento implements IMemento{
    byte[] snapshot;

    public Memento(Object obj){
        try {
            System.out.println("Criando Memento...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            snapshot = baos.toByteArray();
            System.out.println("Memento criado com sucesso. Tamanho: " + snapshot.length + " bytes");
        } catch (Exception e) {
            /*System.err.println("Erro ao criar Memento: " + e.getMessage());
            throw new RuntimeException(e);*/
            snapshot = null;
        }
    }

    @Override
    public Object getSnapshot() {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(snapshot);
            ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
           return ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore memento snapshot", e);
        }
    }
}
