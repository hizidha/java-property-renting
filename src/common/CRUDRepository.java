package common;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T> {
    int add(T t);

    int update(T t);

    int delete(int id);

    List<T> getAll();

    int dataAvailabilityAdd(T t);

    int dataAvailabilityDelete(T t);

    Optional<T> findBy(int id);

    Optional<T> findBy(String name);

    List<T> findListBy(String name);
}