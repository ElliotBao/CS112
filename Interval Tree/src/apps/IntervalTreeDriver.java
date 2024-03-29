package apps;

import structures.*;

import java.io.*;
import java.util.*;

public class IntervalTreeDriver {
	
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) throws IOException {
		System.out.print("Enter intervals file name => ");
		String infile = keyboard.readLine();
		BufferedReader br = new BufferedReader(new FileReader(infile));
		ArrayList<Interval> intervals = readIntervals(br);
		System.out.println("Read the following intervals:");
		for (Interval interval: intervals) {
			System.out.println(interval);
		}
		ArrayList<Interval> interval1=intervals;
		ArrayList<Interval> interval2=intervals;
		IntervalTree tree = new IntervalTree(intervals);
		tree.sortIntervals(interval1, 'l');
		tree.sortIntervals(interval2, 'r');
		System.out.println(tree.getSortedEndPoints(interval1, interval2));
		//performQueries(tree);
		
	}
	
	static ArrayList<Interval> readIntervals(BufferedReader br) throws IOException  {
		String line;
		ArrayList<Interval> ret = new ArrayList<Interval>();
		
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			Interval intrvl = new Interval(
					Integer.parseInt(st.nextToken()), 
					Integer.parseInt(st.nextToken()), st.nextToken());
			ret.add(intrvl);
		}
		return ret;
	}
	
	static void performQueries(IntervalTree tree) throws IOException {
		System.out.print("\nEnter an interval (e.g. 3 5) to intersect, quit to stop => ");
		String schedule = keyboard.readLine();
		while (!schedule.equals("quit")) {
			StringTokenizer st = new StringTokenizer(schedule);
			
			Interval intrvl = new Interval(Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()), "");
					
			ArrayList<Interval> intersects = tree.findIntersectingIntervals(intrvl);
			for (Interval interval: intersects) {
				System.out.println(interval);
			}
			
			System.out.print("\nEnter an interval (e.g. 3 5) to intersect, quit to stop => ");	    
			schedule = keyboard.readLine();
		}
	}
}
