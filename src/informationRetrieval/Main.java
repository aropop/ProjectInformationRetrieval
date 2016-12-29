package informationRetrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.commons.codec.language.Soundex;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {

		  

	  /** Index all text files under a directory. */
	  public static void index(String indexPath, Path docDir, boolean useSoundex) {
	    Date start = new Date();
	    try {
	      System.out.println("Indexing to directory '" + indexPath + "'...");

	      Directory dir = FSDirectory.open(Paths.get(indexPath));
	      Analyzer analyzer = (useSoundex ? getSoundexAnalyzer() : new StandardAnalyzer());
	      
	      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

	      iwc.setOpenMode(OpenMode.CREATE);
	      //iwc.setSimilarity();
	      
	      
	      IndexWriter writer = new IndexWriter(dir, iwc);
	      indexDocs(writer, docDir);

	      writer.close();

	      Date end = new Date();
	      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	    } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	    }
	  }
	  
	  private static Analyzer getSoundexAnalyzer() {   // See http://stackoverflow.com/questions/38599692/how-to-implement-a-phonetic-search-using-lucene
		  return new Analyzer() {
  		    @Override
  		    protected TokenStreamComponents createComponents(String fieldName) {
  		        Tokenizer tokenizer = new StandardTokenizer();
  		        TokenStream stream = new PhoneticFilter(tokenizer, new Soundex(), false);
  		        return new TokenStreamComponents(tokenizer, stream);
  		    }
  		};
	  }
	  
	  private static void indexDocs(final IndexWriter writer, Path path) throws IOException {
	      if (Files.isDirectory(path)) {
		      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		          @Override
		          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		              try {
		                  indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
		              } catch (IOException ignore) {
		                  System.out.println("IO error on adding file");
		              }
		              return FileVisitResult.CONTINUE;
		          }
		      });
		  } else {
		      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		  }
	  }

	  /** Indexes a single document */
	  private static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
	    try (InputStream stream = Files.newInputStream(file)) {
	      // make a new, empty document
	      Document doc = new Document();
	      
	      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
	      doc.add(pathField);
	      
	      doc.add(new LongPoint("modified", lastModified));

	      // Add document statistics for cosine calculation
	      FieldType fieldType = new FieldType();
          fieldType.setIndexOptions(fieldType.indexOptions().DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
          fieldType.setStored(true);
          fieldType.setStoreTermVectors(true);
          fieldType.setTokenized(true);
	      doc.add(new Field("contents", getAllText(file), fieldType));

          System.out.println("adding " + file);
          writer.addDocument(doc);
	    }
	  }
	  
	  public static String getAllText(Path f) { // TODO make http://stackoverflow.com/questions/12576119/lucene-indexing-of-html-files
	        String textFileContent = "";

	        try {
				for (String line : Files.readAllLines(f)) {
				    textFileContent += line;
				}
			} catch (IOException e) {
			}
	        return textFileContent;
	    }
	  
	  /**
	   * 
	   * Query syntax see http://lucene.apache.org/core/6_3_0/queryparser/index.html
	   * 
	   * @param query
	   * @param indexPath
	   */
	  private static void search(String queryStr, String indexPath, boolean useSoundex) {
		  IndexReader reader;
		  try {
			  reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			  IndexSearcher searcher = new IndexSearcher(reader);
			  Analyzer analyzer = (useSoundex ? getSoundexAnalyzer() : new StandardAnalyzer());
			  QueryParser parser = new QueryParser("contents", analyzer);
			  Query query = parser.parse(queryStr);
			  TopDocs res = searcher.search(query, 100);
			  ScoreDoc[] hits = res.scoreDocs;
		      
		      for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				float score = hits[i].score;
				String path = doc.get("path");
				if (path != null) {
				  System.out.println((i+1) + ". " + score + " -> " + path);
				  String title = doc.get("title");
				  if (title != null) {
				    System.out.println("   Title: " + doc.get("title"));
				  }
				} else {
				  System.out.println((i+1) + ". " + "No path for this document");
				}
		      }
		  } catch (IOException e) {
			  System.out.println("IO error: " + e.getMessage());
			  e.printStackTrace();
		  } catch (ParseException e) {
			  System.out.println("Parser error: " + e.getMessage());
			  e.printStackTrace();
		}
	  }

	public static void main(String[] args) {
		if(args[0].equals("index")) {
			index(args[2], Paths.get(args[1]), args.length > 3 && args[3].equals("true"));
		} else if(args[0].equals("search")) {
			search(args[1], args[2], args.length > 3 && args[3].equals("true"));
		}

	}

}
