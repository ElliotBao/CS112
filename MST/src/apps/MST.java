package apps;

import structures.*;

import java.io.IOException;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		/* COMPLETE THIS METHOD */
		PartialTreeList PTL = new PartialTreeList();
		for(int i=0;i<=graph.vertices.length-1;i++){
			Vertex v = graph.vertices[i];
			PartialTree pt = new PartialTree(v);
			Vertex.Neighbor ngh = v.neighbors;
			MinHeap<PartialTree.Arc> arc = pt.getArcs();
			while(ngh!=null){
				PartialTree.Arc temparc = new PartialTree.Arc(v, ngh.vertex, ngh.weight);
				arc.insert(temparc);
				ngh = ngh.next;
			}
			PTL.append(pt);
		}
		return PTL;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		/* COMPLETE THIS METHOD */
		ArrayList<PartialTree.Arc> result = new ArrayList<PartialTree.Arc>();
		while(ptlist.size()>1){
			PartialTree PTX = ptlist.remove();
			Vertex V2 = PTX.getArcs().getMin().v2;
			PartialTree.Arc PQX = PTX.getArcs().getMin();
			while(findvertex(V2, PTX)==true){
				PQX = PTX.getArcs().deleteMin();
				V2 = PTX.getArcs().getMin().v2;
			}
			PQX = PTX.getArcs().deleteMin();
			PartialTree PTY = ptlist.removeTreeContaining(PQX.v2);
			PTX.merge(PTY);
			result.add(PQX);
			ptlist.append(PTX);
		}
		return result;
	}
	
	private static boolean findvertex(Vertex v, PartialTree PTX){
		while(v!=null){
			if(v==PTX.getRoot()){
				return true;
			}
			if(v==v.parent){
				return false;
			}
			v = v.parent;
		}
		return false;
	}
}
