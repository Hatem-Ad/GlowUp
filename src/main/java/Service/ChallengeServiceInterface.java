package Service;

import java.sql.SQLException;
import java.util.List;

public interface ChallengeServiceInterface<T> {
    boolean ajouter(T t) throws SQLException;
    boolean deleteChallenge(T t) throws SQLException;
    boolean update(T t) throws SQLException;
    T findById(int id) throws SQLException;
    List<T> getAllChallenges() throws SQLException;
    List<T> getChallengesByCategory(int categoryId) throws SQLException;
}
