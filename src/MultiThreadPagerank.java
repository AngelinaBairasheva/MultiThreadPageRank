import java.util.*;

public class MultiThreadPagerank extends Thread 
{
	private static HashMap<Integer, List<Integer>> matrix;
	private double[] pageranks;
	private int start;
	private int length;
	private static int n;
	
	public MultiThreadPagerank(int startIndex, int length){
		pageranks = new double[n];
		
		for (int i = 0; i < n; i++) 
			pageranks[i] = 1; 
		
		this.start = startIndex;
		this.length = length;
	}
	
	public static void setN(int m){
		n = m;
	}
	
	public static void setMatrix(HashMap<Integer, List<Integer>> m){
		matrix = m;
	}
	
	public double[] getPageranks(){
		return pageranks;
	}
	
	@Override
	public void run() {
		double coeff = 0.85; 
		double[] vector = new double[n];
		
		for (int k = 0; k < 100; k++) 
		{ 
			for(int j = 0; j < n; j++)
				vector[j] = 0;
			for (int j = start; j < (start + length); j++) 
				for(int index : getRowsIndexes(j)){
					vector[index] += pageranks[index] / matrix.get(index).size();
				}
					

			for (int i = 0; i < length; i++) 
				pageranks[i] = (1.0 - coeff) + coeff * vector[i]; 
		}
	}
	
	private List<Integer> getRowsIndexes(int ind){
		List<Integer> result = new ArrayList<>();
		
		for(Map.Entry<Integer, List<Integer>> entry : matrix.entrySet()){
			if(entry.getValue().contains(ind))
				result.add(entry.getKey());
		}
		
		return result;
	}
}
