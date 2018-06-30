package pl.plh.app.employment.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.*;
import pl.plh.app.employment.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Profile("demo")
@Transactional
@Log4j2
@Component
public class DemoDataGenerator {
    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private VoivodeshipRepository voivodeshipRepo;

    @Autowired
    private OccupationRepository occupationRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private PersonRepository personRepo;

    private final Map<String, Voivodeship> voivodeships = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Group> groups = new HashMap<>();
    private final Map<String, Occupation> occupations = new HashMap<>();

    public void createAll() {
       createVoivodeships();
       createLocations();
       createGroups();
       createOccupations();
       createPersons();
    }

    private void createVoivodeship(final String shortcut, final String name) {
        voivodeships.put(shortcut, voivodeshipRepo.save(new Voivodeship(null, name)));
    }

    private void createLocation(final String shortcut, String voivodeshipShortcut, final String name, long population) {
        Location location = new Location(null, voivodeships.get(voivodeshipShortcut), name, population);
        locations.put(shortcut, locationRepo.save(location));
    }

    private void createGroup(final String shortcut, final String name) {
        groups.put(shortcut, groupRepo.save(new Group(null, name)));
    }

    private void createOccupation(final String shortcut, String groupShortcut, final String name) {
        occupations.put(shortcut, occupationRepo.save(new Occupation(null, groups.get(groupShortcut), name)));
    }

    private void createPerson(String pesel, String birthDate, Gender gender, String firstName, String lastName,
                              String locationShortcut, List<String> occupationShortcuts) {
        List<Occupation> occupationList = new ArrayList<>();
        for (String shortcut : occupationShortcuts) {
            occupationList.add(occupations.get(shortcut));
        }
        Person person = new Person(null, pesel, LocalDate.parse(birthDate), gender, firstName, lastName,
                                   locations.get(locationShortcut), occupationList);
        personRepo.save(person);
    }

    private void createVoivodeships() {
        log.info("Creating demo voivodeships");

        createVoivodeship("doln", "dolnośląskie");
        createVoivodeship("kuja", "kujawsko-pomorskie");
        createVoivodeship("lube", "lubelskie");
        createVoivodeship("lubu", "lubuskie");
        createVoivodeship("lodz", "łódzkie");
        createVoivodeship("malo", "małopolskie");
        createVoivodeship("mazo", "mazowieckie");
        createVoivodeship("opol", "opolskie");
        createVoivodeship("podk", "podkarpackie");
        createVoivodeship("podl", "podlaskie");
        createVoivodeship("pomo", "pomorskie");
        createVoivodeship("slas", "śląskie");
        createVoivodeship("swie", "świętokrzyskie");
        createVoivodeship("warm", "warmińsko-mazurskie");
        createVoivodeship("wiel", "wielkopolskie");
        createVoivodeship("zach", "zachodniopomorskie");
    }

    private void createGroups() {
        log.info("Creating demo groups");

        createGroup("kier", "Przedstawiciele władz publicznych, wyżsi urzędnicy i kierownicy");
        createGroup("spec", "Specjaliści");
        createGroup("tech", "Technicy i inny średni personel");
        createGroup("biur", "Pracownicy biurowi");
        createGroup("uslu", "Pracownicy usług i sprzedawcy");
        createGroup("roln", "Rolnicy, ogrodnicy, leśnicy i rybacy");
        createGroup("robo", "Robotnicy przemysłowi i rzemieślnicy");
        createGroup("oper", "Operatorzy i monterzy maszyn i urządzeń");
        createGroup("pros", "Pracownicy wykonujący prace proste");
        createGroup("sily", "Siły zbrojne");
    }

    private void createLocations() {
        log.info("Creating demo locations");

        createLocation("wroclaw", "doln", "Wrocław", 630000);
        createLocation("bydgoszcz", "kuja", "Bydgoszcz", 354000);
        createLocation("lublin", "lube", "Lublin", 341000);
        createLocation("zielona-gora", "lubu", "Zielona Góra", 140000);
        createLocation("lodz", "lodz", "Łódź", 697000);
        createLocation("krakow", "malo", "Kraków", 766000);
        createLocation("warszawa", "mazo", "Warszawa", 1754000);
        createLocation("opole", "opol", "Opole", 129000);
        createLocation("rzeszow", "podk", "Rzeszów", 189000);
        createLocation("bialystok", "podl", "Białystok", 297000);
        createLocation("gdansk", "pomo", "Gdańsk", 464000);
        createLocation("katowice", "slas", "Katowice", 299000);
        createLocation("kielce", "swie", "Kielce", 198000);
        createLocation("olsztyn", "warm", "Olsztyn", 173000);
        createLocation("poznan", "wiel", "Poznań", 541000);
        createLocation("szczecin", "zach", "Szczecin", 405000);
    }

