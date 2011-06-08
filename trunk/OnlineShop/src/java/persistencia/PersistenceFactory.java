package persistencia;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class PersistenceFactory {
    public static PersistenceInterface getInstance (String type){
        if (type.equals("pool")){
            return PersistenceBD.getInstance();
        }else if (type.equals("file")){
            return PersistenceFile.getInstance();
        }else {
            return null;
        }
    }
}
