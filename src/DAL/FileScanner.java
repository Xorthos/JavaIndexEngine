package DAL;

import BE.Document;
import BE.Position;
import BE.Term;
import Code.IndexForm;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class FileScanner {

    private  HashSet<String> commonWords = new HashSet<>();
    private  IndexForm form;
    private  int filesIndexed = 0;
    private  HashMap<String, Term> terms = new HashMap<>();
    private  long newTermId = 0;
    private long newDocId = 0;
    private long currentTermId;
    private  long positionCount = 0;
    private  boolean last = false;
    private int files = 0;

    public FileScanner(IndexForm form) {

        loadCommonWords();
        this.form = form;
    }

    private  void loadCommonWords(){
        Scanner sc2 = null;
        try {
            sc2 = new Scanner(new File("common.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc2.hasNextLine()) {
            Scanner s2 = new Scanner(sc2.nextLine());
            while (s2.hasNext()) {
                commonWords.add(s2.next());
            }
        }
    }

    public  void scan2(String path){

        long tStart = System.currentTimeMillis();
        try {
            Files.find(Paths.get(path), 999, (p, bfa) -> bfa.isRegularFile()).forEach(this::scanThroughFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(last) {
            for (Term term :
                    terms.values()) {
                DatabaseGateway.getInstance().saveTerm(term);
            }
            terms.clear();
            newTermId = 0;
            newDocId =0;
            positionCount = 0;
            System.out.println("Terms: " + Long.toString(newTermId));
            System.out.println("Positions: " + Long.toString(positionCount));
            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            System.out.println(tDelta / 1000.0);
        }
    }

    private  void scanThroughFile(Path path){

        createDocumentObject(path.toString());
        int wordIndex = 0;

        Scanner sc2 = null;
        try {
            sc2 = new Scanner(new File(path.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc2.hasNextLine()) {
            Scanner s2 = new Scanner(sc2.nextLine());
            while (s2.hasNext()) {
                String s = s2.next().toLowerCase();

                if(!commonWords.contains(s)){
                    //////////
                    Term presentTerm = terms.get(s);
                    if(presentTerm == null){
                        newTermId ++;
                        currentTermId = newTermId;
                        terms.put(s,new Term(newTermId,s));
                    } else {
                        currentTermId = presentTerm.getId();
                    }
                    positionCount++;
                    DatabaseGateway.getInstance().savePosition(new Position(newDocId, currentTermId, wordIndex));
                }
                wordIndex++;
            }
        }
        filesIndexed++;
        form.setFileCount(filesIndexed);
    }

    private void createDocumentObject(String path) {
        newDocId++;
        Document newDoc = new Document(newDocId, path, LocalDateTime.now());
        DatabaseGateway.getInstance().saveDocument(newDoc);
    }

    public void setLast() {
        last = true;
    }

    public void countFiles(String path) {
        try {
            Files.find(Paths.get(path), 999, (p, bfa) -> bfa.isRegularFile()).forEach(this::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        form.addFilesToBeIndexedCount(files);
    }

    private void add(Path path){
        files++;
    }

    public  void scan3(String path){

        long tStart = System.currentTimeMillis();
        try {
            Files.find(Paths.get(path), 999, (p, bfa) -> bfa.isRegularFile()).forEach(this::countFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(last) {
            terms.clear();
            newTermId = 0;
            positionCount = 0;
            System.out.println("Terms: " + Long.toString(newTermId));
            System.out.println("Positions: " + Long.toString(positionCount));
            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            System.out.println(tDelta / 1000.0);
        }
    }

    private  void countFiles(Path path){
        filesIndexed++;
        int wordIndex = 0;

        Scanner sc2 = null;
        try {
            sc2 = new Scanner(new File(path.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc2.hasNextLine()) {
            Scanner s2 = new Scanner(sc2.nextLine());
            while (s2.hasNext()) {
                String s = s2.next().toLowerCase();
                wordIndex++;
            }
        }
        form.addFilesToBeIndexedCount(wordIndex);
    }
}
