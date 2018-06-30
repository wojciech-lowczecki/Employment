package pl.plh.app.employment.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(doNotUseGetters = true)
@Table(name = "location")
@SequenceGenerator(name = "gen_location_seq", allocationSize = 1, sequenceName = "location_seq")
@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_location_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne(targetEntity = Voivodeship.class, optional = false)
    @JoinColumn(name = "voivodeship_id")
    private Voivodeship voivodeship;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name")
    private String name;

    @NotNull
    @Positive
    @Column(name = "population")
    private Long population;
}
