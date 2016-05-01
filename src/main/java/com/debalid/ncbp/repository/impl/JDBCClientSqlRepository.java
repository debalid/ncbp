package com.debalid.ncbp.repository.impl;

import com.debalid.ncbp.entity.Client;
import com.debalid.ncbp.repository.ClientSqlRepository;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by debalid on 01.05.2016.
 */
public class JDBCClientSqlRepository extends AbstractJDBCConsumer implements ClientSqlRepository {
    public JDBCClientSqlRepository() throws NamingException {
        super();
    }

    @Override
    public List<Client> findAll() throws SQLException {
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT clients.* " +
                                "FROM clients"
                );
        ) {
            ps.execute();
            return this.mapAllToEntities(ps.getResultSet(), "");
        }
    }

    private List<Client> mapAllToEntities(ResultSet beginPoint, String tablePrefix) throws SQLException {
        List<Client> clients = new LinkedList<>();
        while (beginPoint.next()) {
            clients.add(JDBCEntityMappers.mapToClient(beginPoint, tablePrefix));
        }
        return clients;
    }

    @Override
    public Optional<Client> find(Integer id) throws SQLException {
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT clients.* " +
                                "FROM clients " +
                                "WHERE client_id = ?"
                );
        ) {
            ps.setInt(1, id);
            ps.execute();
            List<Client> res = this.mapAllToEntities(ps.getResultSet(), "");
            return res.isEmpty()
                    ? Optional.<Client>empty()
                    : Optional.of(res.get(0));
        }
    }

    @Override
    public void save(Client some) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Client delete(Client some) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
