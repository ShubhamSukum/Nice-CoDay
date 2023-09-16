package com.nice.Optimizers404;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Solution {
	private static String NO_WINNER = "NO_WINNER";

	// Read CandidateFile and create DataSructure for storing values
	private HashMap<String, ConstituencyResult> createDS(Path candidateFile)
	{
		HashMap<String, ConstituencyResult> results = new HashMap<>();

		try
		{
			// Create readers for file
			BufferedReader candidateBufferedReader = new BufferedReader(new FileReader(candidateFile.toFile()));
		
			// remove first line
			candidateBufferedReader.readLine();

			String line = candidateBufferedReader.readLine();

			// Read full File in loop
			while(line != null)
			{
				String[] strRead = line.split(",");
				String constituency = strRead[0];
				String candidateName = strRead[1];
				// System.out.println(constituency + " " + candidateName);
			
				if(results.containsKey(constituency))	// Object present
				{
					results.get(constituency).getCandidateList().add(new CandidateVotes(candidateName, 0));
				}
				else	// Create the Objects not Present
				{
					ConstituencyResult cr = new ConstituencyResult();
					List<CandidateVotes> candidateList = new ArrayList<>();
					cr.setCandidateList(candidateList);
					candidateList.add(new CandidateVotes("NOTA", 0));
					candidateList.add(new CandidateVotes(candidateName, 0));
					results.put(constituency, cr);
				}

				line = candidateBufferedReader.readLine();
			}
			candidateBufferedReader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		return results;
	}

	// Read VotingFile for counting the number of votes each person did
	private HashMap<String, Integer> countNumberOfVotes(HashMap<String, ConstituencyResult> results, Path votingFile)
	{
		HashMap<String, Integer> numberOfTimesVoted = new HashMap<>();
		try
		{
			BufferedReader votingBufferedReader = new BufferedReader(new FileReader(votingFile.toFile()));
			

			// remove first line
			votingBufferedReader.readLine();

			String line = votingBufferedReader.readLine();

			// Read full File in loop
			while(line != null)
			{
				String voterName = line.split(",")[0];
				
				if(numberOfTimesVoted.containsKey(voterName))	// Add vote to existing vote
				{
					numberOfTimesVoted.put(voterName, numberOfTimesVoted.get(voterName) +1);
				}
				else	// First vote
				{
					numberOfTimesVoted.put(voterName, 1);
				}

				// Read next line till EOL
				line = votingBufferedReader.readLine();
			}
			votingBufferedReader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		return numberOfTimesVoted;
	}

	// Voting of People
	private void doVoting(HashMap<String, Integer> numberOfTimesVoted, HashMap<String, ConstituencyResult> results, Path votingFile)
	{
		try
		{
			BufferedReader votingBufferedReader = new BufferedReader(new FileReader(votingFile.toFile()));
			
			// remove first line
			votingBufferedReader.readLine();

			String line = votingBufferedReader.readLine();

			// Read full File in loop
			while(line != null)
			{
				// Reading the individual fields from file.
				String[] tmpLine = line.split(",");
				String voterName = tmpLine[0];
				String constituency = tmpLine[1];
				// No need of Polling Station
				// String pollingStation = tmpLine[2];
				String candidateName = tmpLine[3];

				// Discarded duplicate votes
				if(numberOfTimesVoted.get(voterName) == 1)
				{
					results.get(constituency).getCandidateList().forEach(candidate ->{

							if(candidate.getCandidateName().equals(candidateName))
							{
								candidate.setVotes(candidate.getVotes() +1);
							}
						}
					);
				}

				// Read next Line til EOL
				line = votingBufferedReader.readLine();
			}
			votingBufferedReader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	// Sorting the Results
	private void sortResults(HashMap<String, ConstituencyResult> results)
	{
		// Using custom comparator by using anonymous class
		results.keySet().forEach(resultKey -> {
			results.get(resultKey).getCandidateList().sort(new Comparator<CandidateVotes>() {
				@Override
				public int compare(CandidateVotes o1, CandidateVotes o2) {
					if(o1.getCandidateName().equals("NOTA"))
						return -1;
					if(o2.getCandidateName().equals("NOTA"))
						return -1;
					
					if(o1.getVotes() > o2.getVotes())
						return -1;
					else if(o1.getVotes() == o2.getVotes())
						return o1.getCandidateName().compareTo(o2.getCandidateName());

					return 1;
				}
			});
		});
	}

	// Setting Winners
	private void votingWinners(HashMap<String, ConstituencyResult> results)
	{
		results.keySet().forEach(resultKey -> {
			List<CandidateVotes> candidateList = results.get(resultKey).getCandidateList();
			int candidateListSize = candidateList.size();

			// No need of Nota Votes
			// Long notaVotes = candidateList.get(candidateListSize -1).getVotes();
			Long firstMaxVotes = candidateList.get(0).getVotes();
			Long secondMaxVotes = candidateList.get(1).getVotes();

			if(candidateListSize == 2)	// Only 1 Candidate and NOTA
			{
				//	Therefore Candidate WON
				results.get(resultKey).setWinnerName(candidateList.get(0).getCandidateName());
			}
			else	// More than 1 candidate and NOTA
			{
				// First have more votes, therfore won the voting
				if(firstMaxVotes > secondMaxVotes)
				{
					results.get(resultKey).setWinnerName(candidateList.get(0).getCandidateName());
				}
				else	// first and second have same vote therfore No Winner of voting
				{
					results.get(resultKey).setWinnerName(Solution.NO_WINNER);
				}
			}
		});
	}

	public ElectionResult execute(Path candidateFile, Path votingFile) {
		ElectionResult resultData = new ElectionResult();

		HashMap<String, ConstituencyResult> resultsMap = createDS(candidateFile);
		HashMap<String, Integer> numberOfVotesCount = countNumberOfVotes(resultsMap, votingFile);
		doVoting(numberOfVotesCount, resultsMap, votingFile);
		sortResults(resultsMap);
		votingWinners(resultsMap);

		resultData.setResultData(resultsMap);
		// System.out.println(resultData.toString());

		return resultData;
	}

}