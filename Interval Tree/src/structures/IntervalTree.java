package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public void sortIntervals(ArrayList<Interval> intervals, char lr) {
		// COMPLETE THIS METHOD
		if(lr=='l'){
			int length=intervals.size();
			int smallest=intervals.get(0).leftEndPoint;
			int indexofsmallest=0;
			for(int i=length;i>0;i--){
				smallest=intervals.get(0).leftEndPoint;
				indexofsmallest=0;
				for(int j=0;j<=i-1;j++){
					if(intervals.get(j).leftEndPoint<=smallest){
						smallest=intervals.get(j).leftEndPoint;
						indexofsmallest=j;
					}
				}
				Interval temp=intervals.remove(indexofsmallest);
				intervals.add(temp);
			}
		}
		else if(lr=='r'){
			int length=intervals.size();
			int smallest=intervals.get(0).rightEndPoint;
			int indexofsmallest=0;
			for(int i=length;i>0;i--){
				smallest=intervals.get(0).rightEndPoint;
				indexofsmallest=0;
				for(int j=0;j<=i-1;j++){
					if(intervals.get(j).rightEndPoint<=smallest){
						smallest=intervals.get(j).rightEndPoint;
						indexofsmallest=j;
					}
				}
				Interval temp=intervals.remove(indexofsmallest);
				intervals.add(temp);
			}
		}
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Integer> sortedLEndPoints=new ArrayList<Integer>();
		ArrayList<Integer> sortedREndPoints=new ArrayList<Integer>();
		ArrayList<Integer> sortedEndPoints=new ArrayList<Integer>();
		for(int i=0;i<=leftSortedIntervals.size()-1;i++){
			int value=leftSortedIntervals.get(i).leftEndPoint;
			if(sortedLEndPoints.contains(value)){
				continue;
			}
			else{
				sortedLEndPoints.add(value);
			}
		}
		for(int i=0;i<=rightSortedIntervals.size()-1;i++){
			int value=rightSortedIntervals.get(i).rightEndPoint;
			if(sortedREndPoints.contains(value)){
				continue;
			}
			else{
				sortedREndPoints.add(value);
			}
		}
		int i=0;
		int j=0;
		while(i<=sortedLEndPoints.size()-1&&j<=sortedREndPoints.size()-1){
			if(sortedLEndPoints.get(i)<sortedREndPoints.get(j)){
				if(sortedEndPoints.contains(sortedLEndPoints.get(i))){
					i++;
					continue;
				}
				else{
				sortedEndPoints.add(sortedLEndPoints.get(i));
				i++;
				}
			}
			else if(sortedLEndPoints.get(i)>sortedREndPoints.get(j)){
				if(sortedEndPoints.contains(sortedREndPoints.get(j))){
					j++;
					continue;
				}
				else{
				sortedEndPoints.add(sortedREndPoints.get(j));
				j++;
				}
			}
			else{
				if(sortedEndPoints.contains(sortedLEndPoints.get(i))){
					i++;
					j++;
					continue;
				}
				else{
				sortedEndPoints.add(sortedLEndPoints.get(i));
				i++;
				j++;
				}
			}
		}
		while(i<=sortedLEndPoints.size()-1){
			if(sortedEndPoints.contains(sortedLEndPoints.get(i))){
				i++;
				continue;
			}
			else{
				sortedEndPoints.add(sortedLEndPoints.get(i));
				i++;
			}
		}
		while(j<=sortedREndPoints.size()-1){
			if(sortedEndPoints.contains(sortedREndPoints.get(j))){
				j++;
				continue;
			}
			else{
				sortedEndPoints.add(sortedREndPoints.get(j));
				j++;
			}
		}
		return sortedEndPoints;
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		Queue<IntervalTreeNode> Q=new Queue<IntervalTreeNode>();
		for(int i=0;i<=endPoints.size()-1;i++){
			IntervalTreeNode p=new IntervalTreeNode(endPoints.get(i),endPoints.get(i),endPoints.get(i));
			Q.enqueue(p);
		}
		int s=Q.size;
		while(s>1){
		s=Q.size;
		int temp=s;
		while(temp>1){
			IntervalTreeNode T1=Q.dequeue();
			IntervalTreeNode T2=Q.dequeue();
			float v1=T1.maxSplitValue;
			float v2=T2.minSplitValue;
			IntervalTreeNode N=new IntervalTreeNode((v1+v2)/2,T1.minSplitValue,T2.maxSplitValue);
			N.leftChild=T1;
			N.rightChild=T2;
			Q.enqueue(N);
			temp=temp-2;
		}
		if(temp==1){
			Q.enqueue(Q.dequeue());
		}
		}
		return Q.dequeue();
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		IntervalTreeNode cur=root;
		for(int i=0;i<=leftSortedIntervals.size()-1;i++){
			 cur=root;
			while((cur.leftChild!=null&&cur.rightChild!=null)&&!leftSortedIntervals.get(i).contains(cur.splitValue)){
			if(leftSortedIntervals.get(i).leftEndPoint>cur.splitValue){
				cur=cur.rightChild;
			}
			else if(leftSortedIntervals.get(i).leftEndPoint<cur.splitValue){
				cur=cur.leftChild;
			}
			}
			if(cur.leftIntervals==null){
		    cur.leftIntervals=new ArrayList<Interval>();
			cur.leftIntervals.add(leftSortedIntervals.get(i));
			}
			else{
			cur.leftIntervals.add(leftSortedIntervals.get(i));
			}
		}
		
		for(int i=0;i<=rightSortedIntervals.size()-1;i++){
			 cur=root;
			while((cur.leftChild!=null&&cur.rightChild!=null)&&!rightSortedIntervals.get(i).contains(cur.splitValue)){
			if(rightSortedIntervals.get(i).leftEndPoint>cur.splitValue){
				cur=cur.rightChild;
			}
			else if(rightSortedIntervals.get(i).leftEndPoint<cur.splitValue){
				cur=cur.leftChild;
			}
			}
			if(cur.rightIntervals==null){
			cur.rightIntervals=new ArrayList<Interval>();
			cur.rightIntervals.add(rightSortedIntervals.get(i));
			}
			else{
			cur.rightIntervals.add(rightSortedIntervals.get(i));
			}
		}
	}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Interval> r=new ArrayList<Interval>();
		query(q,root,r);
		return r;
	}
	private void query(Interval q, IntervalTreeNode t, ArrayList<Interval> Result){
		if(t.leftChild==null&&t.rightChild==null){
			return;
		}
		if(t.leftIntervals==null||t.rightIntervals==null){
			return;
		}
		if(q.contains(t.splitValue)){
			Result.addAll(t.leftIntervals);
			query(q,t.leftChild,Result);
			query(q,t.rightChild,Result);
		}
		if(t.splitValue<q.leftEndPoint){
		    t.matchRight(q, Result);
		    query(q,t.rightChild,Result);
		}
		if(t.splitValue>q.rightEndPoint){
		    t.matchLeft(q, Result);
		    query(q,t.leftChild,Result);
		}
	}

}
