package BE;

import org.mongodb.morphia.annotations.*;

@Entity("Term")
public class Term {

    @Id
    Long id;
    @Indexed
    String value;

    public Term() {
    }

    public Term(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Term(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Term)) {
            return false;
        }

        Term that = (Term) other;

        // Custom equality check here.
        return this.value.equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return 31 + ((value == null) ? 0 : value.hashCode());

    }
}
