import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Main
{
	public static void main(String[] args) throws IOException{	
	
		
		Search search = new Search();
		Pair<int[][], List<String>> result = search.getMatrix("http://www.gazeta.ru/", 9);
		int[][] matrix = result.getElement0();
 		for(Map.Entry<Integer, List<Integer>> entry : search.matrixReduce(matrix, matrix.length).entrySet()){
			System.out.println(entry.getKey());
			entry.getValue().forEach(System.out::println);
			System.out.println();
		}
		for(double d : search.multiThreadPagerank(matrix, matrix.length, 1))
			System.out.println(d);


    }
}
