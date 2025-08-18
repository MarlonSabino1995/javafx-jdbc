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
import model.dao.SellerDao;
import model.entities.Departamento;
import model.entities.Vendedor;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	@Override
	public void insert(Vendedor obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("insert into vendedor(nome, email, data_de_nascimento, salario_base, departamento_id) "
					+ "values(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataDeNascimento().getTime()));
			st.setDouble(4, obj.getSalarioBase());
			st.setInt(5,obj.getDepartamento().getId());
		
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
				throw new DBExeception("Error ao inserir dados ! Nenhuma linha afetada!");
			}
			
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
		}
		
		
	}

	@Override
	public void update(Vendedor obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE vendedor set nome = ?, email = ?, data_de_nascimento = ?"
					+ " salario_base = ?, departamento_id = ? where id = ?");
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataDeNascimento().getTime()));
			st.setDouble(4, obj.getSalarioBase());
			st.setInt(5,obj.getDepartamento().getId());
			st.setInt(6, obj.getId());
		
			st.executeUpdate();
		
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
		}
		
	}

	@Override
	public void delete(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM vendedor where id = ?");
			st.setInt(1, id);
			st.executeQuery();
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
		}
		
	}

	@Override
	public Vendedor findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs =null;
		try {
			st = conn.prepareStatement("SELECT vendedor.*,departamento.nome as DepName "
					+ "from vendedor inner join departamento on vendedor.departamento_id = departamento.id "
					+ "where vendedor.id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) {
				Departamento dp = instantiateDepartment(rs);
				
				Vendedor seller = instantiateSeller(rs,dp);
				return seller ;
			}
			
			return null;
		
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Vendedor instantiateSeller(ResultSet rs,Departamento dep) throws SQLException {
		Vendedor seller = new Vendedor();
		seller.setId(rs.getInt("id"));;
		seller.setNome(rs.getString("nome"));
		seller.setEmail(rs.getString("email"));
		seller.setSalarioBase(rs.getDouble("salario_base"));
		seller.setDataDeNascimento(rs.getDate("data_de_nascimento"));
		seller.setDepartamento(dep);
		return seller;
	}
	private Departamento instantiateDepartment(ResultSet rs) throws SQLException{
		Departamento dp = new Departamento();
		dp.setId(rs.getInt("id"));
		dp.setNome(rs.getString("nome"));
		return dp;
	}
	@Override
	public List<Vendedor> findAll() {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT vendedor.*, departamento.nome as DepName"
					+ " from vendedor inner join departamento "
					+ "on vendedor.departamento_id = departamento.id "
					+ "order by nome");
			
			rs = st.executeQuery();
			
			List<Vendedor> list = new ArrayList<Vendedor>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while(rs.next()) {
				
				Departamento dep = map.get(rs.getInt("departamento_id"));
				
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("departamento_id"), null);
				}
				
				Vendedor obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}
		finally {
			DB.closePreparedStatement(st);
			DB.closeResultSet(rs);
		}
		
	}
	@Override
	public List<Vendedor> findByDepartment(Departamento dp) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select vendedor.*, departamento.nome as DepName "
					+ "from vendedor inner join departamento "
					+ "on vendedor.departamento_id = departamento.id "
					+ "where departamento_id = ? "
					+ "order by nome");
			st.setInt(1, dp.getId());
			rs = st.executeQuery();
			
			List<Vendedor> list = new ArrayList<Vendedor>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while(rs.next()) {
				Departamento dep = map.get(rs.getInt("departamento_id"));
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("departamento_id"), dep);
				}
				Vendedor seller = instantiateSeller(rs, dep);
				list.add(seller);
			}
			return list;
		}catch(SQLException e) {
			throw new DBExeception(e.getMessage());
		}finally {
			DB.closePreparedStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
