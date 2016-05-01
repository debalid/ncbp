package com.debalid.ncbp.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * There is a requirement to throw SQL exceptions if something goes wrong with database :(
 * Created by debalid on 20.04.2016.
 */
public interface SqlRepository<TEntity, Identifier> {
    void save(TEntity some) throws SQLException;

    TEntity delete(TEntity some) throws SQLException;

    Optional<TEntity> find(Identifier id) throws SQLException;

    List<TEntity> findAll() throws SQLException;
}
