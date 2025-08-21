package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Vendedor;

public class VendedorService {

	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Vendedor> findAll(){
		return dao.findAll();
	}
	
	public void salvarOuAtualizar(Vendedor dep) {
		if(dep.getId() == null) {
			dao.insert(dep);
		}else{
			dao.update(dep);
		}
	}
	
	public void remove(Vendedor vendedor) {
		dao.delete(vendedor.getId());
	}
}
