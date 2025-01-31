package Service;

import java.sql.SQLException;
import java.util.List;

public interface MessageServiceInterface<T> {

    boolean ajouter(T t) throws SQLException;
    boolean delete(T t) throws SQLException;
    boolean update(T t) throws SQLException;
    T findById(int id) throws SQLException;
    List<T> getAllMessages() throws SQLException;

}
