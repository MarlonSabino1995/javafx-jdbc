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
import model.entities.Departamento;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;

public class DepartmentFormController implements Initializable{

	private Departamento departamento;
	
	private DepartamentoService depService;
	
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
	
	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}
	
	public void setDepService(DepartamentoService depService) {
		this.depService = depService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if(departamento == null) {
			throw new IllegalStateException("Departamento está nulo");
		}
		if(depService == null) {
			throw new IllegalStateException("Seviço está nulo");
		}
		try {
			departamento = getFormData();
			depService.salvarOuAtualizar(departamento);
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

	private Departamento getFormData() {
		Departamento dep  = new Departamento();
		
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
		if(departamento == null) {
			throw new IllegalStateException("Departamento está nulo");
		}
			
		txtId.setText(String.valueOf(departamento.getId()));
		txtNome.setText(departamento.getNome());
	}
	
	private void setErrorMenssage(Map<String, String> erros) {
		Set<String> fields = erros.keySet();
		
		if(fields.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}
	}
	
}
