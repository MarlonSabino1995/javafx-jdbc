package model.dao;

import java.util.List;

import model.entities.Departamento;
import model.entities.Vendedor;

public interface DepartmentDao {

	void insert(Departamento obj);
	void update(Departamento obj);
	void deleteById(Integer id);
	Departamento findById(Integer id);
	List<Departamento> findAll();
}
