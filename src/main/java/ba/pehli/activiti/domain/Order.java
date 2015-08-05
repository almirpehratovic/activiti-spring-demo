package ba.pehli.activiti.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="orders")
public class Order implements Serializable{
	private int id;
	private String account;
	private double amount;
	private boolean control;
	private String executionId;
	
	public Order(){
		
	}
	
	public Order(int id, String konto, double iznos, String status) {
		this.id = id;
		this.account = konto;
		this.amount = iznos;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="account")
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	@Column(name="amount")
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@Column(name="control")
	public boolean isControl() {
		return control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}
	
	@Column(name="executionId")
	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String toString(){
		return "[Narudzbenica " + id + ",konto " + account + ",iznos " + amount + "]";
	}
}
