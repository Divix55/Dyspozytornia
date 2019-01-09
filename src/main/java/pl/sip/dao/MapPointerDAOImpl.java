package pl.sip.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.sip.dto.NewMapPointer;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MapPointerDAOImpl implements MapPointerDAO {

    private final DataSource dataSource;

    @Autowired
    public MapPointerDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<NewMapPointer> createStoreTable(){
        String sql = "select StoreId, Name, Longitude, Latitude from Stores";
        Connection connection = null;
        ArrayList<NewMapPointer> listOfPointers = new ArrayList<NewMapPointer>();

        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NewMapPointer newPoint = new NewMapPointer();
                newPoint.setPointId(resultSet.getInt("StoreId"));
                newPoint.setPointName(resultSet.getString("Name"));
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

    public ArrayList<NewMapPointer> createShopTable(){
        String sql = "select ShopId, Name, Longitude, Latitude from Shops";
        Connection connection = null;
        ArrayList<NewMapPointer> listOfPointers = new ArrayList<NewMapPointer>();

        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NewMapPointer newPoint = new NewMapPointer();
                newPoint.setPointId(resultSet.getInt("ShopId"));
                newPoint.setPointName(resultSet.getString("Name"));
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
