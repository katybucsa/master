package repository;


import model.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeRepository {

    private final DBConnection dbConnection;
    private static final Logger logger = LogManager.getLogger();
    private static RecipeRepository instance = null;

    static public RecipeRepository getInstance() {

        if (instance == null)
            instance = new RecipeRepository();

        return instance;
    }

    private RecipeRepository() {

        dbConnection = new DBConnection();
    }

    public Integer addRecipe(Recipe recipe) {

        Connection conn = dbConnection.getConnection();
        int id;
        try (PreparedStatement stat = conn.prepareStatement("INSERT INTO Recipe (name,ingredients,method,dificulty,preparing_time) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            Array ingredients = conn.createArrayOf("varchar", recipe.getIngredients().toArray());
            stat.setString(1, recipe.getName());
            stat.setArray(2, ingredients);
            stat.setString(3, recipe.getMethod());
            stat.setInt(4, recipe.getDificulty());
            stat.setInt(5, recipe.getPreparingTime());
            stat.executeUpdate();
            ResultSet resultSet = stat.getGeneratedKeys();
            resultSet.next();
            id = resultSet.getInt(1);
        } catch (SQLException e) {
            System.out.println("Database error " + e);
            return null;
        }
        return id;
    }

    public Recipe getById(int id) {

        logger.traceEntry("Finding recipe with id {}", id);
        Connection conn = dbConnection.getConnection();
        try (PreparedStatement stat = conn.prepareStatement("select * from Recipe where id=?")) {
            stat.setInt(1, id);
            try (ResultSet resultSet = stat.executeQuery()) {
                resultSet.next();
                String name = resultSet.getString("name");
                Array ingredients = resultSet.getArray("ingredients");
                String method = resultSet.getString("method");
                int dificulty = resultSet.getInt("dificulty");
                int preparingTime = resultSet.getInt("preparing_time");
                return new Recipe(id, name, Arrays.asList((String[]) ingredients.getArray()), method, dificulty, preparingTime);
            }
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
    }

    public List<Recipe> getAll() {

        logger.traceEntry();
        Connection conn = dbConnection.getConnection();
        List<Recipe> recipes = new ArrayList<>();
        try (PreparedStatement stat = conn.prepareStatement("select * from Recipe")) {
            try (ResultSet resultSet = stat.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    Array ingredients = resultSet.getArray("ingredients");
                    String method = resultSet.getString("method");
                    int dificulty = resultSet.getInt("dificulty");
                    int preparingTime = resultSet.getInt("preparing_time");
                    Recipe r = new Recipe(id, name, Arrays.asList((String[]) ingredients.getArray()), method, dificulty, preparingTime);
                    recipes.add(r);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
        logger.traceExit(recipes);
        return recipes;
    }

    public void deleteRecipe(Integer id) {

        logger.traceEntry("deleting recipe with {}", id);
        Connection conn = dbConnection.getConnection();
        try (PreparedStatement stat = conn.prepareStatement("DELETE FROM Recipe WHERE id=? ")) {
            stat.setInt(1, id);
            stat.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
    }
}
