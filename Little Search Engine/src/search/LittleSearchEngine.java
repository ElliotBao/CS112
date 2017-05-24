package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		if(docFile==null){
			throw new FileNotFoundException();
		}
		HashMap<String,Occurrence> keywords=new HashMap<String,Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		while(sc.hasNext()){
			String line = sc.nextLine();
			if(line!=null && !(line.trim().isEmpty())){
				StringTokenizer st = new StringTokenizer(line, " ");
				while(st.hasMoreTokens()){
					String cur=getKeyWord(st.nextToken());
					if(cur!=null){
						if(keywords.containsKey(cur)){
							Occurrence oc = keywords.get(cur);
							oc.frequency++;
							keywords.put(cur, oc);
						}
						else{
							Occurrence oc = new Occurrence(docFile,1);
							keywords.put(cur, oc);
						}
					}
				}
			}
		}
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		String key; //the key of the occurrence 
		Occurrence value; // the corresponding occurrence
		Iterator iter = kws.keySet().iterator();
		while(iter.hasNext()){
			key = (String)iter.next();
			value = (Occurrence)kws.get(key);
			if(keywordsIndex.containsKey(key)){
				ArrayList<Occurrence> arr = keywordsIndex.get(key);
				arr.add(value);
				ArrayList<Integer> last = insertLastOccurrence(arr);
				ArrayList<Occurrence> cur = new ArrayList<Occurrence>();
				for(int i=0;i<arr.size()-1;i++){
					cur.add(arr.get(i));
				}
				if(cur.size()==1){
					if(cur.get(0).frequency>=value.frequency){
						cur.add(value);
					}
					else{
						cur.add(0, value);
					}
				}
				else if(last.get(last.size()-1)==cur.size()-1){
					if(cur.get(cur.size()-1).frequency>=value.frequency){
						cur.add(value);
					}
					else{
						cur.add(last.get(last.size()-1), value);
					}
				}
				else if(last.get(last.size()-1)==0){
					if(cur.get(0).frequency>=value.frequency){
						cur.add(1, value);
					}
					else{
						cur.add(0, value);
					}
				}
				else{
					if(cur.get(last.get(last.size()-1)).frequency>=value.frequency){
					cur.add(last.get(last.size()-1)+1,value);
					}
					else{
					cur.add(last.get(last.size()-1),value);
					}
				}
				keywordsIndex.remove(key);
				keywordsIndex.put(key, cur);
			}
			else{
				ArrayList<Occurrence> newOcc = new ArrayList<Occurrence>();
				newOcc.add(value);
				keywordsIndex.put(key, newOcc);
			}
			}
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		word=word.toLowerCase();
		if(noiseWords.get(word)!=null){
			return null;
		}
		int index = word.length()-1;
		for(int i=word.length()-1;i>=0;i--){
			if(word.charAt(i)>='a'&&word.charAt(i)<='z'){
				index=i;
				break;
			}
		}
		for(int i=index;i>=0;i--){
			if(!(word.charAt(i)>='a'&&word.charAt(i)<='z')){
				return null;
			}
		}
		if(noiseWords.get(word.substring(0, index+1))!=null){
			return null;
		}
		return word.substring(0, index+1);
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int last = occs.size()-1;
		ArrayList<Integer> occ = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i=0;i<=last-1;i++){
			occ.add(occs.get(i).frequency);
		}
		int key = occs.get(last).frequency;
		int min = 0;
		int max = occ.size()-1;
		while(min<=max){
			int mid = (min+max)/2;
			result.add(mid);
			if(occ.get(mid)>key){
				min = mid+1;
			}
			else if(occ.get(mid)<key){
				max = mid-1;
			}
			else{
				break;
			}
		}
		return result;
	}
	
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<Occurrence> list1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> list2 = keywordsIndex.get(kw2);
		ArrayList<String> result = new ArrayList<String>();
		if(list1==null&&list2==null){
			return null;
		}
		else if(list1!=null&&list2==null){
			int count=0;
			while(count<list1.size()&&count<5){
				result.add(list1.get(count).document);
				count++;
			}
		}
		else if(list1==null&&list2!=null){
			int count=0;
			while(count<list2.size()&&count<5){
				result.add(list2.get(count).document);
				count++;
			}
		}
		else{
			int i=0, j=0, count=0;
			while(((i<list1.size()||j<list2.size()))&&count<5){
				if(i>=list1.size()){
					if(!result.contains(list2.get(j).document)){
					result.add(list2.get(j).document);
					j++;
					count++;
					continue;
					}
					else{
					j++;
					continue;
					}
				}
				if(j>=list2.size()){
					if(!result.contains(list1.get(i).document)){
					result.add(list1.get(i).document);
					i++;
					count++;
					continue;
					}
					else{
					i++;
					continue;
					}
				}
				if(list1.get(i).frequency>list2.get(j).frequency&&!result.contains(list1.get(i).document)){
					result.add(list1.get(i).document);
					i++;
					count++;
				}
				else if(list1.get(i).frequency<list2.get(j).frequency&&!result.contains(list2.get(j).document)){
					result.add(list2.get(j).document);
					j++;
					count++;
				}
				else{
					if(!result.contains(list1.get(i).document)){
						result.add(list1.get(i).document);
						i++;
						count++;
					}
					else{
						i++;
					}
					if(!result.contains(list2.get(j).document)){
						result.add(list2.get(j).document);
						j++;
						count++;
					}
					else{
						j++;
					}
				}
			}
		}
		return result;
	}
	public static void main(String[] args) throws FileNotFoundException{
		LittleSearchEngine lse = new LittleSearchEngine();
		lse.makeIndex("docs.txt","noisewords.txt");
		System.out.println(lse.getKeyWord("test-case"));
		//ArrayList<String> r = lse.top5search("bus", "car");
		//for(int i=0;i<=r.size()-1;i++){
			//System.out.println(r.get(i));
		//}
		/*ArrayList<Occurrence> list1 = new ArrayList<Occurrence>();
		list1.add(new Occurrence("a",18));
		list1.add(new Occurrence("b",5));
		list1.add(new Occurrence("c",2));
		list1.add(new Occurrence("d",2));
		list1.add(new Occurrence("e",1));
		list1.add(new Occurrence("f",9));
		ArrayList<Integer> r = lse.insertLastOccurrence(list1);
		for(int i=0;i<=r.size()-1;i++){
			System.out.println(r.get(i));
		}*/
		/*ArrayList<Occurrence> r = lse.keywordsIndex.get("either");
		for(int i=0;i<=r.size()-1;i++){
			System.out.println(r.get(i));
		}*/
		/*HashMap<String,Occurrence> h = lse.loadKeyWords("ron.txt");
		System.out.println(h.get("either"));*/
	}
}
