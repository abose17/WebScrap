package cs833FinalProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class PageRank{
    static ArrayList<String> vocabulary = new ArrayList<String>();
	static ArrayList<String> word_store = new ArrayList<String>();
	static ArrayList<String> StopWord = new ArrayList<String>();
	static ArrayList<String> queryList = new ArrayList<String>();
	static Map<String, Double> storeIdf = new HashMap<String, Double>();
	static Map<String, ArrayList<String>> docsave= new HashMap<String, ArrayList<String>>();
	static Map<String, Map<String, Integer>> tfList= new HashMap<String, Map<String, Integer>>();
	static Map<String, Integer> entryDf = new HashMap<String, Integer>();
	static Map<String, Integer> entryQf = new HashMap<String, Integer>();
	static Map<String, Double> docLength = new HashMap<String, Double>();
	static Map<String, Double> cosineProd = new HashMap<String, Double>();
	static String[] split_sp;
	static BufferedReader buf = null;
	static String temp="", store="";
	private static Porter stemmer = new Porter();
	static Scanner scan =  new Scanner(System.in);
	
	
	 static ArrayList<String> parse(String reservoir, Boolean QC)
	 {
		 ArrayList<String> docword = new ArrayList<String>();
		 ArrayList<String> query_word = new ArrayList<String>();
		 split_sp= reservoir.split(" ");
			for(int i=0;i<split_sp.length;i++ )
			{
					String cleaned_word = split_sp[i].toLowerCase().replaceAll("\\p{Punct}", "").trim();
					//System.out.println("split_sp.length "+split_sp.length);
					boolean skip_word =false; 
					
					if (cleaned_word.trim().equalsIgnoreCase("")) {
						skip_word = true;
					}
					
					if(skip_word == false)
					{
						for(int l=0; l<StopWord.size();l++)
						{
							if (StopWord.get(l).equalsIgnoreCase(cleaned_word)) {
								skip_word = true;
							}
						}
						
						cleaned_word = stemmer.stripAffixes(cleaned_word);
					}								
					
				if (!skip_word && QC==true) {
						//word_store.add(cleaned_word);
						docword.add(cleaned_word);
						if(!vocabulary.contains(cleaned_word))
						{
							vocabulary.add(cleaned_word);
						}
					}
				if (!skip_word && QC==false) {
					query_word.add(cleaned_word);
				}
					//System.out.println(word_store.get(i));
			}
			if(QC==true)
				return docword;
			else
				return query_word;
	 }
	 
	 
	 	//static void stopWordStore(String stopPath) {
	 	static void stopWordStore() {
	 		try{
	 			BufferedReader stop_bf = new BufferedReader(new FileReader("/media/abose/741EEDCC1CBD91DE/Information_Retrieval/CIS833HW2/stopwords.txt"));
	 			String sc="";
			
		    //For the StopWord
			
				while(sc != null) {
					sc=stop_bf.readLine();	
					if (sc!= null ) {
						StopWord.add(sc);
						//System.out.println("sc  "+ sc);
					}
				}
			System.out.println("Stopword Number "+ StopWord.size());
			stop_bf.close();
			
			}catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 		catch(IOException e2) {
				e2.printStackTrace();
			}
	 	}
	 	
	 	static void queryProcess() {
	 	//void queryProcess(String str) {//not called from main function
	 		String query="";
	 		System.out.println("Enter The Query");
			query=scan.nextLine();
			queryList=parse(query, false);
			System.out.println("Query LIst  "+ queryList);
	 	}
	 	
	 	
	 	static void textProcessor(String store, String URLrec)
	 	{
	 		//try {
	 		//for(int fd = 0; fd < fileArryRcv.length; fd++)
				//{
					ArrayList<String> docwordFuncRet = new ArrayList<String>();
					//if (fileArryRcv[fd].isFile()) {
				        //System.out.println(file_read[fd].getName());
						
				        //buf = new BufferedReader(new FileReader(fileArryRcv[fd]));
				        //buf = new BufferedReader(new FileReader(dir));
					 
				        //For the Html Doc
							/*while((temp=buf.readLine()) != null)
							{
					    	Document doc = Jsoup.parse(temp);
					    	String text = doc.body().text();
					    	store=store+text+ " ";
					    	//System.out.println(store);
							}*/
						//System.out.println(store);				
						docwordFuncRet=parse(store, true);
						//store="";
						//for(int j=0;j<docword.size();j++)
							//System.out.println("Doc Word "+ docword.get(j));
						System.out.println("Word Store "+ word_store.size() + ", Doc Word "+ docwordFuncRet.size()+ ", Voc Word "+ vocabulary.size());
						docsave.put(URLrec, docwordFuncRet);
						//System.out.println("docsave Size "+ docsave.size());
				    //}
					
					//docword.clear();
				//}
						/*}catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}*/
	 	}
	 	
	 	
		public static void pageRankmain() {
			
			//String query="";
			//File dir = new File("/media/abose/741EEDCC1CBD91DE/Information_Retrieval/CIS833HW2/cranfieldDocs");
			
			stopWordStore();
			//File[] file_read= dir.listFiles();
			//textProcessor(file_read);// Will be called from LinkCrawler
			//queryProcess();
			//System.out.println("docsave "+ docsave);
			/*for ( Map.Entry<String, ArrayList<String>> entry : docsave.entrySet()) {
			    String key = entry.getKey();
			    List<String> values = entry.getValue();
			    System.out.print("Key = " + key);
			    System.out.println(" , Values = "+ values);
			}*/
			//########## RESPOINSIBLE FOR CREATING INVERTED INDEX OF TERMS ##########
			for(int i=0;i<vocabulary.size();i++) {
				int count=1;
				Map<String, Integer> nestMap = new HashMap<String, Integer>();
				for ( Map.Entry<String, ArrayList<String>> entry : docsave.entrySet()) {
				    String keyMain = entry.getKey();
				    List<String> values = entry.getValue();
				    if(values.contains(vocabulary.get(i)))
				    	{
				    	entryDf.put(vocabulary.get(i), count);//entryDf stores documents frequency of vocabulary word \\it increase the count value for specific key
				    	count++;
				    	}
				   
				    nestMap.put(keyMain, Collections.frequency(values,vocabulary.get(i)));
			    	
			    	//System.out.println("VOC "+ vocabulary.get(i)+ " KeyMain " +keyMain+ " "+ Collections.frequency(values,vocabulary.get(i)));
					}
					tfList.put(vocabulary.get(i), nestMap);
			}
			//########## RESPOINSIBLE FOR CREATING A MAP "storeIdf" TO STORE IDF VALUES OF TERMS ##########
			for ( Map.Entry<String, Integer> endf : entryDf.entrySet()) {
				String keydf = endf.getKey();
				int valdf = endf.getValue();
				//System.out.print("Key = " +  "'"+keydf+ "'");
			    //System.out.print(" , Values = "+ valdf);
			    double Hudai = (double) (docsave.size())/valdf;
			    //System.out.println("docsave size "+docsave.size()+ " valdf "+ valdf + " Hudai "+ Hudai+  " log Value1 "+ Math.log(Hudai)+ " log Value2 "+ Math.log(2));
			    double idf= Math.log(Hudai)/Math.log(2);
			    storeIdf.put(keydf, idf);
			    //System.out.print("  idf-> "+ idf);
			    //System.out.println();
			} 
			
			//############ Just for Printing ##########NO WORK HERE########
			for (Map.Entry<String, Map<String, Integer>> tokTf : tfList.entrySet()) {
				String keyTf = tokTf.getKey();
				Map<String, Integer> valTF = tokTf.getValue();
				System.out.println("Inverted Indexing by Key Word = " + "'"+keyTf+ "'");
				for (Map.Entry<String, Integer> nesTf : valTF.entrySet()) {
					String keyTF = nesTf.getKey();
					Integer valTf = nesTf.getValue();
					System.out.print(" Document Consists This Key Word = " + "'"+ keyTF+ "'");
					System.out.println(" , Term Frequencies in This Doc = "+ valTf);
				}    
			}
			//########### FOR CALCULATING DOCUMENT LENTH  #############//
			for ( Map.Entry<String, ArrayList<String>> entry_new : docsave.entrySet()) // Iterating Number of Documents
			{
				String keyMain_new = entry_new.getKey();
				//int countStop=0;
				double sum=0;
				 for(Map.Entry<String, Map<String, Integer>> tokTf_new : tfList.entrySet()) // Iterating through the vocabulary
				 { 
					 Map<String, Integer> valTF_new = tokTf_new.getValue();
					 for (Map.Entry<String, Integer> nesTf_new : valTF_new.entrySet()) // Iterating Through the Documents
					 {
						 if(keyMain_new.equals(nesTf_new.getKey())) // Only doc name EQUAL with docsave name can enter here
						 {
							 double val = nesTf_new.getValue()* (Double) storeIdf.get(tokTf_new.getKey());
							 sum = sum+ Math.pow(val, 2);
						 }
					 }
					 
					 //countStop++;
				 }
				 docLength.put(keyMain_new, Math.sqrt(sum));
				 sum=0;
			}
			
			
			System.out.println("Document Length "+ docLength);
			//System.out.println("tfList "+tfList);
			
			    //########### FOR CALCULATING QUERY LENTH  #############///
				double qrySum=0;
				 for(String qryTerm: queryList) // Iterating through the vocabulary
				 {
					//entryQf.put(qryTerm, Collections.frequency(queryList,qryTerm));// VUl ase
					 
						 if(storeIdf.get(qryTerm) != null) 
						 {
							 entryQf.put(qryTerm, Collections.frequency(queryList,qryTerm));
							 double val = entryQf.get(qryTerm)* (Double) storeIdf.get(qryTerm);
							 qrySum = qrySum+ Math.pow(val, 2);
						 }
					 System.out.println(qryTerm+" "+entryQf.get(qryTerm)+ " "+ (Double) storeIdf.get(qryTerm));
				 }
				 qrySum=Math.sqrt(qrySum);
				 System.out.println("qrySum " + qrySum);
				 
				//########### FOR CALCULATING COSINE SIMILARITY  #############///
				 for ( Map.Entry<String, ArrayList<String>> entry_dotP : docsave.entrySet()) // Iterating Number of Documents
					{
						double sumProduct=0;
						double productTfIdf=0;
						String keyMain_new = entry_dotP.getKey(); //Geting Doc Name
						 for(Map.Entry<String, Integer> qryTermItr : entryQf.entrySet()) // Iterating through the vocabulary
						 {
							 
							 Map<String, Integer> matchTerm = tfList.get(qryTermItr.getKey()); // Getting a map of doc Names by a Query Term as value of tfList

								 if(matchTerm.containsKey(keyMain_new)) // Only doc name EQUAL with docsave name can enter here
								 {
									 //System.out.println("Test "+ matchTerm.get(keyMain_new) +" keyMain_new " + keyMain_new);
									  double docTfIdf= matchTerm.get(keyMain_new) * storeIdf.get(qryTermItr.getKey());
									  double qryTfIdf= qryTermItr.getValue() * storeIdf.get(qryTermItr.getKey());
									  productTfIdf = docTfIdf * qryTfIdf;
								 }
							 
							 sumProduct= sumProduct+productTfIdf;
							 //System.out.println("productTfIdf "+productTfIdf+" sumProduct "+ sumProduct);
							 productTfIdf=0;
						 }
						 cosineProd.put(entry_dotP.getKey(), sumProduct/(docLength.get(entry_dotP.getKey())*qrySum));
						 sumProduct=0;
						 //System.out.println(entry_dotP.getKey()+ " " + docLength.get(entry_dotP.getKey()));
					}
				 System.out.println("cosineProd "+ cosineProd);
				 
				 System.out.println("Number of Top Pages you want to see");
				 int topRank = Integer.parseInt(scan.nextLine());
				 Map<String, Double> result = cosineProd.entrySet().stream()
			                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(topRank)
			                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
			                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

				    scan.close();
			        //Alternative way
			        //Map<String, Double> result2 = new LinkedHashMap<>();
			        //cosineProd.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(2).forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));

			        System.out.println("Sorted...");
			        System.out.println(result);
			        //System.out.println(result2);
	}
}