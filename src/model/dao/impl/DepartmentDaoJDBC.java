package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBExeception;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Departamento;

public class DepartmentDaoJDBC implements DepartmentDao{

	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	
	
	@Override
	public void insert(Departamento obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO departamento (NOME) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNome());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
					DB.closeResultSet(rs);
				}
			}
			else {
				throw new DBExeception("Erro ao inserir novo Departamento ! Nenhuma linha afetada! ");

			}
				
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
		}
		
	}

	@Override
	public void update(Departamento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE departamento set nome = ? where id = ?");
			st.setString(1, obj.getNome());
			st.setInt(2, obj.getId());
			st.executeUpdate();
			
		}catch(SQLException e) {
			throw new DbIntegrityException(e.getLocalizedMessage());
		}finally {
			DB.closePreparedStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"Delete from departamento where id = ?");
			st.setInt(1, id);
			st.executeQuery();
					
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
		}
		
	}

	@Override
	public Departamento findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT * from departamento where id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) {
				Departamento dp = instantiateDepartment(rs);
				return dp;
			}
			return null;
			
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	@Override
	public List<Departamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT * FROM departamento ORDER BY nome",Statement.RETURN_GENERATED_KEYS);
			rs = st.executeQuery();
			
			List<Departamento> dep = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<Integer, Departamento>();
			
			while(rs.next()) {
				Departamento dp = map.get(rs.getInt("departamento.id"));
				if(dp == null) {
					dp = instantiateDepartment(rs);
					map.put(rs.getInt("departamento.id"), null);
				}
				dep.add(dp);
			}
			return dep;
			
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	private Departamento instantiateDepartment (ResultSet rs) throws SQLException {
		Departamento dp = new Departamento();
		dp.setId(rs.getInt("departamento.id"));
		dp.setNome(rs.getString("nome"));
		return dp;
	}

}
