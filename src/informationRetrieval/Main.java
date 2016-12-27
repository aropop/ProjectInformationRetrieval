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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {

		  

	  /** Index all text files under a directory. */
	  public void index(String indexPath, Path docDir) {
	    Date start = new Date();
	    try {
	      System.out.println("Indexing to directory '" + indexPath + "'...");

	      Directory dir = FSDirectory.open(Paths.get(indexPath));
	      Analyzer analyzer = new StandardAnalyzer();
	      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

	      iwc.setOpenMode(OpenMode.CREATE);


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
	  
	  private void indexDocs(final IndexWriter writer, Path path) throws IOException {
	    if (Files.isDirectory(path)) {
	      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	          try {
	            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
	          } catch (IOException ignore) {
	            // don't index files that can't be read.
	          }
	          return FileVisitResult.CONTINUE;
	        }
	      });
	    } else {
	      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
	    }
	  }

	  /** Indexes a single document */
	  static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
	    try (InputStream stream = Files.newInputStream(file)) {
	      // make a new, empty document
	      Document doc = new Document();
	      
	      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
	      doc.add(pathField);
	      
	      doc.add(new LongPoint("modified", lastModified));

	      doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

          System.out.println("adding " + file);
          writer.addDocument(doc);
	    }
	  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
