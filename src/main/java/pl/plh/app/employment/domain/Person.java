package pl.plh.app.employment.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(doNotUseGetters = true)
@Table(name = "person")
@SequenceGenerator(name = "gen_person_seq", allocationSize = 1, sequenceName = "person_seq")
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_person_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 11, max = 11)
    @Column(name = "pesel", unique = true, columnDefinition = "char(11)")
    private String pesel;

    @NotNull
    @Past
    @Column(name = "birth_date")
    LocalDate birthDate;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender")
    private Gender gender;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "last_name")
    private String lastName;

    @ManyToOne(targetEntity = Location.class, optional = false)
    @JoinColumn(name = "location_id")
    private Location location;

    @Setter
    @Size(max = 3) // 0 when person is unemployed
    @ManyToMany(targetEntity = Occupation.class)
    @JoinTable(
            name = "person_x_occupation",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "occupation_id", referencedColumnName = "id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"person_id", "occupation_id"})}
    )
    private List<Occupation> occupations = new ArrayList<>();
}
