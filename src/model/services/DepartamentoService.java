package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartamentoService {

	public List<Departamento> findAll(){
		List<Departamento> departamentos = new ArrayList<>();
		departamentos.add(new Departamento(1,"Livros"));
		departamentos.add(new Departamento(2,"Computadores"));
		departamentos.add(new Departamento(3,"Eletronicos"));
		return departamentos;
	}
}
