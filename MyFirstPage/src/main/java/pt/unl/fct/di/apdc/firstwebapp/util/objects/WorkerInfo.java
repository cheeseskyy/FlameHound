package pt.unl.fct.di.apdc.firstwebapp.util.objects;

import java.util.List;

public class WorkerInfo {
	public String username;
	public String entity;
	public long approvalRate;
	public long disapprovalRate;
	public long occurrenciesTreated;
	public List<String> occurrencies;
	public long rating;
	
	public WorkerInfo() {

	}

	public WorkerInfo(String username, String entity, long approvalRate, long disapprovalRate, long occurrenciesTreated,
			List<String> occurrencies) {
		this.username = username;
		this.entity = entity;
		this.approvalRate = approvalRate;
		this.disapprovalRate = disapprovalRate;
		this.occurrenciesTreated = occurrenciesTreated;
		this.occurrencies = occurrencies;
		this.rating = 0;
		if(approvalRate > 0 && disapprovalRate > 0)
			rating = Math.min(10,approvalRate/disapprovalRate);
		else if(approvalRate > 0)
			rating = 5;
	}

	
}