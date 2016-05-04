package com.debalid.ncbp.repository;

import com.debalid.ncbp.entity.Order;

import java.sql.SQLException;
import java.util.List;

/**
 * Main repository interface for orders.
 * Created by debalid on 20.04.2016.
 */
public interface OrderSqlRepository extends SqlRepository<Order, Long> {
    List<Order> findByNumber(String numberChunk) throws SQLException;

    List<Order> findByClientTitle(String clientTitleChunk) throws SQLException;

    /**
     * Find orders by its number and client's title
     *
     * @param numberChunk      Chunk to be contained in number. Null means ignore.
     * @param clientTitleChunk Chunk to be contained is client's title. Null means ignore.
     * @return List of founded orders.
     * @throws SQLException If there is exception during connection with database.
     */
    List<Order> findByNumberAndClient(String numberChunk, String clientTitleChunk) throws SQLException;
}
