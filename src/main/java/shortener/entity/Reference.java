package shortener.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "reference", schema = "public")
public class Reference implements BaseEntity, Serializable {

    private static final long serialVersionUID = -7539517893214297888L;

    public Reference() {}

    public Reference(String fullRef, String reducedRef, int requestsNumb, long userId) {
        this.fullRef = fullRef;
        this.reducedRef = reducedRef;
        this.requestsNumb = requestsNumb;
        this.userId = userId;
    }

    @Id
    @Column(name = "\"id\"")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Field 'fullRef' can`t be empty!")
    @Size(max = 500, message = "Field 'fullRef' max length is 500!")
    @Column(name = "\"fullRef\"")
    private String fullRef;

    @NotBlank(message = "Field 'reducedRef' can`t be empty!")
    @Column(name = "\"reducedRef\"")
    private String reducedRef;

    @Min(value = 0, message = "Field 'requestsNumb' can`t be empty!")
    @Column(name = "\"requestsNumb\"")
    private int requestsNumb;

    @Min(value = 1, message = "Field 'userId' can`t be empty!")
    @Column(name = "\"userId\"")
    private long userId;

    @ManyToOne
    @JoinColumn(name = "\"userId\"", nullable = false, insertable = false, updatable = false)
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullRef() {
        return fullRef;
    }

    public void setFullRef(String fullRef) {
        this.fullRef = fullRef;
    }

    public String getReducedRef() {
        return reducedRef;
    }

    public void setReducedRef(String reducedRef) {
        this.reducedRef = reducedRef;
    }

    public int getRequestsNumb() {
        return requestsNumb;
    }

    public void setRequestsNumb(int requestsNumb) {
        this.requestsNumb = requestsNumb;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;
        Reference reference = (Reference) o;
        return id == reference.id &&
                requestsNumb == reference.requestsNumb &&
                userId == reference.userId &&
                fullRef.equals(reference.fullRef) &&
                reducedRef.equals(reference.reducedRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullRef, reducedRef, requestsNumb, userId);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.err.println("Error parse Reference to JSON!");
            e.printStackTrace();
            return "{}";
        }
    }

}
