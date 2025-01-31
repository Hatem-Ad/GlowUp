package Service;

import java.sql.SQLException;
import java.util.List;

public interface EventServiceInterface<E> {
    boolean ajouter(E e) throws SQLException;
    boolean deleteevent(E e) throws SQLException;
    boolean update(E e) throws SQLException;
    E findById(int id) throws SQLException;
    List<E> getAllEvenements() throws SQLException;
}
