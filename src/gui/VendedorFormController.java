package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBExeception;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.VendedorService;

public class VendedorFormController implements Initializable{

	private Vendedor vendedor;
	
	private VendedorService depService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private Label labelErroNome;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}
	
	public void setDepService(VendedorService depService) {
		this.depService = depService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if(vendedor == null) {
			throw new IllegalStateException("Vendedor está nulo");
		}
		if(depService == null) {
			throw new IllegalStateException("Seviço está nulo");
		}
		try {
			vendedor = getFormData();
			depService.salvarOuAtualizar(vendedor);
			notifyDataChanceListeners();
			Utils.currentStage(evento).close();
		}
		catch(ValidationException e) {
			setErrorMenssage(e.getErros());
		}
		catch(DBExeception e) {
			Alerts.showAlert("Erro ao salvar o objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChanceListeners() {
		for(DataChangeListener lister: dataChangeListeners) {
			lister.onDataChanged();
		}
		
	}

	private Vendedor getFormData() {
		Vendedor dep  = new Vendedor();
		
		ValidationException exception = new ValidationException("Validação de erro");
		
		dep.setId(Utils.tryParseToInt(txtId.getText()));
		if(txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErro("nome", "O campo não pode ser vazio");
		}
		
		dep.setNome(txtNome.getText());
		if(exception.getErros().size() >0) {
			throw exception;
		}
		
		return dep;
	}
	
	

	@FXML
	public void onBtCancelarAction(ActionEvent evento) {
		Utils.currentStage(evento).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	
	public void updateFormData() {
		if(vendedor == null) {
			throw new IllegalStateException("Vendedor está nulo");
		}
			
		txtId.setText(String.valueOf(vendedor.getId()));
		txtNome.setText(vendedor.getNome());
	}
	
	private void setErrorMenssage(Map<String, String> erros) {
		Set<String> fields = erros.keySet();
		
		if(fields.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}
	}
	
}
