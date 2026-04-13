package com.miniinstagram.search.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import org.apache.lucene.search.*;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class LuceneManager {

    private static final String INDEX_DIR =
            "/Users/shifa-pt8239/lucene_index_insta";

    private static IndexWriter writer;

    static {
        try {

            Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

            IndexWriterConfig config =
                    new IndexWriterConfig(new StandardAnalyzer());

            writer = new IndexWriter(dir, config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexUser(String id, String username, String description)
            throws Exception {

        Document doc = new Document();

        doc.add(new StringField("id", id, Field.Store.YES));
        doc.add(new TextField("username", username, Field.Store.YES));
        doc.add(new TextField("description", description, Field.Store.YES));

        writer.updateDocument(new Term("id", id), doc);

        writer.commit();

        System.out.println("User indexed: " + username);
    }
    public static java.util.List<String> searchUsers(String queryStr) throws Exception {

        java.util.List<String> results = new java.util.ArrayList<>();

        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        DirectoryReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);

        org.apache.lucene.queryparser.classic.QueryParser parser =
                new org.apache.lucene.queryparser.classic.QueryParser(
                        "username",
                        new StandardAnalyzer()
                );

        //Query query = parser.parse(queryStr + "*");
        
        Query query = parser.parse(QueryParser.escape(queryStr.toLowerCase()) + "*");
        
        TopDocs docs = searcher.search(query, 10);

        for (ScoreDoc sd : docs.scoreDocs) {

            //Document doc = searcher.doc(sd.doc);
        	org.apache.lucene.document.Document doc = searcher.storedFields().document(sd.doc);

        	results.add(doc.get("id")); 
            //results.add(doc.get("username"));
        }

        reader.close();

        return results;
    }
}