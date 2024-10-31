package duongvanbao.Book.Store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Setter
    private String title;

    @Setter
    private String pageNumber;

    @Setter
    private String weight;

    @Setter
    private double oldPrice;

    @Setter
    private double specialPrice;

    @Setter
    private String description;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @Setter
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    @Setter
    private Language language;
}
