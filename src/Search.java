import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Search
{
	public Search() {}

	public Pair<int[][], List<String>> getMatrix(String link, int urlCount) throws IOException{
		int[][] matrix = new int[urlCount][urlCount];
		List<String> urls = new ArrayList<>();
		urls.add(link);
		
		for(int i = 0; i < urlCount; i++){
			Document doc = Jsoup.connect(urls.get(i)).get();
			for(Element url : doc.select("a[href]")){
				if(urls.size() < urlCount && !urls.contains(url.attr("abs:href"))){
					urls.add(url.attr("abs:href"));
				}
				
				int buf = urls.indexOf(url.attr("abs:href"));
				
				if(buf != -1){
					matrix[i][buf] = 1;
				}
			}
		}
		try(FileWriter writer = new FileWriter("D:\\OIP\\MyJavaConsoleApp\\matrix.txt", false)){
			for(int i = 0; i < urlCount; i++){
				for(int j = 0; j < urlCount; j++){
					writer.write(matrix[i][j] + " ");
				}
				writer.write('\n');
			}
		}
		
		return new Pair(matrix, urls);
	}

	public double[] pagerank(int[][] matrix, int n)
	{
		double coeff = 0.85;
		double[] pageranks = new double[n];
		double[] vector = new double[n];
		for (int i = 0; i < n; i++)
			pageranks[i] = 1.0;

		for (int k = 0; k < 100; k++)
		{
			for (int j = 0; j < n; j++)
			{
				vector[j] = 0;
				for (int i = 0; i < n; i++)
					vector[j] +=  getNumberOfUnits(matrix, i) != 0 ?  matrix[i][j] * pageranks[i] /  getNumberOfUnits(matrix, i) : 0;
			}

			for (int i = 0; i < n; i++)
				pageranks[i] = (1.0 - coeff) + coeff * vector[i];
		}

		return pageranks;
	}

	private int getNumberOfUnits(int[][] matrix, int i){ // метод для получения кол-ва единиц в строке
		int result = 0;

		for(int j = 0; j < matrix.length; j++)
			if(matrix[i][j] == 1)
				result++;

		return result;
	}

	public HashMap<Integer, List<Integer>> matrixReduce(int[][] matrix, int n){
		HashMap<Integer, List<Integer>> res = new HashMap<>();

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				if(matrix[i][j] == 1){
					if(res.containsKey(i)){
						res.get(i).add(j);
					} else {
						List<Integer> buf = new ArrayList<>();
						buf.add(j);

						res.put(i, buf);
					}
				}
			}
		}
		
		return res;
	}
	

	public double[] multiThreadPagerank(int[][] mat, int n, int threadsCount){

		HashMap<Integer, List<Integer>> matrix = matrixReduce(mat, n);

		int num = n;
		
		double[] result = new double[n];
		for(int r = 0; r < n; n++)
			result[r] = 0;
		result = pagerank(mat, num);
		
		if(threadsCount < n)
			threadsCount = n;
		
		int count = n / threadsCount;
		int remain = n % threadsCount;
		MultiThreadPagerank[] threads = new MultiThreadPagerank[threadsCount];
		MultiThreadPagerank.setMatrix(matrix);
		MultiThreadPagerank.setN(num);
		int start = 0;
		
		for (int i = 0; i < threadsCount; i++) { 
            int length = ((i == 0) ? count + remain : count);
			
            threads[i] = new MultiThreadPagerank(start, length);
            threads[i].start();
			
			start += length;
        }
		
		try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
		
		for(MultiThreadPagerank thread : threads){
			double[] pg = new double[result.length];//thread.getPageranks();
			for(int i = 0; i < result.length; i++){
				result[i] += pg[i];
			}
		}
		
		return result;
	}
}
