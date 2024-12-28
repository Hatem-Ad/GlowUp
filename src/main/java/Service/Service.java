package Service;

import java.sql.SQLException;
import java.util.List;

public interface Service<T> {
    boolean ajouter(T t) throws SQLException;
    boolean deleteCategory(T t) throws SQLException;
    boolean update(T t) throws SQLException;
    T findById(int id) throws SQLException;
    List<T> getAllCategories() throws SQLException;

}
