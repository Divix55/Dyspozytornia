package pl.sip.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.sip.dto.SupplyTicket;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

@Repository
public class SupplyTicketDAOImpl implements SupplyTicketDAO {

    private final DataSource dataSource;
    protected final Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    public SupplyTicketDAOImpl(DataSource dataSource) { this.dataSource = dataSource; }

    public ArrayList<SupplyTicket> createTicketTable() {
        String sql = "select * from Supply";
        Connection connection = null;
        ArrayList<SupplyTicket> listOfTickets = new ArrayList<SupplyTicket>();

        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SupplyTicket newPoint = new SupplyTicket();
                newPoint.setTicketId(resultSet.getInt("SupplyId"));
                newPoint.setShopId(resultSet.getInt("ShopId"));
                newPoint.setStoreId(resultSet.getInt("StoreId"));
                newPoint.setDriverId(resultSet.getInt("DriverId"));
                newPoint.setDuration(resultSet.getInt("DurationTime"));
                newPoint.setDeliveryDate(resultSet.getString("DeliveryDate"));
                newPoint.setCompleted(resultSet.getBoolean("isCompleted"));
                listOfTickets.add(newPoint);
            }
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if(connection != null){
                try{
                    connection.close();
                } catch(SQLException e){
                    System.out.print("Exception in closing connection!");
                }
            }
        }
        return listOfTickets;
    }

    public void createTicket(SupplyTicket ticket) {
        String sql = "Insert into Supply (StoreId, ShopId, DriverId, DeliveryDate, DurationTime, isCompleted)"
                + "values(?, ?, ?, ?, ?, ?)";
        Random rand = new Random();

        int shopId = convertNameToId(ticket.getShopName());
        int storeId = ticket.getStoreId();
        int driverId = ticket.getDriverId();

        Connection connection = null;


        try {
            connection = dataSource.getConnection();
            boolean completed = false;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);
            preparedStatement.setInt(2, shopId);
            preparedStatement.setInt(3, driverId);
            preparedStatement.setString(4, ticket.getDeliveryDate());
            preparedStatement.setInt(5, ticket.getDuration());
            preparedStatement.setBoolean(6, completed);
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e){
                    System.out.print("Exception in closing connection!");
                }
            }
        }
    }

    public String getShopsName(int shopsId) {
        String sql = "Select * from Shops where ShopId = ?";
        String shopName = "";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, shopsId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                shopName = resultSet.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.print("Exception in closing connection!");
                }
            }

        }
        return shopName;
    }

    @Override
    public int[] getDriversByStoreId(int storeId) {
        String sql = "Select * from Drivers where StoreId = ?";
        int []drivers = new int[15];
        int driverCounter = 0;

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                drivers[driverCounter] = resultSet.getInt("DriverId");
                driverCounter += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.print("Exception in closing connection!");
                }
            }

        }

        int[] d = new int[driverCounter];
        for (int i = 0; i<driverCounter; i++){
            d[i] = drivers[i];
        }

        return drivers;
    }

    @Override
    public ArrayList<SupplyTicket> getTicketsByDrivers(int[] drivers) {
        ArrayList<SupplyTicket> tickets = new ArrayList<>();
        for(int driverId: drivers){
            String sql = "Select * from Supply where DriverId = ? and IsCompleted = FALSE ";
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, driverId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    SupplyTicket ticket = new SupplyTicket();
                    ticket.setStoreId(resultSet.getInt("StoreId"));
                    ticket.setShopId(resultSet.getInt("ShopId"));
                    ticket.setDriverId(driverId);
                    ticket.setDeliveryDate(resultSet.getString("DeliveryDate"));
                    tickets.add(ticket);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        System.out.print("Exception in closing connection!");
                    }
                }
            }
        }
        return tickets;
    }

    private int checkSize(String tableName) {
        String sql = "Select * from " + tableName;
        int count = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count++;
            }
        } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        System.out.print("Exception in closing connection!");
                    }
                }

            }
        return count;
    }

    private int convertNameToId(String name) {
        String sql = "Select * from Shops where Name = ?";

        Connection connection = null;
        int id = -1;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("ShopId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.print("Exception in closing connection!");
                }
            }

        }
        return id;
    }
}
