package recipes.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import provided.util.DaoBase;
import recipes.exception.DbException;

public class RecipeDao extends DaoBase {
	public void executeBatch (List<String> sqlBatch) {
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(Statement stmt = conn.createStatement()) {
				for(String sql : sqlBatch) {
					stmt.addBatch(sql);
				}
				
				stmt.executeBatch();
				commitTransaction(conn);
				
			}
			catch(Exception e) {
				throw new DbException(e);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DbException(e);
		}
		
	}

}
