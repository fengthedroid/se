package domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import control.MainControl;

public class Prescription {
	
	public Prescription(/*Patient p,*/ String physician, String issueDate, String effectiveDate, Set<String> drugLines, boolean activeState){
		//this.setOwner(p);
		this.setPhysician(physician);
		this.setIssueDate(issueDate);
		this.setEffectiveDate(effectiveDate);
		this.setDrugLines(drugLines);
		this.active = activeState;
	}
	
	/*private Patient owner;
	
	public Patient getOwner() {
		return owner;
	}

	protected void setOwner(Patient owner) {
		this.owner = owner;
	}*/
	
	/**
	 * flag for the state of the prescription. when created it is active and when saved it is achieved.
	 */
	private boolean active;
	
	public boolean isActive(){
		return this.active;
	}
	
	public void achieve(){
		this.active = false;
	}

	private String physician;
	
	public String getPhysician() {
		return physician;
	}

	public void setPhysician(String physician) {
		this.physician = physician;
	}

	private String issueDate;
	
	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	private String effectiveDate;
	
	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	private Set<String> drugLines;

	public Set<String> getDrugLines() {
		return drugLines;
	}

	public void setDrugLines(Set<String> drugLines) {
		this.drugLines = drugLines;
	}
	
	public void save(){
		assert this.isActive()==true;
		this.achieve();
		this.setIssueDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		MainControl.getMainControl().getPatientManager().savePrescription(this);
	}
}