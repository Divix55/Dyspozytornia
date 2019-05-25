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
    Logger log = Logger.getLogger("SupplyTicketDAOImpl");

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
                newPoint.setTicketStatus(resultSet.getString("Status"));
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

    public void createTicketEntry(SupplyTicket ticket){
        String sql = "Insert into Supply (ShopId, ShopName, DeliveryDate, Status, isCompleted)"
                + "values(?, ?, ?, ?, ?)";

        String date = ticket.getShopYear() + "-" + ticket.getShopMonth() + "-" + ticket.getShopDay();
        String hour = ticket.getShopHour() + ":" + ticket.getShopMinute();
        int shopId = convertNameToId(ticket.getShopName());

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            boolean completed = false;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, shopId);
            preparedStatement.setString(2, ticket.getShopName());
            preparedStatement.setString(3, date + " " + hour);
            preparedStatement.setString(4, "oczekujace");
            preparedStatement.setBoolean(5, completed);
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

    public void createTicketNaive(SupplyTicket ticket) {
        String sql = "update Supply set StoreId = ?, DriverId = ?, DeliveryDate = ?, DurationTime = ?, Status = ? where SupplyId = ?";

        int storeId = ticket.getStoreId();
        int driverId = ticket.getDriverId();

        Connection connection = null;

        try {
            connection = dataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);
            preparedStatement.setInt(2, driverId);
            preparedStatement.setString(3, ticket.getDeliveryDate());
            preparedStatement.setInt(4, ticket.getDuration());
            preparedStatement.setString(5, ticket.getTicketStatus());
            preparedStatement.setInt(6, ticket.getTicketId());
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

    public float getShopsLon(int shopsId) {
        String sql = "Select * from Shops where ShopId = ?";
        return executeLonSelect(shopsId, sql);
    }

    public float getShopsLat(int shopsId) {
        String sql = "Select * from Shops where ShopId = ?";
        return executeLatSelect(shopsId, sql);
    }

    public float getStoreLon(int storeId){
        String sql = "Select * from Stores where StoreId = ?";
        return executeLonSelect(storeId, sql);
    }

    public float getStoreLat(int storeId){
        String sql = "Select * from Stores where StoreId = ?";
        return executeLatSelect(storeId, sql);
    }

    private float executeLonSelect(int shopsId, String sql) {
        Connection connection = null;
        float lon=0;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, shopsId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lon = resultSet.getFloat("Longitude");
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

        return lon;
    }

    private float executeLatSelect(int storeId, String sql) {
        Connection connection = null;
        float lat=0;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lat = resultSet.getFloat("Latitude");
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
        return lat;
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
