package com.debalid.ncbp.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Abstract repository for any entity. Provides general CRUD operations.
 * There is a requirement to throw SQL exceptions if something goes wrong with database :(
 * Created by debalid on 20.04.2016.
 */
public interface SqlRepository<TEntity, Identifier> {
    /**
     * Updates entity in db if present or create a new one.
     * @param some entity to be saved.
     * @throws SQLException
     */
    void save(TEntity some) throws SQLException;

    /**
     * Deletes entity with given identifier.
     * @param id identifier of entity that should be deleted.
     * @return empty if entity was not present or deleted entity.
     * @throws SQLException
     */
    Optional<TEntity> delete(Identifier id) throws SQLException;

    /**
     * Gets entity with given identifier.
     * @param id identifier of entity that should be returned.
     * @return empty if entity was not present or found entity.
     * @throws SQLException
     */
    Optional<TEntity> find(Identifier id) throws SQLException;

    /**
     * Gets all entities from database.
     * @return list of all available entities.
     * @throws SQLException
     */
    List<TEntity> findAll() throws SQLException;
}
