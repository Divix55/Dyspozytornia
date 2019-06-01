package pl.sip.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.sip.dto.NewMapPointer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

@Repository
public class MapPointerDAOImpl implements MapPointerDAO {

    private final DataSource dataSource;
    private Logger log = Logger.getLogger("MapPointerDAO");

    @Autowired
    public MapPointerDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<NewMapPointer> createStoreTable(){
        String sql = "select StoreId, Name, NIP, City, Street, HomeNumber, Longitude, Latitude from Stores";
        Connection connection = null;
        ArrayList<NewMapPointer> listOfPointers = new ArrayList<>();

        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NewMapPointer newPoint = new NewMapPointer();
                newPoint.setPointId(resultSet.getInt("StoreId"));
                newPoint.setPointName(resultSet.getString("Name"));
                newPoint.setNip(resultSet.getString("NIP"));
                newPoint.setPointCity(resultSet.getString("City"));
                newPoint.setPointAddress(resultSet.getString("Street"));
                newPoint.setPointAddressBlockNumber(resultSet.getString("HomeNumber"));
                newPoint.setPointLongitude(resultSet.getDouble("Longitude"));
                newPoint.setPointLatitude(resultSet.getDouble("Latitude"));
                listOfPointers.add(newPoint);
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

        return listOfPointers;
    }

    public void createMapPointer(NewMapPointer mapPointer, String typeOfPoint) {
        String sql = "";
        if (typeOfPoint.equals("Stores")) {
            sql = "Insert into Stores (Name, NIP, City, Street, HomeNumber, Longitude, Latitude)" + "values(?, ?, ?, ?, ?, ?, ?)";
        } else if (typeOfPoint.equals("Shops")){
            sql = "Insert into Shops (Name, NIP, City, Street, HomeNumber, Longitude, Latitude)" + "values(?, ?, ?, ?, ?, ?, ?)";
        }
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, mapPointer.getPointName());
            preparedStatement.setString(2, mapPointer.getNip());
            preparedStatement.setString(3, mapPointer.getPointCity());
            preparedStatement.setString(4, mapPointer.getPointAddress());
            preparedStatement.setString(5, mapPointer.getPointAddressBlockNumber());
            preparedStatement.setDouble(6, mapPointer.getPointLongitude());
            preparedStatement.setDouble(7, mapPointer.getPointLatitude());
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

    public NewMapPointer getPointerByName(String shopName) {
        String sql = "Select * from Shops where Name=?";
        Connection connection = null;
        NewMapPointer newPoint = new NewMapPointer();

        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, shopName);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                newPoint.setPointId(resultSet.getInt("ShopId"));
                newPoint.setPointName(resultSet.getString("Name"));
                newPoint.setPointLongitude(resultSet.getDouble("Longitude"));
                newPoint.setPointLatitude(resultSet.getDouble("Latitude"));
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

        return newPoint;
    }


    public ArrayList<NewMapPointer> createShopTable(){
        String sql = "select ShopId, Name, NIP, City, Street, HomeNumber, Longitude, Latitude from Shops";
        Connection connection = null;
        ArrayList<NewMapPointer> listOfPointers = new ArrayList<>();

        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NewMapPointer newPoint = new NewMapPointer();
                newPoint.setPointId(resultSet.getInt("ShopId"));
                newPoint.setPointName(resultSet.getString("Name"));
                newPoint.setNip(resultSet.getString("NIP"));
                newPoint.setPointCity(resultSet.getString("City"));
                newPoint.setPointAddress(resultSet.getString("Street"));
                newPoint.setPointAddressBlockNumber(resultSet.getString("HomeNumber"));
                newPoint.setPointLongitude(resultSet.getDouble("Longitude"));
                newPoint.setPointLatitude(resultSet.getDouble("Latitude"));
                listOfPointers.add(newPoint);
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

        return listOfPointers;
    }
}
