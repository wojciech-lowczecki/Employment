package pl.plh.app.employment.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(doNotUseGetters = true)
@Table(name = "occupation", uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id", "name"})})
@SequenceGenerator(name = "gen_occupation_seq", allocationSize = 1, sequenceName = "occupation_seq")
@Entity
public class Occupation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_occupation_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne(targetEntity = Group.class, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name")
    private String name;
}
