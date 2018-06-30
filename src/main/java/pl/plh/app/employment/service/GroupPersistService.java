package pl.plh.app.employment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Group;
import pl.plh.app.employment.repository.GroupRepository;

import java.util.List;

import static pl.plh.app.employment.service.PersistServiceValidator.checkIdExists;

@Transactional
@Service
public class GroupPersistService {
    @Autowired
    private GroupRepository groupRepo;

    public List<Group> getAllGroups() {
        return groupRepo.findAllByOrderByName();
    }

    public Group getGroup(final Long id) {
        return groupRepo.findById(id).orElseThrow(() -> new NoSuchObjectException(Group.class, id));
    }

    // For updating updateGroup() is a better choice
    public Group saveGroup(final Group group) {
        return groupRepo.save(group);
    }

    public Group updateGroup(final Group group) {
        checkIdExists(groupRepo, group.getId(), Group.class);
        return groupRepo.save(group);
    }

    public void deleteGroup(final Long id) {
        checkIdExists(groupRepo, id, Group.class);
        groupRepo.deleteById(id);
    }
}
