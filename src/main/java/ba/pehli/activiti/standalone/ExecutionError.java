package ba.pehli.activiti.standalone;

import java.io.Serializable;

import org.activiti.engine.delegate.BpmnError;

public class ExecutionError extends BpmnError implements Serializable{

	public ExecutionError(String errorCode, String message) {
		super(errorCode, message);
	}
	
	public String toString(){
		return getMessage();
	}
	
}
