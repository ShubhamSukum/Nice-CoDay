package com.nice.Optimizers404;

import java.util.List;

public class ConstituencyResult {

	private String winnerName;
	private List<CandidateVotes> candidateList;
	
	public ConstituencyResult() {
		super();
	}

	public ConstituencyResult(String winnerName, List<CandidateVotes> candidateList) {
		super();
		this.winnerName = winnerName;
		this.candidateList = candidateList;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}

	public List<CandidateVotes> getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(List<CandidateVotes> candidateList) {
		this.candidateList = candidateList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConstituencyResult [winnerName=");
		builder.append(winnerName);
		builder.append(", candidateList=");
		builder.append(candidateList);
		builder.append("]");
		return builder.toString();
	}

}
