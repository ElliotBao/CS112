package apps;

import java.util.Scanner;
import structures.Graph;

import structures.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Driver {
	public static void main(String[] args) throws IOException
	{
		Graph graph = new Graph("graph3-2.txt");
		PartialTreeList ptl = MST.initialize(graph);
//		ArrayList<PartialTree.Arc> result = MST.execute(ptl);
		int x=ptl.size()-1;
		for(int i=0;i<=x;i++){
			System.out.println(ptl.remove());
		}
	}
}
