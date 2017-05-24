package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    	arrays=new ArrayList<ArraySymbol>();
    	scalars=new ArrayList<ScalarSymbol>();
       String temp=expr;
       StringTokenizer st=new StringTokenizer(temp,delims);
       while(st.hasMoreTokens()){
    	   String str=st.nextToken();
    	   int index=temp.indexOf(str)+str.length()-1;
    			   if(index+1<=temp.length()-1&&temp.charAt(index+1)=='['){
    		   ArraySymbol current=new ArraySymbol(str);
    		   if(arrays.contains(current)){
    			   continue;
    		   }
    		   else{
    		   arrays.add(current);
    		   }
    	   }
    	   else{
    		   if(Character.isLetter(str.charAt(str.length()-1))){
    			   ScalarSymbol current1=new ScalarSymbol(str);
    			   if(scalars.contains(current1)){
    				   continue;
    			   }
    			   else{
    			   scalars.add(current1);
    			   }
    		   }
    		   else{
    			   continue;
    		   }
    	   }
       }
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		// following line just a placeholder for compilation
    	expr=expr.replace(" ", "");
    	return basicop(reev(expr));
    }
    private String reev(String str){
    	for(int i=0;i<=str.length()-1;i++){
    		if(str.charAt(i)=='('||str.charAt(i)=='['){
    			int j=i;
    			Stack<Integer> stk=new Stack<Integer>();
    			stk.push(i);
    		    do{
    		    	j++;
    		    	if(str.charAt(j)=='['||str.charAt(j)=='('){
    		    		stk.push(j);
    		    	}
    		    	if(str.charAt(j)==']'||str.charAt(j)==')'){
    		    		stk.pop();
    		    	}
    		    }while(!stk.isEmpty());
    			//find anothe bracket
    			if(str.charAt(i)=='('){
    				if(j<str.length()-1){
    				return str.substring(0, i)+Float.toString(basicop(reev(str.substring(i+1,j))))+reev(str.substring(j+1));}
    				else{
    					return str.substring(0, i)+Float.toString(basicop(reev(str.substring(i+1,j))));
    				}
    			}
    			else if(str.charAt(i)=='['){
    				int index=i;
    				for(int k=i;k>=0;k--){
    					if(k==0){
    						index=k;
    					}
    					if(str.charAt(k)=='+'||str.charAt(k)=='-'||str.charAt(k)=='*'||str.charAt(k)=='/'){
    						index=k+1;
    						break;
    					}
    				}
    				// find the beginning of of the array 
    				if(j<str.length()-1){
        				return str.substring(0, index)+Integer.toString(getavalue(str.substring(index,i+1),(int)basicop(reev(str.substring(i+1,j)))))+reev(str.substring(j+1));}
        				else{
        					return str.substring(0, index)+Integer.toString(getavalue(str.substring(index,i+1),(int)basicop(reev(str.substring(i+1,j)))));
        				}
    			}
    		}
    	}
    	if(str.charAt(0)=='+'||str.charAt(0)=='-'||str.charAt(0)=='*'||str.charAt(0)=='/'){
    		return str;
    	}
    	return Float.toString(basicop(str));
    }
    private float basicop(String str){
    	float total=0;
    	StringTokenizer st=new StringTokenizer(str, "+");
    	while(st.hasMoreTokens()){
    		String temp=st.nextToken();
    		int f=0, sum=0;
    		for(int i=0;i<=temp.length()-1;i++){
    			if(i!=0&&(temp.charAt(i)=='-'&&(temp.charAt(i-1)!='*'&&temp.charAt(i-1)!='/'))){
    				if(f==0){
    					sum+=product(temp.substring(f, i));
    					f=i+1;
    				}
    				else{
    					sum-=product(temp.substring(f, i));
    					f=i+1;
    				}
    			}
    			if(i==temp.length()-1){
    				if(f==0){
    					sum+=product(temp.substring(f, i+1));
    					f=i+1;
    				}
    				else{
    					sum-=product(temp.substring(f, i+1));
    					f=i+1;
    				}
    			}
    		}
    		total+=sum;
    	}
    	return total;
    }

    	
    	
    private float getsvalue(String str){
    	if(Character.isLetter(str.charAt(str.length()-1))){
    		ScalarSymbol ssl=new ScalarSymbol(str);
    		int indexssl=scalars.indexOf(ssl);
    		int value=scalars.get(indexssl).value;
    		return (float)value;
    	}
    	else{
    		return Float.parseFloat(str);
    	}
    }	
    private int getavalue(String str, int i){
    	str=str.substring(0,str.indexOf('['));
    	ArraySymbol asl=new ArraySymbol(str);
    	int indexasl=arrays.indexOf(asl);
    	return arrays.get(indexasl).values[i];
    }
    private float product(String str){
    	float result=1;
    	float sum=1;
    	StringTokenizer st=new StringTokenizer(str,"/");
    	String y=st.nextToken();
    	StringTokenizer sty=new StringTokenizer(y,"*");
    	while(sty.hasMoreTokens()){
			sum*=getsvalue(sty.nextToken());
		}
    	result=sum;
    	while(st.hasMoreTokens()){
    		float pdt=1;
    		String x=st.nextToken();
    		StringTokenizer stx=new StringTokenizer(x,"*");
    		while(stx.hasMoreTokens()){
    			pdt*=getsvalue(stx.nextToken());
    		}
    		result/=pdt;
    	}
    	return result;
    }
 
    
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }
}
