package DAL;

import BE.Document;
import BE.Position;
import BE.Term;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.HashSet;

public class DatabaseGateway {

    private static DatabaseGateway instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private Morphia morphia = new Morphia();
    private Datastore datastore;

    public static DatabaseGateway getInstance(){
        if(instance == null){
            instance = new DatabaseGateway();
        }
        return instance;
    }

    public DatabaseGateway(){
        morphia.mapPackage("BE");
        MongoClient client = new MongoClient();
        client.dropDatabase("SearchEngineV2");
        datastore = morphia.createDatastore(client, "SearchEngineV2");
        datastore.ensureIndexes();
    }


    public void saveDocument(Document doc){
        datastore.save(doc);
    }

    public void saveTerms(HashSet<Term> terms){
        datastore.save(terms);
    }

    public void saveTerm(Term term){
        datastore.save(term);
    }

    public void savePosition(Position position){
        datastore.save(position);
    }
}
