package BE;

import org.mongodb.morphia.annotations.*;

@Entity("Position")
@Indexes(@Index(fields = { @Field("termId"), @Field("docId"), @Field("position")}, options = @IndexOptions(unique = true)))

public class Position {

    @Id
    private String id;

    private long docId;

    private Long termId;

    private int position;

    public Position(long docId, Long termId, int position) {
        this.docId = docId;
        this.termId = termId;
        this.position = position;
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