    private void createOccupations() {
        log.info("Creating demo occupations");
        
        createOccupation("prezes", "kier", "Prezes");
        createOccupation("glowny-ksiegowy", "kier", "Główny księgowy");
        createOccupation("kierownik-projektu", "kier", "Kierownik projektu");
        createOccupation("tester", "spec", "Tester oprogramowania komputerowego");
        createOccupation("programista", "spec", "Programista aplikacji");
        createOccupation("architekt-systemow", "spec", "Architekt systemów teleinformatycznych");
        createOccupation("analityk-systemow", "spec", "Analityk systemów teleinformatycznych");
        createOccupation("analityk-biznesowy", "spec", "Analityk biznesowy");
        createOccupation("admin-systemow", "spec", "Administrator systemów komputerowych");
        createOccupation("spec-kadr", "spec", "Specjalista do spraw kadr");
        createOccupation("sekretarka", "biur", "Sekretarka");
        createOccupation("portier", "uslu", "Portier");
    }

    private void createPersons() {
        log.info("Creating demo persons");

        createPerson("00000000001", "2000-12-31", Gender.MALE, "Jan", "Kowalski", "bydgoszcz",
                     Arrays.asList("programista", "tester"));
        createPerson("00000000002", "1991-12-31", Gender.FEMALE, "Daria", "Nowak", "bydgoszcz",
                     Arrays.asList("architekt-systemowy", "analityk-systemowy"));
        createPerson("00000000003", "1982-12-31", Gender.FEMALE, "Natalia", "Rolska", "bydgoszcz",
                     Arrays.asList("glowny-ksiegowy"));
        createPerson("00000000004", "1973-12-31", Gender.MALE, "Ryszard", "Jasiński", "bydgoszcz",
                     Arrays.asList("prezes"));
        createPerson("00000000005", "1994-12-31", Gender.FEMALE, "Irena", "Oleska", "bydgoszcz",
                     Arrays.asList("sekretarka", "spec-kadr"));
        createPerson("00000000006", "1985-12-31", Gender.MALE, "Grzegorz", "Adelski", "bydgoszcz",
                     Arrays.asList("portier"));
        createPerson("00000000007", "1976-12-31", Gender.FEMALE, "Róża", "Kwiatkowska", "bydgoszcz",
                     Arrays.asList("programista"));
        createPerson("00000000008", "1997-12-31", Gender.MALE, "Kamil", "Malicki", "bydgoszcz",
                     Arrays.asList("admin-systemow"));
        createPerson("00000000009", "1988-12-31", Gender.FEMALE, "Paula", "Dobra", "bydgoszcz",
                     Arrays.asList("kier", "analityk-biznesowy"));
        createPerson("00000000010", "1979-12-31", Gender.MALE, "Egon", "Fircyk", "bydgoszcz",
                     Arrays.asList("programista"));
        createPerson("00000000011", "2000-12-31", Gender.MALE, "Jan", "Nowacki", "warszawa",
                     Arrays.asList("programista", "tester"));
        createPerson("00000000012", "1991-12-31", Gender.FEMALE, "Ewelina", "Nowak", "warszawa",
                     Arrays.asList("architekt-systemowy"));
        createPerson("00000000013", "1982-12-31", Gender.FEMALE, "Helena", "Rolska", "warszawa",
                     Arrays.asList("glowny-ksiegowy"));
        createPerson("00000000014", "1973-12-31", Gender.FEMALE, "Zuzanna", "Oleska", "warszawa",
                     Arrays.asList("sekretarka", "spec-kadr"));
        createPerson("00000000015", "1994-12-31", Gender.MALE, "Grzegorz", "Pulecki", "warszawa",
                     Arrays.asList("portier"));
        createPerson("00000000016", "1985-12-31", Gender.FEMALE, "Róża", "Wazonkowska", "warszawa",
                     Arrays.asList("programista"));
        createPerson("00000000017", "1976-12-31", Gender.MALE, "Jakub", "Malicki", "warszawa",
                     Arrays.asList("admin-systemow"));
        createPerson("00000000018", "1997-12-31", Gender.MALE, "Stanisław", "Fircyk", "warszawa",
                     Arrays.asList("programista"));
        createPerson("00000000019", "1988-12-31", Gender.MALE, "Ludwik", "Kowalski", "warszawa",
                     Arrays.asList("tester"));
        createPerson("00000000021", "1977-12-31", Gender.MALE, "Piotr", "Witkowski", "warszawa",
                     Arrays.asList("prezes"));
    }
}
