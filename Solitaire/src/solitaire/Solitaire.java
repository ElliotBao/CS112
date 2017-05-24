package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		// COMPLETE THIS METHOD
		CardNode current=deckRear.next;
		CardNode prev=deckRear;
		while(current.cardValue!=27){
			current=current.next;
			prev=prev.next;
		}
		CardNode next=current.next;
		if(deckRear.cardValue==27){
			prev.next=next;
			current.next=next.next;
			next.next=current;
			deckRear=next;
		}
		else if(current.next.cardValue==deckRear.cardValue){
			prev.next=next;
			current.next=next.next;
			next.next=current;
			deckRear=current;
		}
		else{
		prev.next=next;
		current.next=next.next;
		next.next=current;
		}
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
	    // COMPLETE THIS METHOD
		CardNode current=deckRear;
		CardNode prev=null;
		do{
			prev=current;
			current=current.next;
		}while(current.cardValue!=28&&current!=deckRear);
		CardNode temp1=current.next;
		CardNode temp2=current.next.next;
		if(deckRear.cardValue==28){
			deckRear=temp1;
		}
		else if(current.next.cardValue==deckRear.cardValue){
			deckRear=temp2;
		}
		else if(current.next.next.cardValue==deckRear.cardValue){
			deckRear=current;
		}
		current.next=temp2.next;
		prev.next=temp1;
		temp2.next=current;
		
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		// COMPLETE THIS METHOD
		CardNode current=deckRear.next;
		CardNode prev=null;
		if(deckRear.next.cardValue==27||deckRear.next.cardValue==28){
			if(deckRear.cardValue==27||deckRear.cardValue==28){
				return;
			}
			else{
				do{
					current=current.next;
				}while(current.cardValue!=28&&current.cardValue!=27);
				deckRear=current;
			}
		}
		else if(deckRear.cardValue==27||deckRear.cardValue==28){
			do{
				prev=current;
				current=current.next;
			}while(current.cardValue!=28&&current.cardValue!=27);
			deckRear=prev;
		}
		else{
			do{
				prev=current;
				current=current.next;
			}while(current.cardValue!=28&&current.cardValue!=27);
			CardNode current2=current;
			CardNode next=current2.next;
			do{
				next=next.next;
				current2=current2.next;
			}while(current2.cardValue!=28&&current2.cardValue!=27);
			CardNode first=deckRear.next;
			if(next.cardValue==deckRear.cardValue){
				prev.next=next;
				next.next=current;
				current2.next=first;
				deckRear=prev;
			}
			else{
				prev.next=next;
				deckRear.next=current;
				current2.next=first;
				deckRear=prev;
			}
		}
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {		
		// COMPLETE THIS METHOD
		if(deckRear.cardValue==28||deckRear.cardValue==27){
			return;
		}
		CardNode current=deckRear;
		CardNode first=deckRear.next;
		CardNode prevlast=deckRear;
		int count=0;
		do{
			prevlast=prevlast.next;
		}while(prevlast.next.cardValue!=deckRear.cardValue);
		do{
			current=current.next;
			count++;
		}while(count<deckRear.cardValue);
		deckRear.next=current.next;
		current.next=deckRear;
		prevlast.next=first;
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int cardnumber;
		if(deckRear.next.cardValue==28){
			cardnumber=27;
		}
		else{
			cardnumber=deckRear.next.cardValue;
		}
		int count=1;
		CardNode current=deckRear.next;
		while(current.cardValue!=deckRear.cardValue){ 
			if(count==cardnumber){
				if(current.next.cardValue==27||current.next.cardValue==28){
					jokerA();
					jokerB();
					tripleCut();
					countCut();
					current=deckRear;
					count=0;
					cardnumber=deckRear.next.cardValue;
					if(deckRear.next.cardValue==28){
						cardnumber=27;
					}
				}
				else{
					int key=current.next.cardValue;
					return key;
				}
			}
			current=current.next;
			count++;
		}
		
	   
		return -1;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String msg="";
		for(int i=0;i<=message.length()-1;i++){
			if(!Character.isLetter(message.charAt(i))){
				continue;
			}
			else{
				char letter=Character.toUpperCase(message.charAt(i));
				int alphaInt=letter-'A'+1;
				jokerA();
				jokerB();
				tripleCut();
				countCut();
				
				int key=getKey();
				int sum=alphaInt+key;
				if(sum>26){
					sum=sum-26;
				}
				letter=(char)(sum-1+'A');
				msg=msg+letter;
			}
		}
		
		return msg;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String msg="";
		for(int i=0;i<=message.length()-1;i++){
			char letter=Character.toUpperCase(message.charAt(i));
			int alphaInt=letter-'A'+1;
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			
			int key=getKey();
			int sum=alphaInt-key;
			if(sum<0){
				sum=sum+26;
			}
			letter=(char)(sum-1+'A');
			msg=msg+letter;
		}
		return msg;
	    
	}
}
